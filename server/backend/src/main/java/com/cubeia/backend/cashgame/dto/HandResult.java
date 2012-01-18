package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class HandResult implements Serializable {
	public final PlayerSessionId playerSession;
	
	/**
	 * Sum of all players bets in the hand.
	 */
	public final Money aggregatedBet;
	
	/**
	 * Player winnings including own bets.
	 */
	public final Money win;
	public final Money rake;
	
	public final int seat;
	public final Money startingBalance;
	
	public HandResult(PlayerSessionId playerSession, Money aggregatedBet,
	    Money win, Money rake, int seat, Money startingBalance) {

		this.playerSession = playerSession;
		this.aggregatedBet = aggregatedBet;
		this.win = win;
		this.rake = rake;
		this.seat = seat;
		this.startingBalance = startingBalance;
	}
}
