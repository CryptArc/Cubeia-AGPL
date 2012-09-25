/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.tournament.activator;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.mtt.MttFactory;
import com.cubeia.firebase.api.mtt.activator.ActivatorContext;
import com.cubeia.firebase.api.mtt.activator.CreationParticipant;
import com.cubeia.firebase.api.mtt.lobby.MttLobbyObject;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.games.poker.tournament.configuration.provider.TournamentScheduleProvider;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.cubeia.firebase.io.protocol.Enums.TournamentAttributes.NAME;
import static com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes.IDENTIFIER;
import static com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes.STATUS;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;

public class TournamentScanner implements PokerActivator, Runnable {

    public static final long INITIAL_TOURNAMENT_CHECK_DELAY = 5000;

    public static final long TOURNAMENT_CHECK_INTERVAL = 10000;

    public static final long SHUTDOWN_WAIT_TIME = 10000;

    public static final long DELAY_BEFORE_REMOVING_FINISHED_TOURNAMENTS = 30; // Seconds

    private static final transient Logger log = Logger.getLogger(TournamentScanner.class);

    protected MttFactory factory;

    protected ActivatorContext context;

    private final SitAndGoConfigurationProvider sitAndGoConfigurationProvider;

    private final TournamentScheduleProvider tournamentScheduleProvider;

    protected ScheduledExecutorService executorService = null;

    private ScheduledFuture<?> checkTablesFuture = null;

    /**
     * Lock to synchronize reading and creation of tournament instances
     */
    protected final Object LOCK = new Object();

    private SystemTime dateFetcher;

    @Inject
    public TournamentScanner(SitAndGoConfigurationProvider sitAndGoConfigurationProvider, TournamentScheduleProvider tournamentScheduleProvider,
                             SystemTime dateFetcher) {
        this.sitAndGoConfigurationProvider = sitAndGoConfigurationProvider;
        this.tournamentScheduleProvider = tournamentScheduleProvider;
        this.dateFetcher = dateFetcher;
    }

    /*------------------------------------------------

       LIFECYCLE METHODS

    ------------------------------------------------*/

    public void init(ActivatorContext context) throws SystemException {
        this.context = context;
    }


