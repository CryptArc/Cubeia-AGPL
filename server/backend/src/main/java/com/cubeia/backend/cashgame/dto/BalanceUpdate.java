package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class BalanceUpdate {

	public final PlayerSessionId playerSessionId;
	public final long newBalance;
	public final long balanceVersionNumber;

	public BalanceUpdate(PlayerSessionId playerSessionId, long newBalance,
			long balanceVersionNumber) {
		this.playerSessionId = playerSessionId;
		this.newBalance = newBalance;
		this.balanceVersionNumber = balanceVersionNumber;
	}
}
