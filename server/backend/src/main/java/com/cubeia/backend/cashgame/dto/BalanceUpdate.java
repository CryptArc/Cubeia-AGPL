package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class BalanceUpdate {

	public final PlayerSessionId playerSessionId;
	public final long balance;
	public final long balanceVersionNumber;

	public BalanceUpdate(PlayerSessionId playerSessionId, long balance,
			long balanceVersionNumber) {
		this.playerSessionId = playerSessionId;
		this.balance = balance;
		this.balanceVersionNumber = balanceVersionNumber;
	}
}
