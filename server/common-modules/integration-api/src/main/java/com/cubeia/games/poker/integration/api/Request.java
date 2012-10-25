package com.cubeia.games.poker.integration.api;

import com.cubeia.games.poker.common.Money;

public class Request {

	private long playerId; // player
	private Money amount; // amount to withdraw or deposit
	private Session session; // table or tournament
	
}
