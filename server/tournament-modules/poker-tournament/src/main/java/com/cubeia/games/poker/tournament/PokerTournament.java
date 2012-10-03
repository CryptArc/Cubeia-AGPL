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

import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.UnseatPlayersMttAction;
import com.cubeia.firebase.api.action.mtt.MttDataAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.lobby.DefaultMttAttributes;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.mtt.model.MttRegisterResponse;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.seating.SeatingContainer;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.tables.Move;
import com.cubeia.firebase.api.mtt.support.tables.TableBalancer;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.io.protocol.TournamentOut;
import com.cubeia.games.poker.tournament.configuration.TournamentTableSettings;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsLevel;
import com.cubeia.games.poker.tournament.configuration.lifecycle.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.cubeia.games.poker.tournament.util.ProtocolFactory;
import com.cubeia.poker.timing.TimingFactory;
import org.apache.log4j.Logger;
import org.joda.time.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;

public class PokerTournament implements Serializable {

    private static final Logger log = Logger.getLogger(PokerTournament.class);

    private PokerTournamentState pokerState;

    private PokerTournamentUtil util = new PokerTournamentUtil();

    private TournamentLifeCycle tournamentLifeCycle;

    private SystemTime dateFetcher;

    private transient MTTStateSupport state;

    private transient MttInstance instance;

    private transient TournamentAssist mttSupport;

    private transient MttNotifier notifier;

    private static final Long STARTING_CHIPS = 100000L;

    public PokerTournament(PokerTournamentState pokerState, SystemTime dateFetcher, TournamentLifeCycle tournamentLifeCycle) {
        this.pokerState = pokerState;
        this.dateFetcher = dateFetcher;
        this.tournamentLifeCycle = tournamentLifeCycle;
    }

    public void injectTransientDependencies(MttInstance instance, TournamentAssist support, MTTStateSupport state, MttNotifier notifier) {
        this.instance = instance;
        this.mttSupport = support;
        this.state = state;
        this.notifier = notifier;
    }

