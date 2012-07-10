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

package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import com.cubeia.games.poker.tournament.activator.TournamentTableSettings;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import com.cubeia.poker.timing.TimingFactory;
import org.apache.log4j.Logger;

public class PokerTournamentListener implements PlayerListener {

    private static transient Logger log = Logger.getLogger(PokerTournamentListener.class);

    private transient PokerTournamentProcessor pokerTournamentProcessor;

    private transient PokerTournamentUtil util = new PokerTournamentUtil();

    public PokerTournamentListener(PokerTournamentProcessor pokerTournamentProcessor) {
        this.pokerTournamentProcessor = pokerTournamentProcessor;
    }

    public void playerRegistered(MttInstance instance, MttRegistrationRequest request) {
        MTTStateSupport state = (MTTStateSupport) instance.getState();
        addJoinedTimestamps(state);

        if (tournamentShouldStart(state)) {
            startTournament(instance, state);
        }
    }

    private void addJoinedTimestamps(MTTStateSupport state) {
        PokerTournamentState tournamentState = util.getPokerState(state);
        if (state.getRegisteredPlayersCount() == 1) {
            tournamentState.setFirstRegisteredTime(System.currentTimeMillis());

        } else if (state.getRegisteredPlayersCount() == state.getMinPlayers()) {
            tournamentState.setLastRegisteredTime(System.currentTimeMillis());
        }
    }

    private void startTournament(MttInstance instance, MTTStateSupport state) {
        PokerTournamentState pokerState = util.getPokerState(instance);
        setInitialBlinds(pokerState);

        long registrationElapsedTime = pokerState.getLastRegisteredTime() - pokerState.getFirstRegisteredTime();
        log.debug("Starting tournament [" + instance.getId() + " : " + instance.getState().getName() + "]. Registration time was " + registrationElapsedTime + " ms");

        util.setTournamentStatus(instance, PokerTournamentStatus.RUNNING);
        int tablesToCreate = state.getRegisteredPlayersCount() / state.getSeats();
        // Not sure why we do this?
        if (state.getRegisteredPlayersCount() % state.getSeats() > 0) {
            tablesToCreate++;
        }
        pokerState.setTablesToCreate(tablesToCreate);
        TournamentTableSettings settings = getTableSettings(pokerState);
        pokerTournamentProcessor.createTables(state, tablesToCreate, "mtt", settings);
    }

    private void setInitialBlinds(PokerTournamentState pokerState) {
        // TODO: Make configurable.
        log.info("Setting initial blinds. (sb = 10, bb = 20).");
        pokerState.setSmallBlindAmount(10);
        pokerState.setBigBlindAmount(20);
    }

    private boolean tournamentShouldStart(MTTStateSupport state) {
        return state.getRegisteredPlayersCount() == state.getMinPlayers();
    }

    public void playerUnregistered(MttInstance instance, int pid) {
        // TODO Add support for unregistration.
    }

    private TournamentTableSettings getTableSettings(PokerTournamentState state) {
        TournamentTableSettings settings = new TournamentTableSettings(state.getSmallBlindAmount(), state.getBigBlindAmount());
        settings.setTimingProfile(TimingFactory.getRegistry().getTimingProfile(state.getTiming()));
        return settings;
    }
}
