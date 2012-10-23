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

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundHelper;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.util.ThreadLocalProfiler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * The minimum betting amount in this round.
     */
    private long minBet;

    private Predicate<PokerPlayer> nonFolded = new PokerPredicate() {

		private static final long serialVersionUID = -2668212962504220510L;

    };

    private static class  PokerPredicate implements Predicate<PokerPlayer>, Serializable {
        private static final long serialVersionUID = 3189327257295089272L;
        @Override
        public boolean apply(PokerPlayer player) {
            return !player.hasFolded();
        }
    }


    // TODO: Would probably be nice if the playerToActCalculator knew all it needs to know, so we don't need to pass "seatIdToStart.." as well.
    public BettingRound(int seatIdToStartBettingAfter,
                        PokerContext context,
                        ServerAdapterHolder serverAdapterHolder,
                        PlayerToActCalculator playerToActCalculator,
                        ActionRequestFactory actionRequestFactory,
                        FutureActionsCalculator futureActionsCalculator,
                        int minBet) {
        this.context = context;
        this.serverAdapterHolder = serverAdapterHolder;
        this.futureActionsCalculator = futureActionsCalculator;
        this.playerToActCalculator = playerToActCalculator;
        this.actionRequestFactory = actionRequestFactory;
        this.roundHelper = new RoundHelper(context, serverAdapterHolder);
        this.minBet = minBet;
        this.lastBetSize = minBet;
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
        makeSureHighBetIsNotSmallerThanBigBlind();

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

    private void makeSureHighBetIsNotSmallerThanBigBlind() {
        if (highBet > 0 && highBet < context.getSettings().getBigBlindAmount()) {
            highBet = context.getSettings().getBigBlindAmount();
        }
    }

    @Override
    public String toString() {
        return "BettingRound, isFinished[" + isFinished + "]";
    }

    public boolean act(PokerAction action) {
        log.debug("Act : " + action);
        ThreadLocalProfiler.add("BettingRound.act");
        PokerPlayer player = context.getPlayerInCurrentHand(action.getPlayerId());

        if (!isValidAction(action, player)) {
            return false;
        }

        boolean handled = handleAction(action, player);
        if (handled) {
            getServerAdapter().notifyActionPerformed(action, player);
            getServerAdapter().notifyPlayerBalance(player);

            if (calculateIfRoundFinished()) {
                log.debug("BettingRound is finished");
                isFinished = true;
            } else {
                requestNextAction(player.getSeatId());
            }
        }
        return handled;
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
     * @param p the player to request an action for
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
        if (Sets.filter(playersInPlayAtRoundStart, nonFolded).size() < 2) {
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
    protected boolean handleAction(PokerAction action, PokerPlayer player) {
        boolean handled;
        switch (action.getActionType()) {
            case CALL:
                long amountToCall = getAmountToCall(player);
                handled = call(player);
                action.setBetAmount(amountToCall);
                break;
            case CHECK:
                handled = check(player);
                break;
            case FOLD:
                handled = fold(player);
                break;
            case RAISE:
                setRaiseByAmount(action);
                handled = raise(player, action.getBetAmount());
                break;
            case BET:
                handled = bet(player, action.getBetAmount());
                break;
            default:
                log.warn("Can't handle " + action.getActionType());
                handled = false;
                break;
        }
        if (handled) {
            player.setHasActed(true);
        }
        return handled;
    }

    private boolean isValidAction(PokerAction action, PokerPlayer player) {
        if (!action.getPlayerId().equals(playerToAct)) {
            log.warn("Expected " + playerToAct + " to act, but got action from:" + player.getId());
            return false;
        }

        if (!player.getActionRequest().matches(action)) {
            log.warn("Player " + player.getId() + " tried to act " + action.getActionType() + " but his options were "
                    + player.getActionRequest().getOptions());
            return false;
        }

        if (player.hasActed()) {
            log.warn("Player has already acted in this BettingRound. Player[" + player + "], action[" + action + "]");
            return false;
        }
        return true;
    }

    @VisibleForTesting
    boolean raise(PokerPlayer player, long amount) {
        if (amount < highBet) {
            log.warn("PokerPlayer[" + player.getId() + "] incorrect raise amount. Highbet[" + highBet + "] amount[" + amount + "]. " +
                    "Amounts must be larger than current highest bet");
            return false;
        }

        boolean allIn = player.getBalance() + player.getBetStack() - amount <= 0;
        boolean belowMinBet = amount < 2 * lastBetSize;

        // Check if player went all in with a below minimum raise
        if (belowMinBet && allIn) {
            log.debug("Player[" + player.getId() + "] made a below min raise but is allin.");
        } else if (belowMinBet && !allIn) {
            log.warn("PokerPlayer[" + player.getId() + "] is not allowed to raise below minimum raise. Highbet[" + highBet + "] amount[" + amount + "] balance[" + player.getBalance() + "] betStack[" + player.getBetStack() + "].");
            return allIn;
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
        return true;
    }

    private void notifyPotSizeAndRakeInfo() {
        roundHelper.notifyPotSizeAndRakeInfo();
    }

    private void setRaiseByAmount(PokerAction action) {
        action.setRaiseAmount(action.getBetAmount() - highBet);
    }

    @VisibleForTesting
    boolean bet(PokerPlayer player, long amount) {
        long minAmount = player.getActionRequest().getOption(PokerActionType.BET).getMinAmount();
        if (amount < minAmount) {
            log.warn("PokerPlayer[" + player.getId() + "] - " + String.format("Bet (%d) is smaller than minAmount (%d)", amount, minAmount));
            return false;
        }
        lastBetSize = amount;
        nextValidRaiseLevel = 2 * lastBetSize;
        highBet = highBet + amount;
        lastPlayerToPlaceABet = player;
        player.addBet(highBet - player.getBetStack());
        player.setLastRaiseLevel(getNextValidRaiseLevel());
        resetHasActed();

        notifyPotSizeAndRakeInfo();
        return true;
    }

    private void resetHasActed() {
        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            if (!p.hasFolded()) {
                p.setHasActed(false);
            }
        }
    }

    private boolean fold(PokerPlayer player) {
        player.setHasFolded(true);
        return true;
    }

    private boolean check(PokerPlayer player) {
        // Nothing to do.
        return true;
    }

    @VisibleForTesting
    protected boolean call(PokerPlayer player) {
        long amountToCall = getAmountToCall(player);
        player.addBet(amountToCall);
        lastPlayerToBeCalled = lastPlayerToPlaceABet;
        context.callOrRaise();
        notifyPotSizeAndRakeInfo();
        player.setLastRaiseLevel(getNextValidRaiseLevel());
        return true;
    }

    /**
     * Returns the amount with which the player has to increase his current bet when doing a call.
     */
    @VisibleForTesting
    long getAmountToCall(PokerPlayer player) {
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
        if (context.setSitOutStatus(player.getId(), SitOutStatus.SITTING_OUT)) {
            getServerAdapter().notifyPlayerStatusChanged(player.getId(), PokerPlayerStatus.SITOUT, true);
        }
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
        return minBet;
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
