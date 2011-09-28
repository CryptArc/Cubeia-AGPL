package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class ReserveRequest {

	public final PlayerSessionId playerSessionId;
	public final int roundNumber;
	public final int amount;

	public ReserveRequest(PlayerSessionId playerSessionId, int roundNumber, int amount) {
		this.playerSessionId = playerSessionId;
		this.roundNumber = roundNumber;
		this.amount = amount;
	}
}
