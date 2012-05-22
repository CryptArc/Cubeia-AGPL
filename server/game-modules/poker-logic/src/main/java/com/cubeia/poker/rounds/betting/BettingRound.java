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
package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundHelper;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.util.ThreadLocalProfiler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

public class BettingRound implements Round, BettingRoundContext {

    private static final long serialVersionUID = -8666356150075950974L;

    private static transient Logger log = LoggerFactory.getLogger(BettingRound.class);

    private PokerContext context;

    private ServerAdapterHolder serverAdapterHolder;
    
    private RoundHelper roundHelper;

    @VisibleForTesting
    protected long highBet = 0;

    @VisibleForTesting
    protected Integer playerToAct = null;

    /**
     * Placeholder for the next valid raise level in the game.
     * This may very well be 0 in cases where it is not set.
     */
    @VisibleForTesting
    protected long nextValidRaiseLevel = 0;

    private final ActionRequestFactory actionRequestFactory;

    private boolean isFinished = false;

    /**
     * Last highest bet that any raise must match
     */
    private long lastBetSize = 0;

    private final PlayerToActCalculator playerToActCalculator;

    private PokerPlayer lastPlayerToPlaceABet;

    private PokerPlayer lastPlayerToBeCalled;

    private FutureActionsCalculator futureActionsCalculator;

    /**
     * Players still in play (not folded or all in) that entered this round.
     */
    @VisibleForTesting
    protected Set<PokerPlayer> playersInPlayAtRoundStart;

    // TODO: Would probably be nice if the playerToActCalculator knew all it needs to know, so we don't need to pass "seatIdToStart.." as well.
    public BettingRound(int seatIdToStartBettingAfter, PokerContext context, ServerAdapterHolder serverAdapterHolder, PlayerToActCalculator playerToActCalculator, ActionRequestFactory actionRequestFactory, FutureActionsCalculator futureActionsCalculator) {

        this.context = context;
        this.serverAdapterHolder = serverAdapterHolder;
        this.futureActionsCalculator = futureActionsCalculator;
        this.playerToActCalculator = playerToActCalculator;
        this.actionRequestFactory = actionRequestFactory;
        this.roundHelper = new RoundHelper(context, serverAdapterHolder);
        lastBetSize = context.getEntryBetLevel();
        initBettingRound(seatIdToStartBettingAfter);
    }

    private void initBettingRound(int seatIdToStartBettingAfter) {
        log.debug("Init new betting round");
        for (PokerPlayer p : context.getPlayersInHand()) {
            /*
                * Initialize the highBet to the highest bet stack of the incoming players.
                * This will be zero on all rounds except when blinds have been posted.
                */
            if (p.getBetStack() > highBet) {
                highBet = p.getBetStack();
            }
            p.clearActionRequest();
            p.setLastRaiseLevel(0);
        }

        playersInPlayAtRoundStart = new HashSet<PokerPlayer>();
        for (PokerPlayer player : context.getPlayersInHand()) {
            log.debug("player {}: folded {}, allIn: {}, hasActed: {}", new Object[]{player.getId(), player.hasFolded(), player.isAllIn(), player.hasActed()});

            if (!player.isAllIn() && !player.hasFolded()) {
                playersInPlayAtRoundStart.add(player);
            }
        }
        log.debug("players in play entering round: {}", playersInPlayAtRoundStart);

        // Check if we should request actions at all
        PokerPlayer p = playerToActCalculator.getFirstPlayerToAct(seatIdToStartBettingAfter, context.getCurrentHandSeatingMap(), context.getCommunityCards());

        log.debug("first player to act = {}", p == null ? null : p.getId());

        // boolean everyoneIsSittingOut = context.isEveryoneSittingOut();

        // if (p == null  ||  allOtherNonFoldedPlayersAreAllIn(p)  ||  everyoneIsSittingOut) {
        if (p == null || allOtherNonFoldedPlayersAreAllIn(p)) {
            // No or only one player can act. We are currently in an all-in show down scenario
            log.debug("No players left to act. We are in an all-in show down scenario");
            notifyAllPlayersOfNoPossibleFutureActions();
            isFinished = true;
        } else {
            requestAction(p);
        }

        // This can be triggered by the if clause above, but also
        // by traversing into requestAction and calling default act on
        // each and every player in sit out scenarios.
        if (isFinished()) {
            roundHelper.scheduleRoundTimeout(context, getServerAdapter());
        }
    }

    @Override
    public String toString() {
        return "BettingRound, isFinished[" + isFinished + "]";
    }

    public void act(PokerAction action) {
        log.debug("Act : " + action);
        ThreadLocalProfiler.add("BettingRound.act");
        PokerPlayer player = context.getPlayerInCurrentHand(action.getPlayerId());

        verifyValidAction(action, player);

        handleAction(action, player);
        getServerAdapter().notifyActionPerformed(action, player);
        getServerAdapter().notifyPlayerBalance(player);

        if (calculateIfRoundFinished()) {
            log.debug("BettingRound is finished");
            isFinished = true;
        } else {
            requestNextAction(player.getSeatId());
        }
    }

