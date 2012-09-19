package com.cubeia.game.poker.config.impl;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceServiceHandler;
import com.cubeia.game.poker.config.api.PokerConfigurationService;

public class PokerConfigurationServiceHandler extends GuiceServiceHandler {

	@Override
	protected Configuration getConfiguration() {
		return new Configuration() {
			
			@Override
			public ContractsConfig getServiceContract() {
				return new ContractsConfig(PokerConfigurationServiceImpl.class, PokerConfigurationService.class);
			}
		};
	}
}
