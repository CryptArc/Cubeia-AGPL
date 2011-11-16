package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class HandResult implements Serializable {
	public final PlayerSessionId playerSession;
	
	/**
	 * Sum of all players bets in the hand.
	 */
	public final long aggregatedBet;
	
	/**
	 * Player winnings including own bets.
	 */
	public final long win;
	public final long rake;
	
	public final int seat;
	public final long startingBalance;
	
	public HandResult(PlayerSessionId playerSession, long aggregatedBet,
			long win, long rake, int seat, long startingBalance) {

		this.playerSession = playerSession;
		this.aggregatedBet = aggregatedBet;
		this.win = win;
		this.rake = rake;
		this.seat = seat;
		this.startingBalance = startingBalance;
	}
}
