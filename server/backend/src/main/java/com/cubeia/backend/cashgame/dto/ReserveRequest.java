package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class ReserveRequest implements Serializable {

	private final PlayerSessionId playerSessionId;
	private final int roundNumber;
	private final Money amount;

	public ReserveRequest(PlayerSessionId playerSessionId, int roundNumber, Money amount) {
		this.playerSessionId = playerSessionId;
		this.roundNumber = roundNumber;
		this.amount = amount;
	}

    public PlayerSessionId getPlayerSessionId() {
        return playerSessionId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Money getAmount() {
        return amount;
    }
}
