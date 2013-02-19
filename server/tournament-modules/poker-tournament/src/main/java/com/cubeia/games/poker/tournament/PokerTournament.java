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
import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.mtt.model.MttPlayerStatus;
import com.cubeia.firebase.api.mtt.model.MttRegisterResponse;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.seating.SeatingContainer;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.tables.Move;
import com.cubeia.firebase.api.mtt.support.tables.TableBalancer;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.games.poker.common.time.SystemTime;
import com.cubeia.games.poker.common.lobby.PokerLobbyAttributes;
import com.cubeia.games.poker.io.protocol.TournamentOut;
import com.cubeia.games.poker.tournament.messages.TournamentTableSettings;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.history.HistoryPersister;
import com.cubeia.games.poker.tournament.messages.BlindsWithDeadline;
import com.cubeia.games.poker.tournament.messages.CloseTournament;
import com.cubeia.games.poker.tournament.messages.PlayerLeft;
import com.cubeia.games.poker.tournament.messages.PokerTournamentRoundReport;
import com.cubeia.games.poker.tournament.messages.TournamentDestroyed;
import com.cubeia.games.poker.tournament.messages.WaitingForPlayers;
import com.cubeia.games.poker.tournament.messages.WaitingForTablesToFinishBeforeBreak;
import com.cubeia.games.poker.tournament.payouts.ConcretePayout;
import com.cubeia.games.poker.tournament.payouts.PayoutHandler;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.cubeia.games.poker.tournament.util.PacketSender;
import com.cubeia.poker.shutdown.api.ShutdownServiceContract;
import com.cubeia.poker.tournament.history.api.HistoricPlayer;
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

import static com.cubeia.firebase.api.common.AttributeValue.wrap;
import static com.cubeia.firebase.api.mtt.model.MttPlayerStatus.OUT;
import static com.cubeia.firebase.api.mtt.model.MttPlayerStatus.PLAYING;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.CLOSED;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.ON_BREAK;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.PREPARING_BREAK;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.RUNNING;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

/**
 * Implementation of poker tournaments.
 *
 * Please note a few things:
 * 1. This class is serializable, but we'll keep all data in the PokerTournamentState.
 * 2. The reason this class exists is that previously all of this was done in PokerTournamentProcessor, but
 *    since that class has to be thread safe it had no fields, so all parameters (such as MttInstance)
 *    were being passed on as arguments between all methods, which was not very elegant or convenient.
 * 3. That being said, this is becoming a God class. Break it down.
 *
 */
public class PokerTournament implements Serializable {

    private static final Logger log = Logger.getLogger(PokerTournament.class);

    private static final long serialVersionUID = 0L;

    private static final int MILLIS_PER_MINUTE = 60 * 1000;

    private static final String REREGISTRATION = "REREGISTRATION";

    private PokerTournamentState pokerState;

    private transient SystemTime dateFetcher;

    private transient MTTStateSupport state;

    private transient MttInstance instance;

    private transient TournamentAssist mttSupport;

    private transient HistoryPersister historyPersister;

    private transient CashGamesBackendService backend;

    private transient PacketSender sender;

    private transient ShutdownServiceContract shutdownService;

    public PokerTournament(PokerTournamentState pokerState) {
        this.pokerState = pokerState;
    }

    /**
     * Invoked when a player has logged out or disconnected. If this is a sit&go, we should
     * un-register the player.
     *
     * @param playerLeft holds information about the player who left, not null
     */
    public void handlePlayerLeft(PlayerLeft playerLeft) {
        if (pokerState.isSitAndGo()) {
            unregisterPlayer(playerLeft.getPlayerId());
        }
    }

    public void injectTransientDependencies(MttInstance instance, TournamentAssist support, MTTStateSupport state,
            TournamentHistoryPersistenceService historyService, CashGamesBackendService backend, SystemTime dateFetcher,
            ShutdownServiceContract shutdownService, PacketSender sender) {
        this.instance = instance;
        this.mttSupport = support;
        this.state = state;
        this.historyPersister = new HistoryPersister(pokerState.getHistoricId(), historyService, dateFetcher);
        this.backend = backend;
        this.dateFetcher = dateFetcher;
        this.shutdownService = shutdownService;
        this.sender = sender;
    }

