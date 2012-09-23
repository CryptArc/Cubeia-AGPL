package com.cubeia.game.poker.config.api;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.games.poker.common.Money;

public interface PokerConfigurationService extends Contract { 
	
	public PokerActivatorConfig getActivatorConfig();
	
	public PokerSystemConfig getSystemConfig();
	
	public Money createSystemMoney(long amount);

}