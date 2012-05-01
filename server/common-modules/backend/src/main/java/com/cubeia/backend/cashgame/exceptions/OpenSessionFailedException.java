package com.cubeia.backend.cashgame.exceptions;

import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse.ErrorCode;

public class OpenSessionFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public final ErrorCode errorCode;

    public OpenSessionFailedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OpenSessionFailedException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
