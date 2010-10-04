package com.cubeia.poker;

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class TestUtils {
	
	public MockPlayer[] createMockPlayers(int n) {
		return createMockPlayers(n, 5000);
	}
	
	public MockPlayer[] createMockPlayers(int n, long balance) {
		MockPlayer[] r = new MockPlayer[n];

		for (int i = 0; i < n; i++) {
			r[i] = new MockPlayer(i);
			r[i].setSeatId(i);
			r[i].setBalance(balance);
		}

		return r;		
	}	

	public int[] createPlayerIdArray(MockPlayer[] mp) {
		int[] ids = new int[mp.length];
		
		for (int i = 0; i < mp.length; i++) {
			ids[i] = mp[i].getId();
		}
		
		return ids;
	}
	
	public void addPlayers(PokerState game, PokerPlayer[] p, long startingChips) {
		for (PokerPlayer pl : p) {
			game.addPlayer(pl);
			game.addChips(pl.getId(), startingChips);
		}
	}
	
	public void addPlayers(PokerState game, PokerPlayer[] p) {
		addPlayers(game, p, 10000);
	}

	public void act(PokerState game, int playerId, PokerActionType actionType) {
		game.act(new PokerAction(playerId, actionType));
	}


}
