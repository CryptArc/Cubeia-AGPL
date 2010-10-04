package com.cubeia.poker;

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public abstract class AbstractTexasHandTester extends GuiceTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game.setAnteLevel(500);
	}
	
	protected void act(int playerId, PokerActionType actionType) {
		act(playerId, actionType, mockServerAdapter.getActionRequest().getOption(actionType).getMinAmount());
	}
	
	protected void act(int playerId, PokerActionType actionType, long amount) {
		PokerAction action = new PokerAction(playerId, actionType);
		action.setBetAmount(amount);
		game.act(action);
	}	

	protected void addPlayers(PokerState game, PokerPlayer[] p) {
		for (PokerPlayer pl : p) {
			game.addPlayer(pl);
		}
	}
	
}
