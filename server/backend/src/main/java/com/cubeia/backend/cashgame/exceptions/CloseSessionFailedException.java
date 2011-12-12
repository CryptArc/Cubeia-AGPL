package com.cubeia.backend.cashgame.exceptions;

public class CloseSessionFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CloseSessionFailedException(String message) {
		super(message);
	}
	
	public CloseSessionFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
