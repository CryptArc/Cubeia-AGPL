package com.cubeia.backend.cashgame.dto;

public class OpenSessionFailedResponse {
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
