package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class CloseSessionRequest {

	public final PlayerSessionId playerSessionId;
	public final int roundNumber;

	public CloseSessionRequest(PlayerSessionId playerSessionId, int roundNumber) {
		this.playerSessionId = playerSessionId;
		this.roundNumber = roundNumber;
	}
}
