package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class ReserveResponse implements Serializable {

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
