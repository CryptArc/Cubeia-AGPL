package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CloseSessionRequest implements Serializable {

    private final PlayerSessionId playerSessionId;
    private final int roundNumber;

    public CloseSessionRequest(PlayerSessionId playerSessionId, int roundNumber) {
        this.playerSessionId = playerSessionId;
        this.roundNumber = roundNumber;
    }

    public PlayerSessionId getPlayerSessionId() {
        return playerSessionId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