    public void start() {
        synchronized (LOCK) {
            if (checkTablesFuture != null) {
                log.warn("Start called on running activator.");
            }

            executorService = Executors.newScheduledThreadPool(1);
            checkTablesFuture = executorService.scheduleAtFixedRate(this, INITIAL_TOURNAMENT_CHECK_DELAY, TOURNAMENT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    public void setMttFactory(MttFactory factory) {
        this.factory = factory;
    }

    public void stop() {
        synchronized (LOCK) {
            if (checkTablesFuture == null) {
                // already stopped
                return;
            }

            checkTablesFuture.cancel(false);
            executorService.shutdown();

            try {
                executorService.awaitTermination(SHUTDOWN_WAIT_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("interrupted waiting for checktable executor to terminate.", e);
            }

            if (!executorService.isTerminated()) {
                log.error("executor service failed to terminate on shutdown.");
            }

            checkTablesFuture = null;
            executorService = null;
        }
    }

    public void destroy() {
    }


    /*------------------------------------------------

       ABSTRACT METHODS

    ------------------------------------------------*/


    /*------------------------------------------------

       PRIVATE & LOGIC METHODS

    ------------------------------------------------*/

    /**
     * Checks for finished tournaments and schedules them to be removed.
     */
    protected Set<Integer> checkDestroyTournaments() {
        MttLobbyObject[] tournamentInstances = factory.listTournamentInstances();
        Set<Integer> removed = new HashSet<Integer>();

        for (MttLobbyObject t : tournamentInstances) {
            String status = getStringAttribute(t, STATUS.name());

            if (status.equalsIgnoreCase(FINISHED.name()) || status.equalsIgnoreCase(CANCELLED.name())) {
                executorService.schedule(new Destroyer(t.getTournamentId()), DELAY_BEFORE_REMOVING_FINISHED_TOURNAMENTS, TimeUnit.SECONDS);
                removed.add(t.getTournamentId());
            }
        }
        return removed;
    }

    /*------------------------------------------------

       PUBLIC ACTIVATOR INTERFACE METHODS

    ------------------------------------------------*/

    public void checkTournamentsNow() {
        synchronized (LOCK) {
            checkTournaments();
            checkDestroyTournaments();
        }
    }


    /*------------------------------------------------

       PRIVATE METHODS

    ------------------------------------------------*/


    private void createSitAndGo(SitAndGoConfiguration sitAndGo, ActivatorContext context) {
        factory.createMtt(context.getMttId(), sitAndGo.getConfiguration().getName(), createParticipant(sitAndGo));
    }

    private void createScheduledTournament(ScheduledTournamentInstance configuration) {
        factory.createMtt(context.getMttId(), configuration.getName(), createParticipant(configuration));
    }

    private CreationParticipant createParticipant(SitAndGoConfiguration configuration) {
        return new SitAndGoCreationParticipant(configuration);
    }

    private CreationParticipant createParticipant(ScheduledTournamentInstance configuration) {
        return new ScheduledTournamentCreationParticipant(configuration);
    }

    private void checkTournaments() {
        checkSitAndGos();
        checkScheduledTournaments();
    }

    private void checkScheduledTournaments() {
        log.info("Checking scheduled tournaments.");
        Collection<ScheduledTournamentConfiguration> tournamentSchedule = tournamentScheduleProvider.getTournamentSchedule();

        Set<String> existingTournaments = getExistingTournaments();

        for (ScheduledTournamentConfiguration configuration : tournamentSchedule) {
            TournamentSchedule schedule = configuration.getSchedule();
            DateTime nextAnnounceTime = schedule.getNextAnnounceTime(dateFetcher.date());
            if (dateFetcher.date().isAfter(nextAnnounceTime)) {
                ScheduledTournamentInstance instance = configuration.spawnConfigurationForNextInstance(schedule.getNextStartTime(dateFetcher.date()));
                if (!existingTournaments.contains(instance.getIdentifier())) {
                    createScheduledTournament(instance);
                }
            }
        }
    }

    private Set<String> getExistingTournaments() {
        Set<String> existingTournaments = newHashSet();
        MttLobbyObject[] tournamentInstances = factory.listTournamentInstances();
        for (MttLobbyObject tournament : tournamentInstances) {
            String identifier = getStringAttribute(tournament, IDENTIFIER.name());
            if (!isNullOrEmpty(identifier)) {
                log.debug("Found tournament with identifier " + identifier);
                existingTournaments.add(identifier);
            }
        }
        return existingTournaments;
    }

    private String getStringAttribute(MttLobbyObject tournament, String attributeName) {
        AttributeValue value = tournament.getAttributes().get(attributeName);

        if (value == null || value.getType() != AttributeValue.Type.STRING) {
            return "";
        }
        return value.getStringValue();
    }

    private void checkSitAndGos() {
        log.debug("Checking sit and gos.");
        MttLobbyObject[] tournamentInstances = factory.listTournamentInstances();
        Set<String> missingTournaments = new HashSet<String>();
        Map<String, SitAndGoConfiguration> requestedConfigurations = mapToName(sitAndGoConfigurationProvider.getConfigurations());
        missingTournaments.addAll(requestedConfigurations.keySet());

        for (MttLobbyObject t : tournamentInstances) {
            String status = getStringAttribute(t, STATUS.name());
            if (status.equalsIgnoreCase(REGISTERING.name())) {
                String name = getStringAttribute(t, NAME.name());
                missingTournaments.remove(name);
            }
        }

        for (String configuration : missingTournaments) {
            createSitAndGo(requestedConfigurations.get(configuration), context);
        }
    }

    private Map<String, SitAndGoConfiguration> mapToName(Collection<SitAndGoConfiguration> configurations) {
        Map<String, SitAndGoConfiguration> map = Maps.newHashMap();
        for (SitAndGoConfiguration sitAndGo : configurations) {
            map.put(sitAndGo.getConfiguration().getName(), sitAndGo);
        }
        return map;
    }

    protected class Destroyer implements Runnable {

        private final int mttid;

        public Destroyer(int mttid) {
            this.mttid = mttid;
        }

        public void run() {
            factory.destroyMtt(PokerTournamentActivatorImpl.POKER_GAME_ID, mttid);
        }
    }

    public void run() {
        try {
            synchronized (LOCK) {
                checkTournamentsNow();
            }
        } catch (Throwable t) {
            // Catching all errors so that the scheduler won't take the hit (and die).
            log.fatal("Failed checking tournaments: " + t, t);
        }
    }
}
