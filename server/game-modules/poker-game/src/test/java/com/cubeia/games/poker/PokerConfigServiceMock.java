package com.cubeia.games.poker;

import com.cubeia.game.poker.config.api.PokerActivatorConfig;
import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.game.poker.config.api.PokerSystemConfig;
import com.cubeia.games.poker.common.Money;

public class PokerConfigServiceMock implements PokerConfigurationService {

	@Override
	public PokerActivatorConfig getActivatorConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PokerSystemConfig getSystemConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Money createSystemMoney(long amount) {
		return new Money(amount, "EUR", 2);
	}
}