    public void processRoundReport(MttRoundReportAction action) {
        if (log.isDebugEnabled()) {
            log.debug("Process round report from table[" + action.getTableId() + "] Report: " + action);
        }

        PokerTournamentRoundReport report = (PokerTournamentRoundReport) action.getAttachment();

        updateBalances(report);
        Set<Integer> playersOut = getPlayersOut(report);
        log.info("Players out of tournament[" + instance.getId() + "] : " + playersOut);
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
        pokerState.invalidatePlayerList();
        pokerState.invalidateTournamentStatistics();
        updatePlayersLeft();
    }

    private void startBreakIfReady(int tableId) {
        if (pokerState.isOnBreak()) {
            pokerState.addTableReadyForBreak(tableId);
            if (allTablesAreReadyForBreak()) {
                // start break
                startBreak();
            } else {
                setTournamentStatus(PREPARING_BREAK);
                pokerState.prepareBreak(state.getTables());
                pokerState.addTablesReadyForBreak(tablesWithLonelyPlayer());
                notifyWaitingForOtherTablesToFinishBeforeBreak(tableId);
            }
        }
    }

    private void startBreak() {
        setTournamentStatus(ON_BREAK);
        scheduleNextBlindsLevel();
        notifyAllTablesThatBreakStarted();
    }

    private void notifyAllTablesThatBreakStarted() {
        notifyAllTablesOfNewBlinds();
    }

    private void notifyAllTablesOfNewBlinds() {
        for (Integer tableId : state.getTables()) {
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(createBlindsWithDeadline());
            notifyTable(tableId, action);
        }
    }

    private BlindsWithDeadline createBlindsWithDeadline() {
        log.debug("Creating blinds with deadline: " + pokerState.getNextLevelStartTime());
        Level level = pokerState.getCurrentBlindsLevel();
        return new BlindsWithDeadline(level.getSmallBlindAmount(), level.getBigBlindAmount(), level.getAnteAmount(),
                                      level.getDurationInMinutes(), level.isBreak(), pokerState.getNextLevelStartTime().getMillis());
    }

