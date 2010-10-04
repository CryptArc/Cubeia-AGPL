package com.cubeia.poker.states;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;

public class PlayingState extends AbstractPokerGameState {

	private static final long serialVersionUID = 7076228045164551068L;

	public String toString() {
	    return "PlayingState";
	}
	   
	@Override
	public void act(PokerAction action, PokerState pokerGame) {
		pokerGame.getGameType().act(action);
	}
	
	@Override
	public void timeout(PokerState pokerGame) {
		pokerGame.getGameType().timeout();
	}
}
