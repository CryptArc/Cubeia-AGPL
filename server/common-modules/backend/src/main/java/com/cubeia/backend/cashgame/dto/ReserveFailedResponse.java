package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ReserveFailedResponse implements Serializable {

    private final ErrorCode errorCode;
    private final String message;
    private final PlayerSessionId sessionId;
    private final boolean playerSessionNeedsToBeClosed;

    public ReserveFailedResponse(PlayerSessionId sessionId, ErrorCode errorCode, String message, boolean playerSessionNeedsToBeClosed) {
        this.sessionId = sessionId;
        this.errorCode = errorCode;
        this.message = message;
        this.playerSessionNeedsToBeClosed = playerSessionNeedsToBeClosed;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public PlayerSessionId getSessionId() {
        return sessionId;
    }

    public boolean isPlayerSessionNeedsToBeClosed() {
        return playerSessionNeedsToBeClosed;
    }

    public enum ErrorCode {
        AMOUNT_TOO_HIGH, UNSPECIFIED_FAILURE, SESSION_NOT_OPEN, MAX_LIMIT_REACHED;
    }
}
