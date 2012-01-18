package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OpenSessionFailedResponse implements Serializable {
	private final ErrorCode errorCode;
	private final String message;
	private final int playerId;

	public OpenSessionFailedResponse(ErrorCode errorCode, String message, int playerId) {
		this.errorCode = errorCode;
		this.message = message;
		this.playerId = playerId;
	}

	public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public enum ErrorCode {
		UNKOWN_PLATFORM_TABLE_ID, WALLET_CALL_FAILED;
	}
}
