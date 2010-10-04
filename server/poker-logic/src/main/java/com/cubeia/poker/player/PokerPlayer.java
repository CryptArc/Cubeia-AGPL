package com.cubeia.poker.player;

import java.io.Serializable;

import ca.ualberta.cs.poker.Card;
import ca.ualberta.cs.poker.Hand;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PossibleAction;

public interface PokerPlayer extends Serializable {

	/**
	 * 
	 * @return a list of cards, never <code>null</code>.
	 */
	public Hand getPocketCards();
	
	public void addPocketCard(Card card);
	
	public void clearHand();
	
	/**
	 * Gets the player's id.
	 * @return
	 */
	public int getId();

	public int getSeatId();

	public long getBetStack();

	public void addBet(long i);

	public void clearActionRequest();

	public void setActionRequest(ActionRequest possibleActions);

	public ActionRequest getActionRequest();

	public void setHasActed(boolean b);

	public void setHasFolded(boolean b);

	public boolean hasFolded();

	public boolean hasActed();

	public void setHasOption(boolean b);
	
	public boolean hasOption();

	public void clearBetStack();

	public void enableOption(PossibleAction option);

	public void setSitOutStatus(SitOutStatus status);

	public SitOutStatus getSitOutStatus();

	public boolean hasPostedEntryBet();

	public void setHasPostedEntryBet(boolean b);

	public boolean isSittingOut();

	public void clearBalance();
	
	public long getBalance();
	
	/**
	 * Sets this player as being in a hand or not.
	 * 
	 * @param isInHand <code>true</code> if the player is in a hand, <code>false</code> when the player is not in a hand
	 */
	public void setPlayerIsInHand(boolean isInHand);

	/**
	 * Checks if this player is in a hand.
	 * @return <code>true</code> if the player is in a hand, <code>false</code> otherwise
	 */
	public boolean isInHand();

	/**
	 * Adds (or removes) chips to the player's chip stack.
	 * 
	 * @param chips chips to add (positive) or remove (negative)
	 */
	public void addChips(long chips);

	public void commitBetStack();
	
	public boolean isAllIn();
	
	public void sitIn();
	
	public void addReturnedChips(long chips);

	public long getReturnedChips();
	
}