package com.cubeia.poker.action;

import java.io.Serializable;


/**
 * Represents an action performed by a player.
 *
 */
public class PokerAction implements Serializable {

	private static final long serialVersionUID = -3457732987197089379L;

	private final Integer playerId;
	
	private final PokerActionType actionType;

    private boolean timeout = false;
    
    /** Amount is in cents for money */
    private long betAmount = -1;
    
    /** In cents for money */
    private long raiseAmount = -1;
    
    /** In cents for money */
    private long stackAmount = -1;
    

	public PokerAction(Integer playerId, PokerActionType actionType) {
		this.playerId = playerId;
		this.actionType = actionType;
	}

	/**
     * 
     * @param playerToAct
     * @param check
     * @param amount
     */
    public PokerAction(int playerToAct, PokerActionType check, long amount) {
        this(playerToAct, check);
        this.betAmount = amount;
    }
    
	/**
	 * 
	 * @param playerToAct
	 * @param check
	 * @param timeout, true if this is a result of a timeout
	 */
	public PokerAction(int playerToAct, PokerActionType check, boolean timeout) {
        this(playerToAct, check);
        this.timeout = timeout;
    }

    public String toString() {
	    return "PokerAction - pid["+playerId+"] type["+actionType+"] timeout["+timeout+"] amount["+betAmount+"]";
	}
	
	public Integer getPlayerId() {
		return playerId;
	}

	public PokerActionType getActionType() {
		return actionType;
	}

	public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(long amount) {
        this.betAmount = amount;
    }

    /**
	 * Is this action a result of a timeout?
	 * 
	 * @return true if this is a timeout
	 */
    public boolean isTimeout() {
        return timeout;
    }

	public long getRaiseAmount() {
		return raiseAmount;
	}

	public void setRaiseAmount(long raiseAmount) {
		this.raiseAmount = raiseAmount;
	}

	public long getStackAmount() {
		return stackAmount;
	}

	public void setStackAmount(long stackAmount) {
		this.stackAmount = stackAmount;
	}

}
