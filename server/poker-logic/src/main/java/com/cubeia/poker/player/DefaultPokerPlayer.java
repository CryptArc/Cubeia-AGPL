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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;

public class DefaultPokerPlayer implements PokerPlayer {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultPokerPlayer.class);
	
	protected static final long serialVersionUID = 74353817602536715L;

	protected int betStack = 0;
	
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

	protected boolean isSittingOut;
	
	protected boolean exposingPocketCards;

	private long balance = 0;

	private long returnedChips = 0;

    private long pendingBalance;

    private boolean sitInAfterSuccessfulBuyIn;
    
    private Long sitOutTimestamp;
	
	public DefaultPokerPlayer(int id) {
		playerId = id;
	}
	
	public String toString() {
		String sitOutSince = sitOutTimestamp == null ? "" : ":"+(System.currentTimeMillis()-sitOutTimestamp+"ms");
	    String value = "pid["+playerId+"] seat["+seatId+"] " +
	    		"balance["+balance+"] pendingBalance["+pendingBalance+"] " +
	    		"sitout["+isSittingOut+sitOutSince+"] sitoutstatus["+sitOutStatus+"] " +
				"folded["+hasFolded+"] hasActed["+hasActed+"]";
	    return value;
	}
	
	public void addBet(long bet) {
		betStack += bet;
		if (betStack > balance) {
			throw new IllegalArgumentException("PokerPlayer["+playerId+"] - "+String.format("Bet (%d) is bigger than balance (%d)", bet, balance));
		}
	}

	public void clearActionRequest() {
		// log.trace("Clear Action for player "+playerId);
		actionRequest = new ActionRequest();
		actionRequest.setPlayerId(playerId);
	}

	public void clearBetStack() {
		betStack = 0;
		returnedChips = 0;
	}

	public long getBetStack() {
		return betStack;
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

	public boolean hasActed() {
		return hasActed;
	}

	public boolean hasFolded() {
		return hasFolded;
	}

	public void setActionRequest(ActionRequest actionRequest) {
		// log.trace("Setting action request " + actionRequest + " on player " + getId());
		this.actionRequest = actionRequest;
	}

	public void setHasActed(boolean b) {
		this.hasActed = b;
	}

	public void setHasFolded(boolean b) {
		this.hasFolded = b;
	}

	public void setHasOption(boolean b) {
		hasOption = b;
	}

	public boolean hasOption() {
		return hasOption;
	}

	public void addPocketCard(Card card, boolean publicCard) {
		pocketCards.addCard(card);
		if (publicCard) {
		    publicPocketCards.add(card);
		} else {
			privatePocketCards.add(card);
		}
	}

	public void clearHand() {
		pocketCards.clear();
		publicPocketCards.clear();
		privatePocketCards.clear();
		exposingPocketCards = false;
	}

	public void enableOption(PossibleAction option) {
		if (actionRequest == null) {
			actionRequest = new ActionRequest();
			actionRequest.setPlayerId(playerId);
		}

		actionRequest.enable(option);
	}

	public SitOutStatus getSitOutStatus() {
		return sitOutStatus;
	}

	public boolean hasPostedEntryBet() {
		return hasPostedEntryBet;
	}

	public void setSitOutStatus(SitOutStatus status) {
		this.isSittingOut = true;
		this.sitOutStatus = status;
		sitOutTimestamp = System.currentTimeMillis();
	}

	public void setHasPostedEntryBet(boolean status) {
		hasPostedEntryBet = status;
	}

	public boolean isSittingOut() {
		return isSittingOut;
	}
	
	public void sitIn() {
		this.isSittingOut = false;
		sitOutTimestamp = null;
	}

	public void clearBalance() {
		this.balance = 0;
		this.returnedChips = 0;
	}

	public long getBalance() {
		return balance - betStack + returnedChips;
	}

	public void addChips(long chips) {
		this.balance += chips;
	}

	public void commitBetStack() {
		this.balance -= betStack;
		//this.balance += returnedChips;
		
		clearBetStack();
	}
	
	public void setBalance(long balance) {
		this.balance = balance;
	}

	@Override
	public boolean isAllIn() {
		return getBalance() == 0;
	}

	@Override
	public void addReturnedChips(long chips) {
		returnedChips = chips;
		
	}

	@Override
	public long getReturnedChips() {
		return returnedChips;
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
	public long getPendingBalance() {
	    return pendingBalance;
	}
	
	@Override
	public void addPendingAmount(long amount) {
	    pendingBalance += amount;
	}
	
    @Override
    public boolean commitPendingBalance(long maxBuyIn) {
    	boolean hasPending = pendingBalance > 0;
    	if (hasPending && balance < maxBuyIn) {
    		long allowedAmount = maxBuyIn - balance;
    		if (pendingBalance > allowedAmount) {
    			balance += allowedAmount;
    			pendingBalance -= allowedAmount;
    		} else {
    			balance += pendingBalance;
    			pendingBalance = 0;
    		}
    	}
    	return hasPending;
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
}
