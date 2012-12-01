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

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TournamentId;
import com.cubeia.backend.cashgame.TournamentSessionId;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.OpenTournamentSessionRequest;
import com.cubeia.backend.cashgame.dto.TransferMoneyRequest;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.UnseatPlayersMttAction;
import com.cubeia.firebase.api.action.mtt.MttDataAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
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
import com.cubeia.games.poker.common.Money;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.common.lobby.PokerLobbyAttributes;
import com.cubeia.games.poker.io.protocol.TournamentOut;
import com.cubeia.games.poker.tournament.configuration.TournamentTableSettings;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.history.HistoryPersister;
import com.cubeia.games.poker.tournament.payouts.PayoutHandler;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.cubeia.games.poker.tournament.util.ProtocolFactory;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import org.apache.log4j.Logger;
import org.joda.time.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;

public class PokerTournament implements Serializable {

    private static final Logger log = Logger.getLogger(PokerTournament.class);

    private static final long serialVersionUID = 0L;

    private PokerTournamentState pokerState;

    private transient SystemTime dateFetcher;

    private transient MTTStateSupport state;

    private transient MttInstance instance;

    private transient TournamentAssist mttSupport;

    private transient MttNotifier notifier;

    private transient HistoryPersister historyPersister;

    private transient CashGamesBackendService backend;

    public PokerTournament(PokerTournamentState pokerState) {
        this.pokerState = pokerState;
    }

    public void injectTransientDependencies(MttInstance instance, TournamentAssist support, MTTStateSupport state,
            MttNotifier notifier, TournamentHistoryPersistenceService historyService, CashGamesBackendService backend, SystemTime dateFetcher) {
        this.instance = instance;
        this.mttSupport = support;
        this.state = state;
        this.notifier = notifier;
        this.historyPersister = new HistoryPersister(pokerState.getHistoricId(), historyService, dateFetcher);
        this.backend = backend;
        this.dateFetcher = dateFetcher;
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
            startBreakIfReady(action.getTableId());
        }

