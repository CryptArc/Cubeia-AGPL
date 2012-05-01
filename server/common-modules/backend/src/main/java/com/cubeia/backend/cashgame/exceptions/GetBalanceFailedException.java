package com.cubeia.backend.cashgame.exceptions;

public class GetBalanceFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public GetBalanceFailedException(String message) {
        super(message);
    }

    public GetBalanceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