    private void requestNextAction(int lastSeatId) {
        PokerPlayer player = playerToActCalculator.getNextPlayerToAct(lastSeatId, context.getCurrentHandSeatingMap());
        if (player == null) {
            log.debug("Setting betting round is finished because there is no player left to act");
            isFinished = true;
            notifyAllPlayersOfNoPossibleFutureActions();
        } else {
            log.debug("Next player to act is: " + player.getId());
            requestAction(player);
        }
    }

    /**
     * Get the player's available actions and send a request to the client
     * or perform default action if the player is sitting out.
     *
     * @param p
     */
    private void requestAction(PokerPlayer p) {
        playerToAct = p.getId();
        if (p.getBetStack() < highBet) {
            p.setActionRequest(actionRequestFactory.createFoldCallRaiseActionRequest(this, p));
        } else {
            ActionRequest ar = actionRequestFactory.createFoldCheckBetActionRequest(this, p);
            p.setActionRequest(ar);
        }

        if (p.isSittingOut()) {
            performDefaultActionForPlayer(p);
        } else {
            roundHelper.requestAction(p.getActionRequest());
            notifyAllPlayersOfPossibleFutureActions(p);
        }
    }

    /**
     * Notify all the other players about their future action options
     * i.e. check next and fold next checkboxes
     *
     * @param excludePlayer player that should get no actions
     */
    private void notifyAllPlayersOfPossibleFutureActions(PokerPlayer excludePlayer) {

        for (PokerPlayer player : context.getCurrentHandPlayerMap().values()) {

            if (player.getId() != excludePlayer.getId()) {
                getServerAdapter().notifyFutureAllowedActions(player, futureActionsCalculator.calculateFutureActionOptionList(player, highBet));
            } else {
                getServerAdapter().notifyFutureAllowedActions(player, Lists.<PokerActionType>newArrayList());
            }
        }

    }

    /**
     * Notify all players that they will not have any future actions in the current round
     * so they can turn of the check, check/fold and fold checkboxes
     */
    private void notifyAllPlayersOfNoPossibleFutureActions() {
        for (PokerPlayer player : context.getCurrentHandPlayerMap().values()) {
            getServerAdapter().notifyFutureAllowedActions(player, Lists.<PokerActionType>newArrayList());
        }
    }


