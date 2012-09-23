package com.cubeia.games.poker.activator;

import com.cubeia.poker.PokerState;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class InjectorPokerStateCreator implements PokerStateCreator {

	@Inject
	private Injector injector;
	
	@Override
	public PokerState newPokerState() {
		return injector.getInstance(PokerState.class); 
	}
}
