package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OpenSessionFailedResponse implements Serializable {
	public final ErrorCode errorCode;
	public final String message;
	public final int playerId;

	public OpenSessionFailedResponse(ErrorCode errorCode, String message, int playerId) {
		this.errorCode = errorCode;
		this.message = message;
		this.playerId = playerId;
	}

	public enum ErrorCode {
		UNKOWN_PLATFORM_TABLE_ID, WALLET_CALL_FAILED;
	}
}
