package com.cubeia.games.poker.integration.api;

import com.cubeia.games.poker.common.Money;

public class DepositRequest extends Request {
	
	private Money rakeTurnover; // total rake contribution during session
	private Money betTurnover; // total bet during session
	
}
