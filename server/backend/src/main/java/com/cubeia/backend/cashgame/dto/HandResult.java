package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class HandResult implements Serializable {
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
