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

package com.cubeia.poker.rounds.blinds;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.blinds.BlindsCalculator;
import com.cubeia.poker.blinds.EntryBetType;
import com.cubeia.poker.blinds.EntryBetter;
import com.cubeia.poker.blinds.MissedBlind;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.BlindsInfo;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundHelper;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.util.PokerUtils;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

public class BlindsRound implements Round {

    private static final long serialVersionUID = -6452364533249060511L;

    private static transient Logger log = Logger.getLogger(BlindsRound.class);

    private BlindsState currentState;

    private BlindsInfo blindsInfo = new BlindsInfo();

    private BlindsInfo previousBlindsInfo;

    private boolean isTournamentBlinds;
    
    private PokerContext context;
    
    private ServerAdapterHolder serverAdapterHolder;

    private BlindsCalculator blindsCalculator;

    private RoundHelper roundHelper;

    public static final BlindsState WAITING_FOR_SMALL_BLIND_STATE = new WaitingForSmallBlindState();

    public static final BlindsState WAITING_FOR_BIG_BLIND_STATE = new WaitingForBigBlindState();

    public static final BlindsState WAITING_FOR_ENTRY_BET_STATE = new WaitingForEntryBetState();

    public static final BlindsState FINISHED_STATE = new FinishedState();

    public static final BlindsState CANCELED_STATE = new CanceledState();

    private SortedMap<Integer, PokerPlayer> sittingInPlayers;

    private Queue<EntryBetter> entryBetters;

    private int pendingEntryBetterId;

    public BlindsRound(PokerContext context, ServerAdapterHolder serverAdapterHolder, BlindsCalculator blindsCalculator) {
        this.serverAdapterHolder = serverAdapterHolder;
        this.blindsCalculator = blindsCalculator;
        this.roundHelper = new RoundHelper(context, serverAdapterHolder);
        this.isTournamentBlinds = context.isTournamentBlinds();
        this.context = context;
        this.sittingInPlayers = getSittingInPlayers(context.getCurrentHandSeatingMap());
        this.previousBlindsInfo = context.getBlindsInfo();
        blindsInfo.setAnteLevel(context.getAnteLevel());
        clearPlayerActionOptions();
        initBlinds();
        if (blindsInfo.hasDeadSmallBlind()) {
            currentState = WAITING_FOR_BIG_BLIND_STATE;
        } else {
            currentState = WAITING_FOR_SMALL_BLIND_STATE;
        }
    }

    private void clearPlayerActionOptions() {
        SortedMap<Integer, PokerPlayer> seatingMap = context.getCurrentHandSeatingMap();
        for (PokerPlayer p : seatingMap.values()) {
            p.clearActionRequest();
        }
    }

    private void initBlinds() {
        com.cubeia.poker.blinds.BlindsInfo newBlindsInfo = blindsCalculator.initializeBlinds(convertBlindsInfo(), context.getPlayerMap().values());
        if (newBlindsInfo != null) {
            setNewBlindsInfo(newBlindsInfo);
            markMissedBlinds(blindsCalculator.getMissedBlinds());
            entryBetters = blindsCalculator.getEntryBetters();

            if (newBlindsInfo.getSmallBlindPlayerId() != -1) {
                requestSmallBlind(getPlayerInSeat(newBlindsInfo.getSmallBlindSeatId()));
            } else {
                requestBigBlind(getPlayerInSeat(newBlindsInfo.getBigBlindSeatId()));
            }
        } else {
            throw new RuntimeException("Could not initialize blinds. Not enough players. Players sitting in: " + sittingInPlayers);
        }
    }

    private void markMissedBlinds(List<MissedBlind> missedBlinds) {
        for (MissedBlind missed : missedBlinds) {
            log.info("Settings missed blinds status to " + missed.getMissedBlindsStatus() + " for player " + missed.getPlayer().getPlayerId());
            context.getPlayer(missed.getPlayer().getPlayerId()).setMissedBlindsStatus(missed.getMissedBlindsStatus());
        }
    }

