package com.cubeia.games.poker;

import com.cubeia.firebase.guice.game.EventScoped;
import com.cubeia.games.poker.adapter.FirebaseServerAdapter;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.handler.PokerHandler;
import com.google.inject.AbstractModule;

public class IntegrationGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		 bind(ActionCache.class).in(EventScoped.class);
		 bind(StateInjector.class);
		 bind(FirebaseServerAdapter.class).in(EventScoped.class);
		 bind(PokerHandler.class).in(EventScoped.class);
	}

}
