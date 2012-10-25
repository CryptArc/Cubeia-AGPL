package com.cubeia.games.poker.integration.api;

public interface UserService {

	/**
	 * Verify a user name and cookie. This will be
	 * called for clients logging in via a web browser. 
	 */
	public UserInformation validateCookie(String username, String cookie);
	
	/**
	 * Verify a user name and credentials. This will be
	 * called for non-web clients. 
	 */
	// public UserInformation validateLogin(String username, String password, byte[] credentials);
	
}
