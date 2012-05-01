package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AnnounceTableFailedResponse implements Serializable {
    private final ErrorCode errorCode;
    private final String message;

    public AnnounceTableFailedResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public enum ErrorCode {
        EXTERNAL_CALL_FAILED, UNKOWN_PLATFORM_TABLE_ID, WALLET_CALL_FAILED;
    }

    @Override
    public String toString() {
        return "AnnounceTableFailedResponse [errorCode=" + getErrorCode()
                + ", message=" + getMessage() + "]";
    }
}
