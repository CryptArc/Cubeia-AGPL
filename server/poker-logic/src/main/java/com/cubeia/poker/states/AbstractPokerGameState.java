package com.cubeia.poker.states;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;

public abstract class AbstractPokerGameState implements PokerGameState {

	private static final long serialVersionUID = 1L;

	public void timeout(PokerState context) {
		throw new IllegalStateException(this + " is wrong state. Context: "+context);
	}
	
	public void act(PokerAction action, PokerState pokerGame) {
		throw new IllegalStateException("PokerState: "+pokerGame+" Action: "+action);
	}

	public String getStateDescription() {
		return getClass().getName();
	}
}