    private void setNewBlindsInfo(com.cubeia.poker.blinds.BlindsInfo newBlindsInfo) {
        blindsInfo.setDealerButtonSeatId(newBlindsInfo.getDealerSeatId());
        // Small blind
        if (newBlindsInfo.getSmallBlindPlayerId() != -1) {
            blindsInfo.setSmallBlind(getPlayerInSeat(newBlindsInfo.getSmallBlindSeatId()));
        } else {
            blindsInfo.setHasDeadSmallBlind(true);
        }
        blindsInfo.setSmallBlindPlayerId(newBlindsInfo.getSmallBlindPlayerId());

        // Big blind
        blindsInfo.setBigBlindSeatId(newBlindsInfo.getBigBlindSeatId());
        blindsInfo.setBigBlindPlayerId(newBlindsInfo.getBigBlindPlayerId());
    }

    private com.cubeia.poker.blinds.BlindsInfo convertBlindsInfo() {
        return new com.cubeia.poker.blinds.BlindsInfo(previousBlindsInfo.getDealerButtonSeatId(),
                previousBlindsInfo.getSmallBlindSeatId(), previousBlindsInfo.getBigBlindSeatId(), previousBlindsInfo.getBigBlindPlayerId());
    }

    private boolean firstHandOnTable() {
        return !previousBlindsInfo.isDefined() || !atLeastOnePlayerHasEntered();
    }

    private boolean atLeastOnePlayerHasEntered() {
        boolean result = false;

        for (PokerPlayer player : sittingInPlayers.values()) {
            if (player.hasPostedEntryBet()) {
                result = true;
                break;
            }
        }

        return result;
    }

    private SortedMap<Integer, PokerPlayer> getSittingInPlayers(SortedMap<Integer, PokerPlayer> sortedMap) {
        SortedMap<Integer, PokerPlayer> copy = new TreeMap<Integer, PokerPlayer>(sortedMap);
        Iterator<PokerPlayer> iterator = copy.values().iterator();
        while (iterator.hasNext()) {
            PokerPlayer player = iterator.next();
            if (player.isSittingOut()) {
                iterator.remove();
            }
        }
        return copy;
    }

    private void moveFromNonHeadsUpToHeadsUp() {
        // Moving from non heads up to heads up.
        PokerPlayer bigBlind = getSittingInPlayerInSeatAfter(previousBlindsInfo.getBigBlindSeatId());
        setBigBlind(bigBlind);

        PokerPlayer smallBlind = getSittingInPlayerInSeatAfter(bigBlind.getSeatId());
        moveDealerButtonToSeatId(smallBlind.getSeatId());
        requestSmallBlind(smallBlind);
    }

    private void initNonHeadsUpHand() {
        log.debug("Initializing non heads up hand on table");
        moveDealerButtonToSeatId(previousBlindsInfo.getSmallBlindSeatId());
        PokerPlayer smallBlind = getPlayerInSeat(previousBlindsInfo.getBigBlindSeatId());
        PokerPlayer bigBlind = getSittingInPlayerInSeatAfter(previousBlindsInfo.getBigBlindSeatId());
        setBigBlind(bigBlind);

        if (smallBlindStillSeated(smallBlind)) {
            requestSmallBlind(smallBlind);
        } else {
            handleDeadSmallBlind();
            requestBigBlind(bigBlind);
        }
    }

    private void handleDeadSmallBlind() {
        PokerPlayer player = context.getPlayerInCurrentHand(previousBlindsInfo.getBigBlindPlayerId());

        if (player != null) {
            if (isSittingOut(player)) {
                player.setSitOutStatus(SitOutStatus.MISSED_SMALL_BLIND);
            }
        }
        // Always remember the small blind seat id anyway.
        blindsInfo.setSmallBlindSeatId(previousBlindsInfo.getBigBlindSeatId());
        blindsInfo.setHasDeadSmallBlind(true);
    }

