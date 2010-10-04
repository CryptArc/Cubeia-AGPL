package com.cubeia.poker.action;

import java.io.Serializable;


/**
 * Represents one possible action that a player can make.
 *
 */
public class PossibleAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private final PokerActionType actionType;
	
	private final long minAmount;
	
	private final long maxAmount;
	
	/**
	 * Constructs a possible action with min and max amount set to 0.
	 * 
	 * For example, this could be a check or a fold.
	 * 
	 * @param actionType
	 */
	public PossibleAction(PokerActionType actionType) {
		this(actionType, 0, 0);
	}
	
	/**
	 * Constrcuts a possible action with min and max set to amount.
	 * 
	 * This could for examble be a bet or a raise in fixed limit poker.
	 * 
	 * @param actionType
	 * @param amount
	 */
	public PossibleAction(PokerActionType actionType, long amount) {
		this(actionType, amount, amount);
	}	
	
	/**
	 * Constructs a possible action.
	 * 
	 * @param actionType
	 * @param minAmount
	 * @param maxAmount
	 */
	public PossibleAction(PokerActionType actionType, long minAmount, long maxAmount) {
		this.actionType = actionType;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	public boolean allows(PokerActionType option) {
		return actionType == option;
	}

	public long getMaxAmount() {
		return maxAmount;
	}
	
	public long getMinAmount() {
		return minAmount;
	}

	public PokerActionType getActionType() {
		return actionType;
	}
	
	@Override
	public String toString() {
		return String.format("[Action: %s Min: %d Max: %d]", actionType, minAmount, maxAmount);
	}
}
