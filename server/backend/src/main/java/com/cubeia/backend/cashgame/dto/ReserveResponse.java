package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class ReserveResponse {

	public final BalanceUpdate balanceUpdate;
	public final int amountReserved;

	public ReserveResponse(BalanceUpdate balanceUpdate, int amountReserved) {
		this.balanceUpdate = balanceUpdate;
		this.amountReserved = amountReserved;
	}
	
	public PlayerSessionId getPlayerSessionId() {
		return balanceUpdate.playerSessionId;
	}
}
