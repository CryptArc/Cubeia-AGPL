package com.cubeia.games.poker;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TournamentProcessor;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import com.cubeia.firebase.api.game.table.TableListener;
import com.cubeia.firebase.guice.game.ConfigurationAdapter;
import com.cubeia.poker.PokerState;

public class IntegrationGuiceConfig extends ConfigurationAdapter {


	private static transient Logger log = Logger.getLogger(IntegrationGuiceConfig.class);
	
	@Override
	public Class<? extends GameProcessor> getGameProcessorClass() {
		return Processor.class;
	}

	@Override
	public Class<? extends TableListener> getTableListenerClass() {
		log.warn("Guice Config - getTableListenerClass");
		return PokerTableListener.class;
	}

	@Override
	public Class<?> getGameStateClass() {
		return PokerState.class;
	}

	@Override
	public Class<? extends TableInterceptor> getTableInterceptorClass() {
		return PokerTableInterceptor.class;
	}

	@Override
	public Class<? extends TournamentProcessor> getTournamentProcessorClass() {
		return Processor.class;
	}
}
