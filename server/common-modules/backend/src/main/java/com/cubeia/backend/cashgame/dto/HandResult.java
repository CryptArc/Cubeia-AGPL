package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class HandResult implements Serializable {
	private final PlayerSessionId playerSession;
	
	/**
	 * Sum of all players bets in the hand.
	 */
	private final Money aggregatedBet;
	
	/**
	 * Player winnings including own bets.
	 */
	private final Money win;
	private final Money rake;
	
	private final int seat;
	private final Money startingBalance;
	
	public HandResult(PlayerSessionId playerSession, Money aggregatedBet,
	    Money win, Money rake, int seat, Money startingBalance) {

		this.playerSession = playerSession;
		this.aggregatedBet = aggregatedBet;
		this.win = win;
		this.rake = rake;
		this.seat = seat;
		this.startingBalance = startingBalance;
	}

    public PlayerSessionId getPlayerSession() {
        return playerSession;
    }

    public Money getAggregatedBet() {
        return aggregatedBet;
    }

    public Money getWin() {
        return win;
    }

    public Money getRake() {
        return rake;
    }

    public int getSeat() {
        return seat;
    }

    public Money getStartingBalance() {
        return startingBalance;
    }
}