        if (log.isDebugEnabled()) {
            log.debug("Remaining players: " + state.getRemainingPlayerCount() + " Remaining tables: " + state.getTables());
        }
        updateRemainingPlayerCount();
    }

    private void startBreakIfReady(int tableId) {
        if (pokerState.isOnBreak()) {
            pokerState.addTableReadyForBreak(tableId);
            if (allTablesAreReadyForBreak()) {
                // start break
                startBreak();
            } else {
                notifyWaitingForOtherTablesToFinishBeforeBreak(tableId);
            }
        }
    }

    private void startBreak() {
        notifyAllTablesThatBreakStarted();
        scheduleNextBlindsLevel();
    }

    private void notifyAllTablesThatBreakStarted() {
        notifyAllTablesOfNewBlinds();
    }

    private void notifyAllTablesOfNewBlinds() {
        for (Integer tableId : state.getTables()) {
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(pokerState.getCurrentBlindsLevel());
            instance.getMttNotifier().notifyTable(tableId, action);
        }
    }

    private void notifyWaitingForOtherTablesToFinishBeforeBreak(int tableId) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(new WaitingForTablesToFinishBeforeBreak());
        notifier.notifyTable(tableId, action);
    }

    private boolean allTablesAreReadyForBreak() {
        return pokerState.allTablesReadyForBreak(state.getTables().size(), tablesWithLonelyPlayer());
    }

    private Set<Integer> tablesWithLonelyPlayer() {
        Set<Integer> tables = newHashSet();
        for (int tableId:state.getTables()) {
            if (state.getPlayersAtTable(tableId).size() == 1) {
                tables.add(tableId);
            }
        }
        return tables;
    }

    private void increaseBlindsIfNeeded(Level currentBlindsLevel, int tableId) {
        if (currentBlindsLevel.getBigBlindAmount() < pokerState.getBigBlindAmount()) {
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(pokerState.getCurrentBlindsLevel());
            instance.getMttNotifier().notifyTable(tableId, action);
        }
    }

    public void handleTablesCreated(MttTablesCreatedAction action) {
        for (int tableId: action.getTables()) {
            historyPersister.addTable(getExternalTableId(tableId));
        }
        if (pokerState.allTablesHaveBeenCreated(state.getTables().size())) {
            mttSupport.seatPlayers(state, createInitialSeating());
            scheduleSendStartToTables();
        }
    }

    private String getExternalTableId(int tableId) {
        return instance.getTableLobbyAccessor(tableId).getStringAttribute(PokerLobbyAttributes.TABLE_EXTERNAL_ID.name());
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
        if (!playersOut.isEmpty()) {
            handlePayouts(playersOut);
            unseatPlayers(tableId, playersOut);
        }
    }

    private void unseatPlayers(int tableId, Set<Integer> playersOut) {
        mttSupport.unseatPlayers(state, tableId, playersOut, UnseatPlayersMttAction.Reason.OUT);
    }

    private void handlePayouts(Set<Integer> playersOut) {
        PayoutHandler payoutHandler = new PayoutHandler(pokerState.getPayouts());
        Map<Integer, Long> payouts = payoutHandler.calculatePayouts(playersOut, balancesAtStartOfHand(playersOut), state.getRemainingPlayerCount());
        for (Map.Entry<Integer, Long> payout : payouts.entrySet()) {
            // Transfer the given amount of money from the tournament account to the player account.
            transferMoneyAndCloseSession(pokerState.getPlayerSession(payout.getKey()), payout.getValue());
        }
    }

    private void transferMoneyAndCloseSession(PlayerSessionId playerSession, long payoutInCents) {
        if (payoutInCents > 0) {
            backend.transfer(createPayoutRequest(payoutInCents, playerSession));
        }
        backend.closeTournamentSession(new CloseSessionRequest(playerSession), createTournamentId());
    }

    private TransferMoneyRequest createPayoutRequest(long amount, PlayerSessionId playerSession) {
        PlayerSessionId fromSession = pokerState.getTournamentSession();
        PlayerSessionId toSession = playerSession;
        String comment = "Tournament payout";
        Money money = pokerState.convertToMoney(amount);

        return new TransferMoneyRequest(money, fromSession, toSession, comment);
    }

    private Map<Integer, Long> balancesAtStartOfHand(Set<Integer> playersOut) {
        Map<Integer, Long> balances = newHashMap();
        for (int playerId : playersOut) {
            balances.put(playerId, pokerState.getPlayerBalance(playerId));
        }
        return balances;
    }

    private Long getStartingChips() {
        return PokerTournamentState.STARTING_CHIPS;
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
            historyPersister.playerOut(packet.player, packet.position);
        }
    }

    private void handleFinishedTournament() {
        log.info("Tournament [" + instance.getId() + ":" + instance.getState().getName() + "] was finished.");
        historyPersister.tournamentFinished();
        setTournamentStatus(PokerTournamentStatus.FINISHED);

        // Find and pay winner
        MTTStateSupport state = (MTTStateSupport) instance.getState();
        Integer table = state.getTables().iterator().next();
        Collection<Integer> winners = state.getPlayersAtTable(table);
        sendTournamentOutToPlayers(winners, instance);
        payWinner(winners.iterator().next());

        closeMainTournamentSession();
    }

    private void closeMainTournamentSession() {
        log.debug("Closing tournament session for tournament " + pokerState.getHistoricId());
        backend.closeTournamentSession(new CloseSessionRequest(pokerState.getTournamentSession()), createTournamentId());
    }

    private void payWinner(Integer playerId) {
        log.debug("Paying winner: " + playerId);
        Long value = pokerState.getPayouts().getPayoutsForPosition(1);
        PlayerSessionId playerSession = pokerState.getPlayerSession(playerId);
        transferMoneyAndCloseSession(playerSession, value);
    }

    private boolean isTournamentFinished() {
        return state.getRemainingPlayerCount() == 1;
    }

    private void startNextRoundIfPossible(int tableId) {
        if (!pokerState.isOnBreak()) {
            if (state.getPlayersAtTable(tableId).size() > 1 ) {
                mttSupport.sendRoundStartActionToTables(state, singleton(tableId));
            } else {
                // Notify table that we are waiting for more players before we can start the next hand.

            }
        }
    }

    /**
     * Tries to balance the tables by moving one or more players from this table to other
     * tables.
     *
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
     * @param sourceTableId the table we are moving player from
     * @return true if table is closed
     */
    private boolean applyBalancing(List<Move> moves, int sourceTableId) {
        Set<Integer> tablesToStart = new HashSet<Integer>();

        for (Move move : moves) {
            int tableId = move.getDestinationTableId();
            int playerId = move.getPlayerId();

            Collection<Integer> playersAtDestinationTableBeforeMoving = state.getPlayersAtTable(tableId);
            // Move the player, we don't care which seat he gets put at, so set it to -1.
            log.debug("Moving player " + playerId + " from table " + sourceTableId + " to table " + tableId);
            mttSupport.movePlayer(state, playerId, tableId, -1, UnseatPlayersMttAction.Reason.BALANCING, pokerState.getPlayerBalance(playerId));
            if (playersAtDestinationTableBeforeMoving.size() == 1) {
                // There was only one player at the table before we moved this player there, start a new round.
                tablesToStart.add(tableId);
            }
            historyPersister.playerMoved(playerId, tableId);
        }

        if (!tablesToStart.isEmpty() && !pokerState.isOnBreak()) {
            log.debug("Sending explicit start to tables[" + Arrays.toString(tablesToStart.toArray()) + "] due to low number of players.");
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

    private void scheduleTournamentStart() {
        if (pokerState.shouldScheduleTournamentStart(dateFetcher.date())) {
            MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.START_TOURNAMENT);
            long timeToTournamentStart = pokerState.getTimeUntilTournamentStart(dateFetcher.date());
            log.debug("Scheduling tournament start in " + Duration.millis(timeToTournamentStart).getStandardMinutes() + " minutes, for tournament " + instance);
            instance.getScheduler().scheduleAction(action, timeToTournamentStart);
        } else {
            log.debug("Won't schedule tournament start because the life cycle says no.");
        }
    }

    private void scheduleRegistrationOpening() {
        MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.OPEN_REGISTRATION);
        long timeToRegistrationStart = pokerState.getTimeUntilRegistrationStart(dateFetcher.date());
        log.debug("Scheduling registration opening in " + timeToRegistrationStart + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(action, timeToRegistrationStart);
    }

    private void scheduleSendStartToTables() {
        MttObjectAction action = new MttObjectAction(instance.getId(), TournamentTrigger.SEND_START_TO_TABLES);
        log.debug("Scheduling round start in " + 1000 + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(action, 1000);
    }

    private void scheduleNextBlindsLevel() {
        long millisecondsToNextLevel = Duration.standardMinutes(pokerState.getCurrentBlindsLevel().getDurationInMinutes()).getMillis();
        log.debug("Scheduling next blinds level in " + millisecondsToNextLevel + " millis, for tournament " + instance);
        instance.getScheduler().scheduleAction(new MttObjectAction(instance.getId(), TournamentTrigger.INCREASE_LEVEL), millisecondsToNextLevel);
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
        historyPersister.statusChanged(status.name());
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
        setPayoutsToUse();
        createTables();
        historyPersister.tournamentStarted(state.getName());
        transferBuyInsToTournamentSession();
        transferFeesToRakeAccount();
    }

    private void transferBuyInsToTournamentSession() {
        for (PlayerSessionId playerSessionId : pokerState.getPlayerSessions()) {
            Money buyIn = pokerState.getBuyInAsMoney();
            PlayerSessionId fromAccount = playerSessionId;
            TournamentSessionId toAccount = pokerState.getTournamentSession();

            TransferMoneyRequest request = new TransferMoneyRequest(buyIn, fromAccount, toAccount, "Buy-in for tournament " + pokerState.getHistoricId());
            backend.transfer(request);
        }
    }

    private void transferFeesToRakeAccount() {
        for (PlayerSessionId playerSessionId : pokerState.getPlayerSessions()) {
            Money fee = pokerState.getFeeAsMoney();
            PlayerSessionId fromAccount = playerSessionId;

            backend.transferMoneyToRakeAccount(fromAccount, fee, "Fee for tournament " + pokerState.getHistoricId());
        }
    }

    private void setPayoutsToUse() {
        pokerState.setPayouts(state.getRegisteredPlayersCount());
    }

    private void createTables() {
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
        return pokerState.shouldTournamentStart(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private boolean tournamentShouldBeCancelled() {
        return pokerState.shouldCancelTournament(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private TournamentTableSettings getTableSettings() {
        log.debug("Getting table settings. Small = " + pokerState.getSmallBlindAmount() + " big = " + pokerState.getBigBlindAmount());
        TournamentTableSettings settings = new TournamentTableSettings(pokerState.getSmallBlindAmount(), pokerState.getBigBlindAmount());
        settings.setTimingProfile(pokerState.getTiming());
        return settings;
    }
    
    public void playerRegistered(MttRegistrationRequest request) {
        addJoinedTimestamps();
        historyPersister.playerRegistered(request.getPlayer().getPlayerId());
    }

    public void playerUnregistered(int playerId) {
        try {
            log.debug("Player " + playerId + " unregistered. Closing session.");
            backend.closeSession(new CloseSessionRequest(pokerState.getPlayerSession(playerId)));
            pokerState.removePlayerSession(playerId);
            historyPersister.playerUnregistered(playerId);
        } catch (CloseSessionFailedException e) {
            log.error("Failed closing session for player " + playerId, e);
            historyPersister.playerFailedUnregistering(playerId, e.getMessage());
        }
    }

    public MttRegisterResponse register(MttRegistrationRequest request) {
        log.info("Checking if " + request + " is allowed to register.");

        if (pokerState.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.DENIED;
        } else {
            backend.openTournamentPlayerSession(createOpenTournamentPlayerSessionRequest(request), pokerState.getTournamentSession());
            pokerState.addPendingRegistration(request.getPlayer().getPlayerId());
            return MttRegisterResponse.ALLOWED;
        }
    }

    private OpenTournamentSessionRequest createOpenTournamentPlayerSessionRequest(MttRegistrationRequest request) {
        TournamentId tournamentId = createTournamentId();
        Money money = pokerState.getBuyInPlusFeeAsMoney();
        log.debug("Created money for buy-in: " + money);
        return new OpenTournamentSessionRequest(request.getPlayer().getPlayerId(), tournamentId, money);
    }

    private TournamentId createTournamentId() {
        return new TournamentId(pokerState.getHistoricId(), instance.getId());
    }

    public MttRegisterResponse unregister(int pid) {
        if (pokerState.getStatus() == PokerTournamentStatus.REGISTERING && state.getPlayerRegistry().isRegistered(pid)) {
            return MttRegisterResponse.ALLOWED;
        } else {
            return MttRegisterResponse.DENIED;
        }
    }

    public void tournamentCreated() {
        String historicId = historyPersister.createHistoricId();
        log.debug("Tournament created. Historic id: " + historicId);
        pokerState.setHistoricId(historicId);
        historyPersister.setHistoricId(historicId);
        backend.openTournamentSession(createOpenTournamentSessionRequest());
        if (tournamentShouldBeCancelled()) {
            cancelTournament();
        } else if (pokerState.shouldOpenRegistration(dateFetcher.date())) {
            openRegistration();
            scheduleTournamentStart();
        } else if (pokerState.shouldScheduleRegistrationOpening(dateFetcher.date())) {
            scheduleRegistrationOpening();
        }
    }

    private OpenTournamentSessionRequest createOpenTournamentSessionRequest() {
        TournamentId tournamentId = createTournamentId();
        return new OpenTournamentSessionRequest(-1, tournamentId, pokerState.createZeroMoney());
    }

    public void handleTrigger(TournamentTrigger trigger) {
        switch (trigger) {
            case START_TOURNAMENT:
                if (enoughPlayers() && !hasPendingRegistrations()) {
                    startTournament();
                } else if (enoughPlayers() && hasPendingRegistrations()) {
                    // Schedule a new start in X seconds.
                    log.debug("We have enough players to start the tournament, but there are pending registrations, waiting for them to go through.");
                } else {
                    cancelTournament();
                }
                break;
            case OPEN_REGISTRATION:
                openRegistration();
                scheduleTournamentStart();
                break;
            case SEND_START_TO_TABLES:
                sendRoundStartToAllTables();
                scheduleNextBlindsLevel();
                break;
            case INCREASE_LEVEL:
                increaseBlindsLevel();
                break;
        }
    }

    private boolean hasPendingRegistrations() {
        return pokerState.hasPendingRegistrations();
    }

    private boolean enoughPlayers() {
        return state.getRegisteredPlayersCount() >= state.getMinPlayers();
    }

    private void increaseBlindsLevel() {
        Level levelBeforeIncreasing = pokerState.getCurrentBlindsLevel();
        Level levelAfterIncreasing = pokerState.increaseBlindsLevel();
        log.debug("Level increased. Before: " + levelBeforeIncreasing + " after: " + levelAfterIncreasing);
        historyPersister.blindsIncreased(pokerState.getCurrentBlindsLevel());

        if (finishedBreak(levelBeforeIncreasing, levelAfterIncreasing)) {
            // The break has finished. Tell all tables about the new blinds and tell them to start.
            log.debug("The break has finished, notifying all tables about new blinds and telling them to start.");
            notifyAllTablesOfNewBlinds();
            sendRoundStartToAllTables();
            pokerState.breakFinished();
        }

        if (!pokerState.isOnBreak()) {
            /*
             * Schedule next blinds level unless this level is a break (because then we have to wait for all tables to finish before
             * we start the break and schedule next level).
             */
            scheduleNextBlindsLevel();
        }
    }

    private boolean finishedBreak(Level levelBeforeIncreasing, Level currentBlindsLevel) {
        return levelBeforeIncreasing.isBreak() && !currentBlindsLevel.isBreak();
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
        // TODO: Refund players. (Note that there may be players with pending registrations as well).
        log.warn("Should refund players here!");
    }

    private void sendRoundStartToAllTables() {
        log.debug("Sending round start to all tables.");
        mttSupport.sendRoundStartActionToTables(state, state.getTables());
    }

    public void handleOpenSessionResponse(OpenSessionResponse response) {
        log.debug("Open session succeeded: " + response);
        if (response.getSessionId().playerId == -1) {
            pokerState.setTournamentSessionId(response.getSessionId());
        } else {
            pokerState.addBuyInToPrizePool();
            pokerState.addPlayerSession(response.getSessionId());
            pokerState.removePendingRequest(response.getSessionId().playerId);
            historyPersister.playerOpenedSession(response.getSessionId().playerId, response.getSessionId().integrationSessionId);

            checkIfTournamentShouldBetStartedOrCancelled();
        }
    }

    public void handleOpenSessionResponseFailed(OpenSessionFailedResponse response) {
        if (response.getPlayerId() == -1) {
            log.fatal("Failed opening tournament session account. Cancelling tournament.");
            cancelTournament();
        } else {
            log.debug("Open session failed: " + response);
            pokerState.removePendingRequest(response.getPlayerId());
            state.getPlayerRegistry().removePlayer(response.getPlayerId());
            historyPersister.playerFailedOpeningSession(response.getPlayerId(), response.getMessage());

            checkIfTournamentShouldBetStartedOrCancelled();
        }
    }

    private void checkIfTournamentShouldBetStartedOrCancelled() {
        if (tournamentShouldStart()) {
            log.debug("Tournament should be started.");
            startTournament();
        } else if (tournamentShouldBeCancelled()) {
            log.debug("Tournament should be cancelled.");
            cancelTournament();
        }
    }
}
