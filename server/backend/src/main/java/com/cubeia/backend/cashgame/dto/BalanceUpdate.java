package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class BalanceUpdate implements Serializable {

	public final PlayerSessionId playerSessionId;
	public final long balance;
	public final long balanceVersionNumber;

	public BalanceUpdate(PlayerSessionId playerSessionId, long balance,
			long balanceVersionNumber) {
		this.playerSessionId = playerSessionId;
		this.balance = balance;
		this.balanceVersionNumber = balanceVersionNumber;
	}

    @Override
    public String toString() {
        return "BalanceUpdate [playerSessionId=" + playerSessionId + ", balance=" + balance + ", balanceVersionNumber="
            + balanceVersionNumber + "]";
    }
	
	
}
