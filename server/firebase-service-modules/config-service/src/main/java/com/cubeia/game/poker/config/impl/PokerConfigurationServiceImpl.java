package com.cubeia.game.poker.config.impl;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.server.conf.Configurable;
import com.cubeia.firebase.api.server.conf.ConfigurationException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.config.ClusterConfigProviderContract;
import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.firebase.guice.inject.Service;

import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.game.poker.config.api.PokerTournamentActivatorConfig;
import com.google.inject.Singleton;

@Singleton
public class PokerConfigurationServiceImpl implements com.cubeia.firebase.api.service.Service, PokerConfigurationService {
	
	@Service(proxy=true)
	private ClusterConfigProviderContract clusterConfig;
	
	@Log4j
	private Logger log;
	
	public void init(ServiceContext con) throws SystemException { }

	public void start() { }
	
	@Override
	public PokerTournamentActivatorConfig getTournamentActivatorConfig() {
		return config(PokerTournamentActivatorConfig.class);
	}

	public void stop() {}
	
	public void destroy() {}

	
	// --- PRIVATE METHODS --- //
	
	private <T extends Configurable> T config(Class<T> clazz) {
		try {
			return clusterConfig.getConfiguration(clazz, null);
		} catch (ConfigurationException e) {
			log.error("Failed to read configuration", e);
			return null; // Er...
		}
	}
	
}