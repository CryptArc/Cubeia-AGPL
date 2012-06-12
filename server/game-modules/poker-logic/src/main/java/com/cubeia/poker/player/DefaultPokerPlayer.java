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

package com.cubeia.poker.player;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.blinds.MissedBlindsStatus;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultPokerPlayer implements PokerPlayer {

    private static final Logger log = LoggerFactory.getLogger(DefaultPokerPlayer.class);

    protected static final long serialVersionUID = 74353817602536715L;

    protected ActionRequest actionRequest;

    protected int playerId;

    protected int seatId;

    protected Hand pocketCards = new Hand();

    protected Set<Card> publicPocketCards = new HashSet<Card>();

    protected Set<Card> privatePocketCards = new HashSet<Card>();

    protected boolean hasActed;

    protected boolean hasFolded;

    protected boolean hasOption;

    protected boolean isSitOutNextRound = false;

    protected SitOutStatus sitOutStatus;

    protected boolean hasPostedEntryBet;

    protected boolean exposingPocketCards;

    private long requestedBuyInAmount;

    protected boolean disconnectTimeoutUsed = false;

    /**
     * the unused amount of chips kept by the player
     */
    private long balance = 0;

    private long startingBalance = 0;

    /**
     * the money reserved in wallet but not yet available to the player
     */
    private long balanceNotInHand = 0L;

    /**
     * the amount reserved for a betting action
     */
    protected long betStack = 0;


    private boolean sitInAfterSuccessfulBuyIn;

    private Long sitOutTimestamp;

    /**
     * The next valid raise level for the last performed action
     */
    private long lastRaiseLevel = 0;

    private boolean buyInRequestActive;

    /**
     * Indicates whether this player has any missed blinds.
     */
    private MissedBlindsStatus missedBlindsStatus = MissedBlindsStatus.NOT_ENTERED_YET;

    public DefaultPokerPlayer(int id) {
        playerId = id;
    }

    public String toString() {
        String sitOutSince = sitOutTimestamp == null ? "" : ":" + (System.currentTimeMillis() - sitOutTimestamp + "ms");
        String value = "pid[" + playerId + "] seat[" + seatId + "] " +
                "balance[" + balance + "] balanceNotInHand[" + balanceNotInHand + "] " +
                "buyInRequestActive[" + buyInRequestActive + "] " +
                "requestedBuyInAmount[" + requestedBuyInAmount + "] " +
                "sitout[" + getSitOutStatus() + sitOutSince + "] sitoutstatus[" + sitOutStatus + "] " +
                "folded[" + hasFolded + "] hasActed[" + hasActed + "] allIn[" + isAllIn() + "] " +
                "hasPostedEntryBet[" + hasPostedEntryBet + "]";
        return value;
    }

    public void clearActionRequest() {
        // log.trace("Clear Action for player "+playerId);
        actionRequest = new ActionRequest();
        actionRequest.setPlayerId(playerId);
    }

    public int getId() {
        return playerId;
    }

    public Hand getPocketCards() {
        return new Hand(pocketCards);
    }

    @Override
    public Set<Card> getPublicPocketCards() {
        return new HashSet<Card>(publicPocketCards);
    }

    public Set<Card> getPrivatePocketCards() {
        return privatePocketCards;
    }

    public ActionRequest getActionRequest() {
        return actionRequest;
    }

    public int getSeatId() {
        return seatId;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public boolean isSittingIn() {
        return !isSittingOut();
    }

    @Override
    public boolean isSittingOut() {
        return sitOutStatus == SitOutStatus.SITTING_OUT;
    }

    @Override
    public boolean hasActed() {
        return hasActed;
    }

    @Override
    public boolean hasFolded() {
        return hasFolded;
    }

    @Override
    public void setActionRequest(ActionRequest actionRequest) {
        // log.trace("Setting action request " + actionRequest + " on player " + getTableId());
        this.actionRequest = actionRequest;
    }

    @Override
    public void setHasActed(boolean b) {
        this.hasActed = b;
    }

    @Override
    public void setHasFolded(boolean b) {
        this.hasFolded = b;
    }

    @Override
    public void setHasOption(boolean b) {
        hasOption = b;
    }

    @Override
    public boolean hasOption() {
        return hasOption;
    }

    @Override
    public void addPocketCard(Card card, boolean publicCard) {
        pocketCards.addCard(card);
        if (publicCard) {
            publicPocketCards.add(card);
        } else {
            privatePocketCards.add(card);
        }
    }

    @Override
    public void clearHand() {
        pocketCards.clear();
        publicPocketCards.clear();
        privatePocketCards.clear();
        exposingPocketCards = false;
    }

    @Override
    public void enableOption(PossibleAction option) {
        if (actionRequest == null) {
            actionRequest = new ActionRequest();
            actionRequest.setPlayerId(playerId);
        }

        actionRequest.enable(option);
    }

    @Override
    public SitOutStatus getSitOutStatus() {
        return sitOutStatus;
    }

    @Override
    public boolean hasPostedEntryBet() {
        return hasPostedEntryBet && missedBlindsStatus == MissedBlindsStatus.NO_MISSED_BLINDS;
    }

    /**
     * If the player was not already sitting out we will
     * not only set the sit out status, but also set the
     * time stamp for sitting out to the time when this
     * method was called.
     */
    @Override
    public void setSitOutStatus(SitOutStatus status) {
        this.sitOutStatus = status;
        if (status == SitOutStatus.SITTING_OUT) {
            sitOutTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void setHasPostedEntryBet(boolean status) {
        hasPostedEntryBet = status;
        missedBlindsStatus = MissedBlindsStatus.NO_MISSED_BLINDS;
    }

    @Override
    public void sitIn() {
        sitOutStatus = SitOutStatus.SITTING_IN;
        sitOutTimestamp = null;
    }

    @Override
    public void clearBalance() {
        this.balance = 0;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    @Override
    public void addChips(long chips) {
        checkArgument(chips >= 0, "PokerPlayer[" + playerId + "] - " + String.format("Tried to add negative amount of chips (%d)", chips));
        this.balance += chips;
    }

    /**
     * Takes chips from the given player, without adding them to his bet stack.
     *
     * @param amount
     */
    @Override
    public void takeChips(long amount) {
        checkArgument(amount <= balance, "PokerPlayer[" + playerId + "] - " + String.format("Amount (%d) is bigger than balance (%d)", amount, balance));
        checkArgument(amount >= 0, "Chips must be positive, was " + amount);
        balance -= amount;
    }

    @Override
    public long takeChipsOrGoAllIn(long amount) {
        if (amount >= balance) {
            log.debug("Balance {} >= amount {}, going all-in.", balance, amount);
            amount = balance;
        }
        takeChips(amount);
        return amount;
    }

    @Override
    public void addBet(long bet) {
        checkArgument(bet <= balance, "PokerPlayer[" + playerId + "] - " + String.format("Bet (%d) is bigger than balance (%d)", bet, balance));
        checkArgument(bet >= 0, "Chips must be positive, was " + bet);
        balance -= bet;
        betStack += bet;
    }

    @Override
    public void addBetOrGoAllIn(long amount) {
        if (amount >= balance) {
            log.debug("Balance {} >= amount {}, going all-in.", balance, amount);
            amount = balance;
        }
        addBet(amount);
    }

    @Override
    public void setStartingBalance(long startingBalance) {
        this.startingBalance = startingBalance;
    }

    @Override
    public long getStartingBalance() {
        return startingBalance;
    }

    @Override
    public long getBetStack() {
        return betStack;
    }

    @Override
    public void removeFromBetStack(long amount) {
        if (amount > betStack) {
            throw new IllegalArgumentException("PokerPlayer[" + playerId + "] - " + String.format("Amount to remove from bet (%d) is bigger than betstack (%d)", amount, betStack));
        }
        betStack -= amount;
    }


    @Override
    public void returnBetStackToBalance() {
        balance += betStack;
        betStack = 0;
    }

    @Override
    public void returnBetStackAmountToBalance(long amount) {
        if (amount > betStack) {
            throw new IllegalArgumentException("PokerPlayer[" + playerId + "] - " + String.format("Amount to return from bet (%d) is bigger than betstack (%d)", amount, betStack));
        }
        balance += amount;
        betStack -= amount;
    }

    @Override
    public boolean isAllIn() {
        return getBalance() == 0;
    }

    @Override
    public boolean getSitOutNextRound() {
        return isSitOutNextRound;
    }

    @Override
    public void setSitOutNextRound(boolean b) {
        isSitOutNextRound = b;

    }

    @Override
    public long getBalanceNotInHand() {
        return balanceNotInHand;
    }

    @Override
    public void addNotInHandAmount(long amount) {
        balanceNotInHand += amount;
    }

    @Override
    public boolean commitBalanceNotInHand(long maxBuyIn) {
        boolean hasPending = balanceNotInHand > 0;
        if (hasPending && balance < maxBuyIn) {
            long allowedAmount = maxBuyIn - balance;
            if (balanceNotInHand > allowedAmount) {
                balance += allowedAmount;
                balanceNotInHand -= allowedAmount;
                log.debug("commiting pending balance for player: " + playerId + " committedValue: " + allowedAmount + " new balance: " + balance + " new pending balance: " + balanceNotInHand);
            } else {
                balance += balanceNotInHand;
                log.debug("commiting all pending balance for player: " + playerId + " committedValue: " + balanceNotInHand + " new balance: " + balance + " new pending balance: " + 0);
                balanceNotInHand = 0;

            }
        }
        return hasPending;
    }

    @Override
    public long getPendingBalanceSum() {
        return getBalanceNotInHand() + getRequestedBuyInAmount();
    }

    @Override
    public boolean isSitInAfterSuccessfulBuyIn() {
        return sitInAfterSuccessfulBuyIn;
    }

    @Override
    public void setSitInAfterSuccessfulBuyIn(boolean sitIn) {
        this.sitInAfterSuccessfulBuyIn = sitIn;
    }

    @Override
    public Long getSitOutTimestamp() {
        return sitOutTimestamp;
    }

    @Override
    public boolean isExposingPocketCards() {
        return exposingPocketCards;
    }

    public void setExposingPocketCards(boolean exposingPocketCards) {
        this.exposingPocketCards = exposingPocketCards;
    }

    @Override
    public void resetBeforeNewHand() {
        clearActionRequest();
        clearHand();
        setHasActed(false);
        setHasFolded(false);
        setDisconnectTimeoutUsed(false);
    }

    @Override
    public void setLastRaiseLevel(long amount) {
        this.lastRaiseLevel = amount;
    }

    @Override
    public long getLastRaiseLevel() {
        return lastRaiseLevel;
    }

    @Override
    public long getRequestedBuyInAmount() {
        return requestedBuyInAmount;
    }

    @Override
    public void addRequestedBuyInAmount(long buyInAmount) {
        requestedBuyInAmount += buyInAmount;
        log.debug("added {} as future buy in amount for player {}, total future buy in amount = {}",
                new Object[]{buyInAmount, playerId, requestedBuyInAmount});
    }

    @Override
    public void setRequestedBuyInAmount(long amount) {
        log.debug("setting buy in amount for player {} to: {}, was: {}",
                new Object[]{playerId, amount, requestedBuyInAmount});
        requestedBuyInAmount = amount;
    }

    @Override
    public void clearRequestedBuyInAmountAndRequest() {
        requestedBuyInAmount = 0;
        buyInRequestActive = false;
    }

    @Override
    public void buyInRequestActive() {
        buyInRequestActive = true;
    }

    @Override
    public boolean isBuyInRequestActive() {
        return buyInRequestActive;
    }

    @Override
    public boolean isDisconnectTimeoutUsed() {
        return disconnectTimeoutUsed;
    }

    @Override
    public void setDisconnectTimeoutUsed(boolean disconnectTimeoutUsed) {
        this.disconnectTimeoutUsed = disconnectTimeoutUsed;
    }

    @Override
    public void setMissedBlindsStatus(MissedBlindsStatus missedBlindsStatus) {
        this.missedBlindsStatus = missedBlindsStatus;
    }

    @Override
    public MissedBlindsStatus getMissedBlindsStatus() {
        return missedBlindsStatus;
    }

    @VisibleForTesting
    public void setBalance(long balance) {
        this.balance = balance;
    }
}