    private void initHeadsUpHand() {
        // Keeping heads up logic.
        moveDealerButtonToSeatId(previousBlindsInfo.getBigBlindSeatId());
        PokerPlayer smallBlind = getPlayerInSeat(previousBlindsInfo.getBigBlindSeatId());
        PokerPlayer bigBlind = getSittingInPlayerInSeatAfter(previousBlindsInfo.getBigBlindSeatId());
        setBigBlind(bigBlind);

        if (smallBlindStillSeated(smallBlind)) {
            requestSmallBlind(smallBlind);
        } else {
            handleDeadSmallBlind();
            requestBigBlind(bigBlind);
        }
    }

    private void moveFromHeadsUpToNonHeadsUp() {
        // Turning off heads up logic.
        moveDealerButtonToSeatId(previousBlindsInfo.getSmallBlindSeatId());
        PokerPlayer smallBlind = getPlayerInSeat(previousBlindsInfo.getBigBlindSeatId());
        PokerPlayer bigBlind = getSittingInPlayerInSeatAfter(previousBlindsInfo.getBigBlindSeatId());
        setBigBlind(bigBlind);
        if (smallBlindStillSeated(smallBlind)) {
            requestSmallBlind(smallBlind);
        } else {
            handleDeadSmallBlind();
            requestBigBlind(bigBlind);
        }
    }

    private void initFirstHandOnTable() {
        // This is the first hand on this table.
        if (numberPlayersSittingIn() > 2) {
            initFirstNonHeadsUpHand();
        } else if (numberPlayersSittingIn() == 2) {
            initFirstHeadsUpHand();
        } else {
            throw new RuntimeException("Don't know how to start a hand with less than two players.");
        }
    }

    private void setBigBlind(PokerPlayer bigBlind) {
        blindsInfo.setBigBlind(bigBlind);
    }

    private void requestSmallBlind(PokerPlayer smallBlind) {
        getBlindsInfo().setSmallBlind(smallBlind);
        smallBlind.enableOption(new PossibleAction(PokerActionType.SMALL_BLIND, blindsInfo.getAnteLevel() / 2));
        smallBlind.enableOption(new PossibleAction(PokerActionType.DECLINE_ENTRY_BET));
        if (isTournamentBlinds()) {
            roundHelper.scheduleTimeoutForAutoAction();
        } else {
            roundHelper.requestAction(smallBlind.getActionRequest());
        }
    }

    private ServerAdapter getServerAdapter() {
        return serverAdapterHolder.get();
    }

    private void requestBigBlind(PokerPlayer bigBlind) {
        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            p.clearActionRequest();
        }

