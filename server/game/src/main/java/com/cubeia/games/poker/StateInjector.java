package com.cubeia.games.poker;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.adapter.FirebaseServerAdapter;
import com.cubeia.poker.PokerState;
import com.google.inject.Inject;

/**
 * TODO: The functionality of this class should be modeled 
 * in Guice modules instead.
 *  
 * @author Fredrik
 */
public class StateInjector {

	@Inject
	FirebaseServerAdapter adapter;
	
	/**
	 * Inject the server adapter to the game logic.
	 * @param table
	 */
	public void injectAdapter(Table table) {
		PokerState state = (PokerState)table.getGameState().getState();
		state.setServerAdapter(adapter);
	}
}