    public void processRoundReport(MttRoundReportAction action) {
        if (log.isDebugEnabled()) {
            log.debug("Process round report from table[" + action.getTableId() + "] Report: " + action);
        }

        PokerTournamentRoundReport report = (PokerTournamentRoundReport) action.getAttachment();

        updateBalances(report);
        Set<Integer> playersOut = getPlayersOut(report);
        log.info("Players out of tournament[" + instance.getId() + "] : " + playersOut);
        sendTournamentOutToPlayers(playersOut, instance);
        handlePlayersOut(action.getTableId(), playersOut);
        boolean tableClosed = balanceTables(action.getTableId());

        if (isTournamentFinished()) {
            handleFinishedTournament();
        } else {
            if (!tableClosed) {
                increaseBlindsIfNeeded(report.getCurrentBlindsLevel(), action.getTableId());
                startNextRoundIfPossible(action.getTableId());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Remaining players: " + state.getRemainingPlayerCount() + " Remaining tables: " + state.getTables());
        }
        updateRemainingPlayerCount();
    }

    private void increaseBlindsIfNeeded(BlindsLevel currentBlindsLevel, int tableId) {
        if (currentBlindsLevel.getBigBlindAmount() < pokerState.getBigBlindAmount()) {
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(pokerState.getCurrentBlindsLevel());
            instance.getMttNotifier().notifyTable(tableId, action);
        }
    }

    public void handleTablesCreated() {
        if (pokerState.allTablesHaveBeenCreated(state.getTables().size())) {
            mttSupport.seatPlayers(state, createInitialSeating());
            scheduleTournamentStart();
        }
    }

    private void updateBalances(PokerTournamentRoundReport report) {
        for (Map.Entry<Integer, Long> balance : report.getBalances()) {
            pokerState.setBalance(balance.getKey(), balance.getValue());
        }
    }

    private Set<Integer> getPlayersOut(PokerTournamentRoundReport report) {
        Set<Integer> playersOut = new HashSet<Integer>();

        for (Map.Entry<Integer, Long> balance : report.getBalances()) {
            if (balance.getValue() <= 0) {
                playersOut.add(balance.getKey());
            }
        }

        log.debug("These players have 0 balance and are out: " + playersOut);
        return playersOut;
    }


    private void handlePlayersOut(int tableId, Set<Integer> playersOut) {
        mttSupport.unseatPlayers(state, tableId, playersOut, UnseatPlayersMttAction.Reason.OUT);
    }

    private Long getStartingChips() {
        return STARTING_CHIPS;
    }

    private void updateRemainingPlayerCount() {
        LobbyAttributeAccessor lobby = instance.getLobbyAccessor();
        lobby.setIntAttribute(DefaultMttAttributes.ACTIVE_PLAYERS.name(), instance.getState().getRemainingPlayerCount());
    }

    private void sendTournamentOutToPlayers(Collection<Integer> playersOut, MttInstance instance) {
        for (int pid : playersOut) {
            TournamentOut packet = new TournamentOut();
            packet.position = instance.getState().getRemainingPlayerCount();
            packet.player = pid;
            log.debug("Telling player " + pid + " that he finished in position " + packet.position);
            MttDataAction action = ProtocolFactory.createMttAction(packet, pid, instance.getId());
            notifier.notifyPlayer(pid, action);
        }
    }

    private void handleFinishedTournament() {
        log.info("Tournament [" + instance.getId() + ":" + instance.getState().getName() + "] was finished.");
        util.setTournamentStatus(instance, PokerTournamentStatus.FINISHED);

        // Find winner
        MTTStateSupport state = (MTTStateSupport) instance.getState();
        Integer table = state.getTables().iterator().next();
        Collection<Integer> winners = state.getPlayersAtTable(table);
        sendTournamentOutToPlayers(winners, instance);
    }

    private boolean isTournamentFinished() {
        return state.getRemainingPlayerCount() == 1;
    }

    private void startNextRoundIfPossible(int tableId) {
        if (state.getPlayersAtTable(tableId).size() > 1) {
            mttSupport.sendRoundStartActionToTables(state, singleton(tableId));
        }
    }

    /**
     * Tries to balance the tables by moving one or more players from this table to other
     * tables.
     *
     * @param tableId
     * @return <code>true</code> if the table was closed
     */
    private boolean balanceTables(int tableId) {
        TableBalancer balancer = new TableBalancer();
        List<Move> moves = balancer.calculateBalancing(createTableToPlayerMap(), state.getSeats(), tableId);
        return applyBalancing(moves, tableId);
    }

    /**
     * Applies balancing by moving players to the destination table.
     *
     * @param moves
     * @param sourceTableId the table we are moving player from
     * @return true if table is closed
     */
    private boolean applyBalancing(List<Move> moves, int sourceTableId) {
        Set<Integer> tablesToStart = new HashSet<Integer>();

        for (Move move : moves) {
            int tableId = move.getDestinationTableId();
            int playerId = move.getPlayerId();

            mttSupport.movePlayer(state, playerId, tableId, -1, UnseatPlayersMttAction.Reason.BALANCING, pokerState.getPlayerBalance(playerId));
            // Move the player, we don't care which seat he gets put at, so set
            // it to -1.
            Collection<Integer> playersAtDestinationTable = state.getPlayersAtTable(tableId);
            if (playersAtDestinationTable.size() == 2) {
                // There was only one player at the table before we moved this
                // player there, start a new round.
                tablesToStart.add(tableId);
            }
        }

        if (!tablesToStart.isEmpty()) {
            log.debug("Sending explicit start to tables[" + tablesToStart.toArray() + "] due to low number of players.");
            mttSupport.sendRoundStartActionToTables(state, tablesToStart);
        }

        return closeTableIfEmpty(sourceTableId);
    }

    private boolean closeTableIfEmpty(int tableId) {
        if (state.getPlayersAtTable(tableId).isEmpty()) {
            mttSupport.closeTables(state, singleton(tableId));
            return true;
        }

        return false;
    }

    /**
     * Creates a map mapping tableId to a collection of playerIds of the players
     * sitting at the table.
     *
     * @return the map
     */
    private Map<Integer, Collection<Integer>> createTableToPlayerMap() {
        Map<Integer, Collection<Integer>> map = new HashMap<Integer, Collection<Integer>>();

        // Go through the tables.
        for (Integer tableId : state.getTables()) {
            List<Integer> players = new ArrayList<Integer>();
            // Add all players at this table.
            players.addAll(state.getPlayersAtTable(tableId));

            if (players.size() > 0) {
                // Put it in the map.
                map.put(tableId, players);
            }
        }
        return map;
    }

    private void scheduleTableCreation() {
        if (tournamentLifeCycle.shouldScheduleTournamentStart(pokerState.getStatus(), dateFetcher.date())) {
            MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.START_TOURNAMENT);
            long timeToTournamentStart = tournamentLifeCycle.getTimeToTournamentStart(dateFetcher.date());
            log.debug("Scheduling tournament start in " + Duration.millis(timeToTournamentStart).getStandardMinutes() + " minutes, for tournament " + instance);
            instance.getScheduler().scheduleAction(action, timeToTournamentStart);
        } else {
            log.debug("Won't schedule tournament start because the life cycle says no.");
        }
    }