        bigBlind.enableOption(new PossibleAction(PokerActionType.BIG_BLIND, blindsInfo.getAnteLevel()));
        bigBlind.enableOption(new PossibleAction(PokerActionType.DECLINE_ENTRY_BET));
        if (isTournamentBlinds()) {
            roundHelper.scheduleTimeoutForAutoAction();
        } else {
            roundHelper.requestAction(bigBlind.getActionRequest());
        }
    }

    private void initFirstHand(Iterator<PokerPlayer> iterator) {
        setAllPlayersToNoMissedBlinds();
        PokerPlayer smallBlind = iterator.next();

        // The small blind seat id can be set immediately.
        getBlindsInfo().setSmallBlind(smallBlind);
        requestSmallBlind(smallBlind);

        PokerPlayer bigBlind = iterator.next();
        setBigBlind(bigBlind);
    }

    private void setAllPlayersToNoMissedBlinds() {
        for (PokerPlayer p : context.getCurrentHandPlayerMap().values()) {
            p.setHasPostedEntryBet(true);
        }
    }

    private void initFirstHeadsUpHand() {
        log.debug("Initializing first heads up hand on table");
        Collection<PokerPlayer> players = sittingInPlayers.values();
        PokerPlayer firstPlayer = players.iterator().next();
        moveDealerButtonToSeatId(firstPlayer.getSeatId());
        // Fetching a new iterator, so that the dealer will become the small blind.
        initFirstHand(players.iterator());
    }

    private void initFirstNonHeadsUpHand() {
        log.debug("Initializing first non heads up hand on table");
        Iterator<PokerPlayer> iterator = sittingInPlayers.values().iterator();
        PokerPlayer firstPlayer = iterator.next();
        moveDealerButtonToSeatId(firstPlayer.getSeatId());
        initFirstHand(iterator);
    }

    private PokerPlayer getSittingInPlayerInSeatAfter(int seatId) {
        return PokerUtils.getElementAfter(seatId, sittingInPlayers);
    }

    private boolean isSittingOut(PokerPlayer player) {
        return player == null || player.isSittingOut();
    }

    private void moveDealerButtonToSeatId(int newDealerSeatId) {
        blindsInfo.setDealerButtonSeatId(newDealerSeatId);
        if (!isTournamentBlinds) {
            markPlayersWhoMissedBlinds(previousBlindsInfo.getDealerButtonSeatId(), blindsInfo.getDealerButtonSeatId());
        }
        getServerAdapter().notifyDealerButton(blindsInfo.getDealerButtonSeatId());
    }

    private void markPlayersWhoMissedBlinds(int buttonFromSeatId, int buttonToSeatId) {
        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            if (PokerUtils.isBetween(p.getSeatId(), buttonFromSeatId, buttonToSeatId)
                    && !p.hasPostedEntryBet()
                    && p.getSitOutStatus() != SitOutStatus.NOT_ENTERED_YET) {
                p.setSitOutStatus(SitOutStatus.MISSED_BIG_BLIND);
            }
        }
    }

    private boolean smallBlindStillSeated(PokerPlayer smallBlind) {
        if (smallBlind == null) {
            return false;
        }

        PokerPlayer playerInSmallBlindSeat = getPlayerInSeat(smallBlind.getSeatId());
        return smallBlind.getId() == playerInSmallBlindSeat.getId() && smallBlind.hasPostedEntryBet();
    }

    private PokerPlayer getPlayerInSeat(int seatId) {
        return context.getCurrentHandSeatingMap().get(seatId);
    }

    private int numberPlayersSittingIn() {
        return sittingInPlayers.size();
    }

    public void act(PokerAction action) {
        switch (action.getActionType()) {
            case SMALL_BLIND:
                currentState.smallBlind(action.getPlayerId(), context, this);
                break;
            case BIG_BLIND:
                currentState.bigBlind(action.getPlayerId(), context, this);
                break;
            case DECLINE_ENTRY_BET:
                currentState.declineEntryBet(action.getPlayerId(), context, this);
                break;
            default:
                log.info(action.getActionType() + " is not legal here");
                return;
        }
        context.getPlayerInCurrentHand(action.getPlayerId()).clearActionRequest();
        PokerPlayer player = context.getPlayerInCurrentHand(action.getPlayerId());
        getServerAdapter().notifyActionPerformed(action, player);
        getServerAdapter().notifyPlayerBalance(player);
    }

    public BlindsInfo getBlindsInfo() {
        return blindsInfo;
    }

    public void smallBlindPosted() {
        this.currentState = WAITING_FOR_BIG_BLIND_STATE;
        PokerPlayer bigBlind = getPlayerInSeat(blindsInfo.getBigBlindSeatId());
        requestBigBlind(bigBlind);
    }

    public void smallBlindDeclined(PokerPlayer player) {
        sittingInPlayers.remove(player.getSeatId());
        notifyPlayerSittingOut(player.getId(), SitOutStatus.MISSED_SMALL_BLIND);
        if (numberPlayersSittingIn() >= 2) {
            PokerPlayer bigBlind = getPlayerInSeat(blindsInfo.getBigBlindSeatId());
            requestBigBlind(bigBlind);
            currentState = WAITING_FOR_BIG_BLIND_STATE;
        } else {
            currentState = CANCELED_STATE;
        }
    }

    private void notifyPlayerSittingOut(int playerId, SitOutStatus status) {
        log.debug("Notify player sitout: " + playerId);
        if (context != null) {
            context.setSitOutStatus(playerId, status);
            getServerAdapter().notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITOUT, true);
        } else {
            log.warn("Trying to notify sit out pid[" + playerId + "] on NULL state!");
        }
    }

    public void bigBlindPosted() {
        if (!isTournamentBlinds() && thereAreUnEnteredPlayersBetweenBigBlindAndDealerButton()) {
            log.debug("There are unentered players, requesting entry bet");
            this.currentState = WAITING_FOR_ENTRY_BET_STATE;
            requestNextEntryBet();
        } else {
            currentState = FINISHED_STATE;
        }
    }

    private void requestNextEntryBet() {
        if (!entryBetters.isEmpty()) {
            EntryBetter entryBetter = entryBetters.poll();
            PokerPlayer player = context.getPlayer(entryBetter.getPlayer().getPlayerId());
            if (entryBetter.getEntryBetType() == EntryBetType.BIG_BLIND) {
                log.debug("Requesting entry big blind from " + player);
                requestBigBlind(player);
            } else if (entryBetter.getEntryBetType() == EntryBetType.DEAD_SMALL_BLIND) {
                requestDeadSmallBlind(player);
            } else if (entryBetter.getEntryBetType() == EntryBetType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND) {
                requestBigBlindPlusDeadSmallBlind(player);
            }
            pendingEntryBetterId = player.getId();
        } else {
            log.warn("No more entry betters!");
        }
    }

    private void requestBigBlindPlusDeadSmallBlind(PokerPlayer player) {
        log.debug("Requesting big blind plus dead small blind from " + player);
    }

    private void requestDeadSmallBlind(PokerPlayer player) {
        log.debug("Requesting dead small blind from " + player);
    }

    private EntryBetter getNextEntryBetter() {
        return entryBetters.poll();
    }

    private boolean thereAreUnEnteredPlayersBetweenBigBlindAndDealerButton() {
        return !entryBetters.isEmpty();
    }

    private boolean isPlayerBetweenBigBlindAndDealerButton(PokerPlayer player) {
        return PokerUtils.isBetween(player.getSeatId(), blindsInfo.getBigBlindSeatId(), blindsInfo.getDealerButtonSeatId());
    }

    public void bigBlindDeclined(PokerPlayer player) {
        log.debug(player + " declined big blind.");
        sittingInPlayers.remove(player.getSeatId());
        notifyPlayerSittingOut(player.getId(), SitOutStatus.MISSED_BIG_BLIND);
        PokerPlayer nextBig = getSittingInPlayerInSeatAfter(player.getSeatId());
        if (nextBig != null && playerIsSittingInAndNotSmallBlind(nextBig)) {
            requestBigBlind(nextBig);
            // Set the new player as big blind in the context
            blindsInfo.setBigBlind(nextBig);
        } else {
            currentState = CANCELED_STATE;
        }
    }

    public void entryBetDeclined(PokerPlayer player) {
        sittingInPlayers.remove(player.getSeatId());
        notifyPlayerSittingOut(player.getId(), SitOutStatus.NOT_ENTERED_YET);
        if (thereAreUnEnteredPlayersBetweenBigBlindAndDealerButton()) {
            log.debug("There are unentered players, requesting entry bet");
            this.currentState = WAITING_FOR_ENTRY_BET_STATE;
            requestNextEntryBet();
        } else {
            currentState = FINISHED_STATE;
        }
    }

    private boolean playerIsSittingInAndNotSmallBlind(PokerPlayer nextBig) {
        if (blindsInfo.getSmallBlindPlayerId() == nextBig.getId()) {
            return false;
        }

        return true;
    }

    public void timeout() {
        currentState.timeout(context, this);
    }

    public boolean isTournamentBlinds() {
        return isTournamentBlinds;
    }

    public String getStateDescription() {
        return currentState != null ? currentState.getClass().getName() : "currentState=null";
    }

    public boolean isFinished() {
        return currentState.isFinished();
    }

    public boolean isCanceled() {
        return currentState.isCanceled();
    }

    public void visit(RoundVisitor visitor) {
        visitor.visit(this);
    }

    // FIXME: Actually check who we are waiting for
    @Override
    public boolean isWaitingForPlayer(int playerId) {
        return false;
    }

    public int getPendingEntryBetterId() {
        return pendingEntryBetterId;
    }
}
