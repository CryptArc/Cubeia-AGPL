package com.cubeia.backend.cashgame.exceptions;

import com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode;

public class ReserveFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public final ErrorCode errorCode;

	public ReserveFailedException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ReserveFailedException(String message, Throwable cause, ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
