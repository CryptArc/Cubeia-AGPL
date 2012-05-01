package com.cubeia.backend.cashgame.exceptions;

import com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode;

public class ReserveFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public final ErrorCode errorCode;
    public final boolean playerSessionNeedsToBeClosed;

    public ReserveFailedException(String message, ErrorCode errorCode, boolean playerSessionNeedsToBeClosed) {
        super(message);
        this.errorCode = errorCode;
        this.playerSessionNeedsToBeClosed = playerSessionNeedsToBeClosed;
    }

    public ReserveFailedException(String message, Throwable cause, ErrorCode errorCode, boolean playerSessionNeedsToBeClosed) {
        super(message, cause);
        this.errorCode = errorCode;
        this.playerSessionNeedsToBeClosed = playerSessionNeedsToBeClosed;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
