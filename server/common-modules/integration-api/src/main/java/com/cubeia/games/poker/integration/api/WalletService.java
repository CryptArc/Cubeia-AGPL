package com.cubeia.games.poker.integration.api;

import com.cubeia.games.poker.common.Money;

public interface WalletService {

	/**
	 * Get the balance of the poker wallet. 
	 */
	public Money getBalance(long playerId);
	
	/**
	 * Withdraw money from the poker wallet to 
	 * use in the poker server. This will be called
	 * on buy-ins, registrations, re-buy etc. 
	 */
	public Response withdraw(Request req);
	
	/**
	 * Deposit money to the poker wallet. This 
	 * will be called on cash-out, tournament wins
	 * etc.
	 */
	public Response deposit(DepositRequest req);
	
}
