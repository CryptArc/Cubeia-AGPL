package com.cubeia.games.poker;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.SeatRequest;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import com.cubeia.poker.PokerState;
import com.google.inject.Inject;

public class PokerTableInterceptor implements TableInterceptor {

    @SuppressWarnings("unused")
    private static final transient Logger log = Logger.getLogger(PokerTableInterceptor.class);
    
    @Inject
    StateInjector stateInjector;
    
    @Inject
    PokerState state;

    public InterceptionResponse allowJoin(Table table, SeatRequest request) {
    	stateInjector.injectAdapter(table);
		return new InterceptionResponse(true, -1);
	}

	
	/**
	 * We will flag the player as disconnected only since we need to hold the
	 * player at the table until the end of next hand.
	 */
	public InterceptionResponse allowLeave(Table table, int playerId) {
		stateInjector.injectAdapter(table); // TODO: Fix this with Guice logic module
		if (state.getGameState().getClass() == PokerState.NOT_STARTED.getClass()) {
			// No hand running, let him go...
			return new InterceptionResponse(true, -1);
		} else {
			// Hand running, set to disconnected only
		    state.playerIsSittingOut(playerId);
			return new InterceptionResponse(false, -1);
		}
	}

	public InterceptionResponse allowReservation(Table table, SeatRequest request) {
		stateInjector.injectAdapter(table);
		return new InterceptionResponse(true, -1);
	}
}