    private void scheduleRegistrationOpening() {
        MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.OPEN_REGISTRATION);
        long timeToRegistrationStart = tournamentLifeCycle.getTimeToRegistrationStart(dateFetcher.date());
        log.debug("Scheduling registration opening in " + timeToRegistrationStart + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(action, timeToRegistrationStart);
    }

    private void scheduleTournamentStart() {
        MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.SEND_START_TO_TABLES);
        log.debug("Scheduling round start in " + 1000 + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(action, 1000);
    }

    private void scheduleNextBlindsLevel() {
        long timeToNextLevel = pokerState.getBlindsStructure().getTimeToNextLevel();
        log.debug("Scheduling next blinds level in " + timeToNextLevel + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(new MttObjectAction(instance.getId(), TournamentTrigger.INCREASE_BLINDS), timeToNextLevel);
    }

    private Collection<SeatingContainer> createInitialSeating() {
        Collection<MttPlayer> players = state.getPlayerRegistry().getPlayers();
        Set<Integer> tableIds = state.getTables();
        List<SeatingContainer> initialSeating = new ArrayList<SeatingContainer>();
        Integer[] tableIdArray = new Integer[tableIds.size()];
        tableIds.toArray(tableIdArray);

        int i = 0;
        for (MttPlayer player : players) {
            pokerState.setBalance(player.getPlayerId(), getStartingChips());
            initialSeating.add(createSeating(player.getPlayerId(), tableIdArray[i++ % tableIdArray.length]));
        }

        return initialSeating;
    }

    private SeatingContainer createSeating(int playerId, int tableId) {
        return new SeatingContainer(playerId, tableId, getStartingChips());
    }

    public PokerTournamentState getPokerTournamentState() {
        return pokerState;
    }

    private void setTournamentStatus(PokerTournamentStatus status) {
        instance.getLobbyAccessor().setStringAttribute(PokerTournamentLobbyAttributes.STATUS.name(), status.name());
        pokerState.setStatus(status);
    }

    private void addJoinedTimestamps() {
        if (state.getRegisteredPlayersCount() == 1) {
            pokerState.setFirstRegisteredTime(System.currentTimeMillis());

        } else if (state.getRegisteredPlayersCount() == state.getMinPlayers()) {
            pokerState.setLastRegisteredTime(System.currentTimeMillis());
        }
    }

    private void startTournament() {
        long registrationElapsedTime = pokerState.getLastRegisteredTime() - pokerState.getFirstRegisteredTime();
        log.debug("Starting tournament [" + instance.getId() + " : " + instance.getState().getName() + "]. Registration time was " + registrationElapsedTime + " ms");

        setTournamentStatus(PokerTournamentStatus.RUNNING);
        int tablesToCreate = state.getRegisteredPlayersCount() / state.getSeats();

        // Not sure why we do this?
        if (state.getRegisteredPlayersCount() % state.getSeats() > 0) {
            tablesToCreate++;
        }
        pokerState.setTablesToCreate(tablesToCreate);
        TournamentTableSettings settings = getTableSettings();
        log.debug("Creating tables for tournament [" + instance + "] . State.getID: " + state.getId());
        mttSupport.createTables(state, tablesToCreate, "mtt", settings);
    }

    private boolean tournamentShouldStart() {
        return tournamentLifeCycle.shouldStartTournament(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private boolean tournamentShouldBeCancelled() {
        return tournamentLifeCycle.shouldCancelTournament(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private TournamentTableSettings getTableSettings() {
        log.debug("Getting table settings. Small = " + pokerState.getSmallBlindAmount() + " big = " + pokerState.getBigBlindAmount());
        TournamentTableSettings settings = new TournamentTableSettings(pokerState.getSmallBlindAmount(), pokerState.getBigBlindAmount());
        settings.setTimingProfile(TimingFactory.getRegistry().getTimingProfile(pokerState.getTiming()));
        return settings;
    }
    
    public void playerRegistered(MttRegistrationRequest request) {
        addJoinedTimestamps();

        if (tournamentShouldStart()) {
            startTournament();
        }
    }

    public void playerUnregistered(int pid) {
        // TODO Add support for unregistration.
    }

    public MttRegisterResponse register(MttRegistrationRequest request) {
        log.info("Checking if " + request + " is allowed to register.");
        if (pokerState.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.ALLOWED;
        } else {
            return MttRegisterResponse.ALLOWED;
        }
    }

    public MttRegisterResponse unregister(int pid) {
        if (pokerState.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.DENIED;
        } else {
            return MttRegisterResponse.ALLOWED;
        }
    }

    public void tournamentCreated() {
        if (tournamentShouldBeCancelled()) {
            cancelTournament();
        } else if (tournamentLifeCycle.shouldOpenRegistration(dateFetcher.date())) {
            openRegistration();
            scheduleTableCreation();
        } else if (tournamentLifeCycle.shouldScheduleRegistrationOpening(pokerState.getStatus(), dateFetcher.date())) {
            scheduleRegistrationOpening();
        }
    }

    public void handleTrigger(TournamentTrigger trigger) {
        switch (trigger) {
            case START_TOURNAMENT:
                if (enoughPlayers()) {
                    startTournament();
                } else {
                    cancelTournament();
                }
                break;
            case OPEN_REGISTRATION:
                openRegistration();
                scheduleTableCreation();
                break;
            case SEND_START_TO_TABLES:
                sendRoundStartToAllTables();
                scheduleNextBlindsLevel();
                break;
            case INCREASE_BLINDS:
                increaseBlindsLevel();
                scheduleNextBlindsLevel();
                break;
        }
    }

    private boolean enoughPlayers() {
        return state.getRegisteredPlayersCount() >= state.getMinPlayers();
    }

    private void increaseBlindsLevel() {
        pokerState.increaseBlindsLevel();
    }

    private void openRegistration() {
        log.debug("Opening registration.");
        setTournamentStatus(PokerTournamentStatus.REGISTERING);
    }

    private void cancelTournament() {
        setTournamentStatus(PokerTournamentStatus.CANCELLED);
        refundPlayers();
    }

    private void refundPlayers() {
        // TODO: Refund players.
        log.warn("Should refund players here!");
    }

    private void sendRoundStartToAllTables() {
        log.debug("Sending round start to all tables.");
        mttSupport.sendRoundStartActionToTables(state, state.getTables());
    }
}