    @VisibleForTesting
    protected boolean calculateIfRoundFinished() {
        Set<PokerPlayer> nonFoldedPlayersLeftInRound = Sets.filter(playersInPlayAtRoundStart, new Predicate<PokerPlayer>() {
            @Override
            public boolean apply(PokerPlayer player) {
                return !player.hasFolded();
            }
        });
        if (nonFoldedPlayersLeftInRound.size() < 2) {
            return true;
        }
        for (PokerPlayer p : context.getPlayersInHand()) {
            if (!p.hasFolded() && !p.hasActed()) {
                return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    protected void handleAction(PokerAction action, PokerPlayer player) {
        switch (action.getActionType()) {
            case CALL:
                long calledAmount = call(player);
                action.setBetAmount(calledAmount);
                break;
            case CHECK:
                check(player);
                break;
            case FOLD:
                fold(player);
                break;
            case RAISE:
                setRaiseByAmount(player, action);
                raise(player, action.getBetAmount());
                break;
            case BET:
                bet(player, action.getBetAmount());
                break;
            default:
                throw new IllegalArgumentException();
        }
        player.setHasActed(true);
    }

    private void verifyValidAction(PokerAction action, PokerPlayer player) {
        if (!action.getPlayerId().equals(playerToAct)) {
            throw new IllegalArgumentException("Expected " + playerToAct + " to act, but got action from:" + player.getId());
        }

        if (!player.getActionRequest().matches(action)) {
            throw new IllegalArgumentException("Player " + player.getId() + " tried to act " + action.getActionType() + " but his options were "
                    + player.getActionRequest().getOptions());
        }

        if (player.hasActed()) {
            throw new IllegalArgumentException("Player has already acted in this BettingRound. Player[" + player + "], action[" + action + "]");
        }
    }

    @VisibleForTesting
    void raise(PokerPlayer player, long amount) {
        if (amount <= highBet) {
            throw new IllegalArgumentException("PokerPlayer[" + player.getId() + "] incorrect raise amount. Highbet[" + highBet + "] amount[" + amount + "]. " +
                    "Amounts must be larger than current highest bet");
        }

        boolean allIn = player.getBalance() + player.getBetStack() - amount <= 0;
        boolean belowMinBet = amount < 2 * lastBetSize;

        // Check if player went all in with a below minimum raise
        if (belowMinBet && allIn) {
            log.debug("Player[" + player.getId() + "] made a below min raise but is allin.");
        } else if (belowMinBet && !allIn) {
            throw new IllegalArgumentException("PokerPlayer[" + player.getId() + "] is not allowed to raise below minimum raise. Highbet[" + highBet + "] amount[" + amount + "] balance[" + player.getBalance() + "] betStack[" + player.getBetStack() + "].");
        } else {
            lastBetSize = amount - highBet;
            nextValidRaiseLevel = highBet + lastBetSize * 2;
            player.setLastRaiseLevel(getNextValidRaiseLevel());
        }

        highBet = amount;
        lastPlayerToBeCalled = lastPlayerToPlaceABet;
        context.callOrRaise();
        lastPlayerToPlaceABet = player;
        player.addBet(highBet - player.getBetStack());
        resetHasActed();

        notifyPotSizeAndRakeInfo();
    }

    private void notifyPotSizeAndRakeInfo() {
        roundHelper.notifyPotSizeAndRakeInfo();
    }

    private void setRaiseByAmount(PokerPlayer player, PokerAction action) {
        action.setRaiseAmount(action.getBetAmount() - highBet);
    }

    @VisibleForTesting
    void bet(PokerPlayer player, long amount) {
        long minAmount = player.getActionRequest().getOption(PokerActionType.BET).getMinAmount();
        if (amount < minAmount) {
            throw new IllegalArgumentException("PokerPlayer[" + player.getId() + "] - " + String.format("Bet (%d) is smaller than minAmount (%d)", amount, minAmount));
        }
        lastBetSize = amount;
        nextValidRaiseLevel = 2 * lastBetSize;
        highBet = highBet + amount;
        lastPlayerToPlaceABet = player;
        player.addBet(highBet - player.getBetStack());
        player.setLastRaiseLevel(getNextValidRaiseLevel());
        resetHasActed();

        notifyPotSizeAndRakeInfo();
    }

    private void resetHasActed() {
        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            if (!p.hasFolded()) {
                p.setHasActed(false);
            }
        }
    }

    private void fold(PokerPlayer player) {
        player.setHasFolded(true);
    }

    private void check(PokerPlayer player) {
        // Nothing to do.
    }

    @VisibleForTesting
    protected long call(PokerPlayer player) {
        long amountToCall = getAmountToCall(player);
        player.addBet(amountToCall);
        lastPlayerToBeCalled = lastPlayerToPlaceABet;
        context.callOrRaise();
        notifyPotSizeAndRakeInfo();
        player.setLastRaiseLevel(getNextValidRaiseLevel());
        return amountToCall;
    }

    /**
     * Returns the amount with which the player has to increase his current bet when doing a call.
     */
    private long getAmountToCall(PokerPlayer player) {
        return Math.min(highBet - player.getBetStack(), player.getBalance());
    }

    public void timeout() {
        PokerPlayer player = playerToAct == null ? null : context.getPlayerInCurrentHand(playerToAct);

        if (player == null || player.hasActed()) {
            // throw new IllegalStateException("Expected " + playerToAct + " to act, but that player can not be found at the table!");
            log.debug("Expected " + playerToAct + " to act, but that player can not be found at the table! I will assume everyone is all in");
            return; // Are we allin?
        }
        setPlayerSitOut(player);
        performDefaultActionForPlayer(player);
    }

    private void setPlayerSitOut(PokerPlayer player) {
        context.setSitOutStatus(player.getId(), SitOutStatus.TIMEOUT);
    }

    private void performDefaultActionForPlayer(PokerPlayer player) {
        log.debug("Perform default action for player sitting out: " + player);
        if (player.getActionRequest().isOptionEnabled(PokerActionType.CHECK)) {
            act(new PokerAction(player.getId(), PokerActionType.CHECK, true));
        } else {
            act(new PokerAction(player.getId(), PokerActionType.FOLD, true));
        }
    }

    public String getStateDescription() {
        return "playerToAct=" + playerToAct + " roundFinished=" + calculateIfRoundFinished();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void visit(RoundVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * True when all other players still in play (except the given one) are all in.
     * Sit out and folded players are not counted.
     */
    public boolean allOtherNonFoldedPlayersAreAllIn(PokerPlayer thisPlayer) {
        for (PokerPlayer player : context.getCurrentHandSeatingMap().values()) {
            boolean self = player.equals(thisPlayer);

            if (!self) {
                boolean notFolded = !player.hasFolded();
                boolean notAllIn = !player.isAllIn();

                if (notFolded && notAllIn) {
                    return false;
                }
            }
        }
        return true;
    }

    public long getHighestBet() {
        return highBet;
    }

    public long getMinBet() {
        return context.getEntryBetLevel();
    }

    public long getSizeOfLastBetOrRaise() {
        return lastBetSize;
    }

    public PokerPlayer getLastPlayerToBeCalled() {
        return lastPlayerToBeCalled;
    }

    public long getNextValidRaiseLevel() {
        return nextValidRaiseLevel;
    }

    @Override
    public boolean isWaitingForPlayer(int playerId) {
        return playerToAct != null && playerId == playerToAct;
    }
    
    private ServerAdapter getServerAdapter() {
        return serverAdapterHolder.get();
    }
}
