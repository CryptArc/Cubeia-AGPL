package com.cubeia.backend.cashgame.dto;

public class AnnounceTableFailedResponse {
	public final ErrorCode errorCode;
	public final String message;

	public AnnounceTableFailedResponse(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public enum ErrorCode {
		A, B, C;
	}
}
