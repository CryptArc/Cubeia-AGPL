package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class HandResult {
	public final PlayerSessionId playerSession;
	public final long aggregatedBet;
	public final long win;
	public final long rake;
	
	public HandResult(PlayerSessionId playerSession, long aggregatedBet,
			long win, long rake) {

		this.playerSession = playerSession;
		this.aggregatedBet = aggregatedBet;
		this.win = win;
		this.rake = rake;
	}
}