    private void notifyWaitingForOtherTablesToFinishBeforeBreak(int tableId) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(new WaitingForTablesToFinishBeforeBreak());
        notifyTable(tableId, action);
    }

    private void notifyTable(int tableId, GameObjectAction action) {
        instance.getMttNotifier().notifyTable(tableId, action);
    }

    private boolean allTablesAreReadyForBreak() {
        return pokerState.allTablesReadyForBreak();
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

    private void increaseBlindsIfNeeded(PokerTournamentRoundReport.Level currentBlindsLevel, int tableId) {
        if (currentBlindsLevel.getBigBlindAmount() < pokerState.getBigBlindAmount()) {
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(createBlindsWithDeadline());
            notifyTable(tableId, action);
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
            updatePlayersLeft();
            pokerState.invalidatePlayerToTableMap();
        }
    }

    private void updatePlayersLeft() {
        instance.getLobbyAccessor().setIntAttribute(PokerTournamentLobbyAttributes.PLAYERS_LEFT.name(), state.getRemainingPlayerCount());
    }

    private void unseatPlayers(int tableId, Set<Integer> playersOut) {
        mttSupport.unseatPlayers(state, tableId, playersOut, UnseatPlayersMttAction.Reason.OUT);
    }

    private void handlePayouts(Set<Integer> playersOut) {
        PayoutHandler payoutHandler = new PayoutHandler(pokerState.getPayouts());
        List<ConcretePayout> payouts = payoutHandler.calculatePayouts(playersOut, balancesAtStartOfHand(playersOut), state.getRemainingPlayerCount());
        for (ConcretePayout payout : payouts) {
            // Transfer the given amount of money from the tournament account to the player account.
            transferMoneyAndCloseSession(pokerState.getPlayerSession(payout.getPlayerId()), payout.getPayoutInCents());
            setPlayerOutInPosition(payout.getPlayerId(), payout.getPosition());
        }
        sendTournamentOutToPlayers(payouts, instance);
    }

    private void transferMoneyAndCloseSession(PlayerSessionId playerSession, long payoutInCents) {
        if (payoutInCents > 0) {
            backend.transfer(createPayoutRequest(payoutInCents, playerSession));
        }
        backend.closeTournamentSession(new CloseSessionRequest(playerSession), createTournamentId());
    }

    private TransferMoneyRequest createPayoutRequest(long amount, PlayerSessionId playerSession) {
        PlayerSessionId fromSession = pokerState.getTournamentSession();
        String comment = "Tournament payout";
        Money money = pokerState.convertToMoney(amount);

        return new TransferMoneyRequest(money, fromSession, playerSession, comment);
    }

    private Map<Integer, Long> balancesAtStartOfHand(Set<Integer> playersOut) {
        Map<Integer, Long> balances = newHashMap();
        for (int playerId : playersOut) {
            balances.put(playerId, pokerState.getPlayerBalance(playerId));
        }
        return balances;
    }

    private long getStartingChips() {
        return pokerState.getStartingChips();
    }

    private void sendTournamentOutToPlayers(List<ConcretePayout> playersOut, MttInstance instance) {
        for (ConcretePayout payout : playersOut) {
            TournamentOut packet = new TournamentOut();
            packet.position = instance.getState().getRemainingPlayerCount();
            int playerId = payout.getPlayerId();
            packet.player = playerId;
            log.debug("Telling player " + playerId + " that he finished in position " + packet.position);
            sender.sendPacketToPlayer(packet, playerId);
            historyPersister.playerOut(packet.player, packet.position, payout.getPayoutInCents());
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
        Integer winner = winners.iterator().next();
        payWinner(winner);
        unseatPlayers(table, singleton(winner));

        closeMainTournamentSession();
        scheduleTournamentClosing();
    }

    private void scheduleTournamentClosing() {
        log.debug("Scheduling tournament to be closed in " + pokerState.getMinutesVisibleAfterFinished() + " minutes.");
        MttAction action = new MttObjectAction(instance.getId(), new CloseTournament());
        instance.getScheduler().scheduleAction(action, pokerState.getMinutesVisibleAfterFinished() * MILLIS_PER_MINUTE);
    }

    private void closeMainTournamentSession() {
        log.debug("Closing tournament session " + pokerState.getTournamentSession() + " for tournament " + pokerState.getHistoricId());
        backend.closeTournamentSession(new CloseSessionRequest(pokerState.getTournamentSession()), createTournamentId());
    }

    private void payWinner(Integer playerId) {
        log.debug("Paying winner: " + playerId);
        int payout = pokerState.getPayouts().getPayoutsForPosition(1);
        setPlayerOutInPosition(playerId, 1);
        sendTournamentOutToPlayers(singletonList(new ConcretePayout(playerId, 1, payout)), instance);

        PlayerSessionId playerSession = pokerState.getPlayerSession(playerId);
        transferMoneyAndCloseSession(playerSession, payout);
    }

    private void setPlayerOutInPosition(int playerId, int position) {
        MttPlayer player = pokerState.getTournamentPlayer(playerId, state);
        player.setStatus(OUT);
        player.setPosition(position);
    }

    private boolean isTournamentFinished() {
        return state.getRemainingPlayerCount() == 1;
    }

    private void startNextRoundIfPossible(int tableId) {
        if (!pokerState.isOnBreak()) {
            if (state.getPlayersAtTable(tableId).size() > 1) {
                mttSupport.sendRoundStartActionToTables(state, singleton(tableId));
            } else {
                // Notify table that we are waiting for more players before we can start the next hand.
                GameObjectAction action = new GameObjectAction(tableId);
                action.setAttachment(new WaitingForPlayers());
                notifyTable(tableId, action);
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
        if (moves.isEmpty()) return false; // Nothing to do
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

        pokerState.invalidatePlayerToTableMap(); // The table to player map is not valid anymore.
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

        for (Integer tableId : state.getTables()) {
            List<Integer> players = new ArrayList<Integer>();
            players.addAll(state.getPlayersAtTable(tableId));

            if (players.size() > 0) {
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
        Duration levelDuration = Duration.standardMinutes(pokerState.getCurrentBlindsLevel().getDurationInMinutes());
        long millisecondsToNextLevel = levelDuration.getMillis();
        pokerState.setNextLevelStartTime(dateFetcher.date().plus(levelDuration));
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
        if (status == pokerState.getStatus()) {
            log.debug("Status is already " + status + ". Ignoring.");
            return;
        }
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
        if (shutdownService.isSystemShuttingDown()) {
            log.info("Won't start tournament, since system is shutting down.");
            cancelTournament();
            return;
        }

        long registrationElapsedTime = pokerState.getLastRegisteredTime() - pokerState.getFirstRegisteredTime();
        log.debug("Starting tournament [" + instance.getId() + " : " + instance.getState().getName() + "]. Registration time was " + registrationElapsedTime + " ms");
        transferBuyInsToTournamentSession();

        setTournamentStatus(RUNNING);
        updatePlayerStatuses(PLAYING);
        pokerState.setStartTime(dateFetcher.date());
        updatePayouts();
        createTables();
        transferFeesToRakeAccount();
        historyPersister.tournamentStarted(state.getName());
        pokerState.invalidatePlayerToTableMap();
    }

    private void updatePlayerStatuses(MttPlayerStatus status) {
        for (MttPlayer player : state.getPlayerRegistry().getPlayers()) {
            player.setStatus(status);
        }
    }

    private void transferBuyInsToTournamentSession() {
        for (PlayerSessionId playerSessionId : pokerState.getPlayerSessions()) {
            Money buyIn = pokerState.getBuyInAsMoney();
            TournamentSessionId toAccount = pokerState.getTournamentSession();

            TransferMoneyRequest request = new TransferMoneyRequest(buyIn, playerSessionId, toAccount, "Buy-in for tournament " + pokerState.getHistoricId());
            backend.transfer(request);
        }
    }

    private void transferFeesToRakeAccount() {
        // TODO: Investigate scaling implications.
        for (PlayerSessionId playerSessionId : pokerState.getPlayerSessions()) {
            Money fee = pokerState.getFeeAsMoney();
            backend.transferMoneyToRakeAccount(playerSessionId, fee, "Fee for tournament " + pokerState.getHistoricId());
        }
    }

    private void updatePayouts() {
        // The tournament will not start unless we have min player registered, so payouts can assume minPlayers participants.
        pokerState.setPayouts(Math.max(state.getMinPlayers(), state.getRegisteredPlayersCount()));
    }

    private void createTables() {
        int tablesToCreate = numberOfTablesToCreate();
        pokerState.setTablesToCreate(tablesToCreate);
        TournamentTableSettings settings = getTableSettings();
        log.debug("Creating tables for tournament [" + instance + "] . State.getID: " + state.getId());
        mttSupport.createTables(state, tablesToCreate, "mtt", settings);
    }

    int numberOfTablesToCreate() {
        return (int) Math.ceil(state.getRegisteredPlayersCount() / (double) state.getSeats());
    }

    private boolean tournamentShouldStart() {
        return pokerState.shouldTournamentStart(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private boolean tournamentShouldBeCancelled() {
        boolean resurrectingSitAndGo = pokerState.isResurrectingTournament() && pokerState.isSitAndGo();
        return resurrectingSitAndGo || pokerState.shouldCancelTournament(dateFetcher.date(), state.getRegisteredPlayersCount(), state.getMinPlayers());
    }

    private TournamentTableSettings getTableSettings() {
        return new TournamentTableSettings(pokerState.getTiming(), pokerState.getBetStrategy());
    }
    
    public void playerRegistered(MttRegistrationRequest request) {
        addJoinedTimestamps();
        if (isReRegistration(request)) {
            historyPersister.playerReRegistered(request.getPlayer().getPlayerId());
        }
        pokerState.invalidatePlayerMap();
        updatePayouts();
    }

    private HistoricPlayer historicPlayer(MttPlayer player, PlayerSessionId sessionId) {
        return new HistoricPlayer(player.getPlayerId(), player.getScreenname(), sessionId.integrationSessionId);
    }

    public void playerUnregistered(int playerId) {
        unregisterPlayer(playerId);
        updatePayouts();
    }

    private void unregisterPlayer(int playerId) {
        try {
            log.debug("Player " + playerId + " unregistered. Closing session.");
            backend.closeSession(new CloseSessionRequest(pokerState.getPlayerSession(playerId)));
            pokerState.removePlayerSession(playerId);
            historyPersister.playerUnregistered(playerId);
            pokerState.invalidatePlayerMap();
        } catch (CloseSessionFailedException e) {
            log.error("Failed closing session for player " + playerId, e);
            historyPersister.playerFailedUnregistering(playerId, e.getMessage());
        }
    }

    public MttRegisterResponse checkRegistration(MttRegistrationRequest request) {
        log.info("Checking if " + request + " is allowed to register.");

        if (isReRegistration(request)) {
            return MttRegisterResponse.ALLOWED;
        }

        if (pokerState.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.DENIED;
        } else {
            backend.openTournamentPlayerSession(createOpenTournamentPlayerSessionRequest(request), pokerState.getTournamentSession());
            pokerState.addPendingRegistration(request.getPlayer().getPlayerId());
            return MttRegisterResponse.ALLOWED;
        }
    }

    private boolean isReRegistration(MttRegistrationRequest request) {
        if (request.getParameters() == null) return false;
        for (Attribute parameter : request.getParameters()) {
            if (REREGISTRATION.equals(parameter.name)) {
                return true;
            }
        }
        return false;
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

    public MttRegisterResponse checkUnregistration(int pid) {
        if (pokerState.getStatus() == PokerTournamentStatus.REGISTERING && state.getPlayerRegistry().isRegistered(pid)) {
            return MttRegisterResponse.ALLOWED;
        } else {
            return MttRegisterResponse.DENIED;
        }
    }

    public void tournamentCreated() {
        log.debug("Tournament created. Historic id: " + pokerState.getHistoricId());
        log.debug("Resurrecting players: " + pokerState.getResurrectingPlayers());
        if (pokerState.isResurrectingTournament()) {
            reRegisterPlayers(pokerState.getResurrectingPlayers());
            pokerState.invalidatePlayerMap();
        } else {
            backend.openTournamentSession(createOpenTournamentSessionRequest());
        }
        if (tournamentShouldBeCancelled()) {
            cancelTournament();
        } else if (pokerState.shouldOpenRegistration(dateFetcher.date())) {
            openRegistration();
            scheduleTournamentStart();
        } else if (pokerState.shouldScheduleRegistrationOpening(dateFetcher.date())) {
            scheduleRegistrationOpening();
        }
    }

    private void reRegisterPlayers(Set<HistoricPlayer> resurrectingPlayers) {
        for (HistoricPlayer resurrectingPlayer : resurrectingPlayers) {
            reRegisterPlayer(resurrectingPlayer);
        }
        updatePayouts();
        pokerState.getResurrectingPlayers().clear();
        pokerState.addBuyInToPrizePool();
    }

    private void reRegisterPlayer(HistoricPlayer resurrectingPlayer) {
        MttPlayer player = new MttPlayer(resurrectingPlayer.getId(), resurrectingPlayer.getName());
        List<Attribute> parameters = singletonList(new Attribute(REREGISTRATION, wrap("true")));
        MttRegistrationRequest request = new MttRegistrationRequest(player, parameters);
        pokerState.addPlayerSession(new PlayerSessionId(resurrectingPlayer.getId(), resurrectingPlayer.getSessionId()));
        state.getPlayerRegistry().register(instance, request);
    }

    private OpenTournamentSessionRequest createOpenTournamentSessionRequest() {
        TournamentId tournamentId = createTournamentId();
        return new OpenTournamentSessionRequest(-1, tournamentId, pokerState.createZeroMoney());
    }

    public void handleTrigger(TournamentTrigger trigger) {
        switch (trigger) {
            case START_TOURNAMENT:
                startOrCancelTournament();
                break;
            case OPEN_REGISTRATION:
                openRegistration();
                scheduleTournamentStart();
                break;
            case SEND_START_TO_TABLES:
                scheduleNextBlindsLevel();
                sendRoundStartToAllTables();
                break;
            case INCREASE_LEVEL:
                increaseBlindsLevel();
                break;
        }
    }

    private void startOrCancelTournament() {
        if (enoughPlayers() && !hasPendingRegistrations()) {
            startTournament();
        } else if (enoughPlayers() && hasPendingRegistrations()) {
            // Schedule a new start in X seconds.
            log.debug("We have enough players to start the tournament, but there are pending registrations, waiting for them to go through.");
        } else {
            cancelTournament();
        }
    }

    private boolean hasPendingRegistrations() {
        return pokerState.hasPendingRegistrations();
    }

    private boolean enoughPlayers() {
        return state.getRegisteredPlayersCount() >= state.getMinPlayers();
    }

    void increaseBlindsLevel() {
        Level levelBeforeIncreasing = pokerState.getCurrentBlindsLevel();
        Level levelAfterIncreasing = pokerState.increaseBlindsLevel();
        log.debug("Level increased. Before: " + levelBeforeIncreasing + " after: " + levelAfterIncreasing);
        historyPersister.blindsIncreased(pokerState.getCurrentBlindsLevel());

        if (!levelAfterIncreasing.isBreak()) {
            /*
             * Schedule next blinds level unless the new level is a break (because then we have to wait for all tables to finish before
             * we start the break and schedule next level).
             */
            scheduleNextBlindsLevel();
        }

        if (finishedBreak(levelBeforeIncreasing, levelAfterIncreasing)) {
            // The break has finished. Tell all tables about the new blinds and tell them to start.
            log.debug("The break has finished, notifying all tables about new blinds and telling them to start.");
            notifyAllTablesOfNewBlinds();
            sendRoundStartToAllTables();
            pokerState.breakFinished();
            setTournamentStatus(RUNNING);
        }
    }

    private boolean finishedBreak(Level levelBeforeIncreasing, Level currentBlindsLevel) {
        return levelBeforeIncreasing.isBreak() && !currentBlindsLevel.isBreak();
    }

    private void openRegistration() {
        log.debug("Opening registration.");
        setTournamentStatus(PokerTournamentStatus.REGISTERING);
    }

    void cancelTournament() {
        log.debug("Cancelling tournament " + pokerState.getHistoricId());
        setTournamentStatus(PokerTournamentStatus.CANCELLED);
        refundPlayers();
        closeMainTournamentSession();
        scheduleTournamentClosing();
    }

    private void refundPlayers() {
        for (MttPlayer player : state.getPlayerRegistry().getPlayers()) {
            unregisterPlayer(player.getPlayerId());
        }
    }

    private void sendRoundStartToAllTables() {
        log.debug("Sending round start to all tables.");
        notifyAllTablesOfNewBlinds();
        mttSupport.sendRoundStartActionToTables(state, state.getTables());
    }

    public void handleOpenSessionResponse(OpenSessionResponse response) {
        log.debug("Open session succeeded: " + response);
        PlayerSessionId sessionId = response.getSessionId();
        if (sessionId.playerId == -1) {
            setTournamentSessionId(sessionId);
        } else {
            pokerState.addBuyInToPrizePool();
            pokerState.addPlayerSession(sessionId);
            pokerState.removePendingRequest(sessionId.playerId);
            historyPersister.playerOpenedSession(sessionId.playerId, sessionId.integrationSessionId);
            MttPlayer tournamentPlayer = pokerState.getTournamentPlayer(sessionId.playerId, state);
            historyPersister.playerRegistered(historicPlayer(tournamentPlayer, sessionId));

            checkIfTournamentShouldBetStartedOrCancelled();
        }
    }

    private void setTournamentSessionId(PlayerSessionId sessionId) {
        historyPersister.setTournamentSessionId(sessionId.integrationSessionId);
        pokerState.setTournamentSessionId(sessionId);
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

    public void closeTournament() {
        log.debug("Closing tournament.");

        /*
         * To future debuggers: there's a (very small) chance that this will cause the tournament to be removed
         * before the "tournament destroyed" messages have been sent to the tables (in which case the tables won't exist).
         * For that to happen the TournamentScanner must run exactly at the same time as this code. To avoid this, we
         * could add some grace time between those events, but for now I've decided it's not that critical if it happens.
         */
        setTournamentStatus(CLOSED);
        notifyTournamentClosed();
    }

    private void notifyTournamentClosed() {
        log.debug("Notifying all tables that tournament is being destroyed.");
        for (Integer tableId : state.getTables()) {
            log.debug("Notifying table " + tableId + " that tournament is being destroyed.");
            TournamentDestroyed destroyed = new TournamentDestroyed();
            GameObjectAction action = new GameObjectAction(tableId);
            action.setAttachment(destroyed);
            notifyTable(tableId, action);
        }
    }

    private void notifyTable(Integer tableId, GameObjectAction action) {
        instance.getMttNotifier().notifyTable(tableId, action);
    }
}
