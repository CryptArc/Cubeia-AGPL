package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.PlayerSessionId;


@SuppressWarnings("serial")
public class ReserveFailedResponse implements Serializable {
    
	public final ErrorCode errorCode;
	public final String message;
	public final PlayerSessionId sessionId;

	public ReserveFailedResponse(PlayerSessionId sessionId, ErrorCode errorCode, String message) {
        this.sessionId = sessionId;
        this.errorCode = errorCode;
		this.message = message;
	}

	public enum ErrorCode {
		A, B, C,
		AMOUNT_TOO_HIGH;
	}
}
