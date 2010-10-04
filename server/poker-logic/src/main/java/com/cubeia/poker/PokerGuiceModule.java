package com.cubeia.poker;

import com.cubeia.poker.gametypes.TexasHoldem;
import com.cubeia.poker.gametypes.TexasHoldemGame;
import com.google.inject.AbstractModule;

public class PokerGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PokerState.class);
		
		// Bind poker game types
		bind(GameType.class).annotatedWith(TexasHoldemGame.class).to(TexasHoldem.class);
		
	}
}
