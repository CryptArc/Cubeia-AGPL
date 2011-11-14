package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AnnounceTableFailedResponse implements Serializable {
	public final ErrorCode errorCode;
	public final String message;

	public AnnounceTableFailedResponse(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public enum ErrorCode {
		EXTERNAL_CALL_FAILED, UNKOWN_PLATFORM_TABLE_ID, WALLET_CALL_FAILED;
	}

	@Override
	public String toString() {
		return "AnnounceTableFailedResponse [errorCode=" + errorCode
				+ ", message=" + message + "]";
	}
}
