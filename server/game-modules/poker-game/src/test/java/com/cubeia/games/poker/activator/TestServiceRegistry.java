package com.cubeia.games.poker.activator;

import org.mockito.Mockito;

import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.service.ServiceRegistryAdapter;
import com.cubeia.game.poker.config.api.PokerConfigurationService;

public class TestServiceRegistry extends ServiceRegistryAdapter {
	
	public TestServiceRegistry() {
		super.addImplementation(CashGamesBackendContract.class, Mockito.mock(CashGamesBackendContract.class));
		super.addImplementation(PokerConfigurationService.class, Mockito.mock(PokerConfigurationService.class));
	}
}