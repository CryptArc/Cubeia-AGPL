package com.cubeia.backend.cashgame.exceptions;

public class CloseTableFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CloseTableFailedException(String message) {
		super(message);
	}
	
	public CloseTableFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
