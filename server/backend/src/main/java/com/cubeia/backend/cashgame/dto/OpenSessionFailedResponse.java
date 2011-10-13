package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OpenSessionFailedResponse implements Serializable {
	public final ErrorCode errorCode;
	public final String message;

	public OpenSessionFailedResponse(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public enum ErrorCode {
		A, B, C;
	}
}
