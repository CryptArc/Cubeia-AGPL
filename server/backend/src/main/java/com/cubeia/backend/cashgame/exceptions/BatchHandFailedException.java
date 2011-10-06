package com.cubeia.backend.cashgame.exceptions;

public class BatchHandFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public BatchHandFailedException(String message) {
		super(message);
	}
	
	public BatchHandFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
