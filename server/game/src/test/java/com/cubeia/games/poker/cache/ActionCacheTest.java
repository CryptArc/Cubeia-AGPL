package com.cubeia.games.poker.cache;

import java.util.List;

import junit.framework.TestCase;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;

public class ActionCacheTest extends TestCase {
	
	private ActionCache cache = new ActionCache();
	
	public void testAddAction() throws Exception {
		GameAction action = new GameDataAction(11, 1);
		cache.addAction(1, action);
		
		assertEquals(1, cache.cache.size());
		List<GameAction> state = cache.getActions(1);
		assertEquals(1, state.size());
		
		GameAction action2 = new GameDataAction(22, 1);
		cache.addAction(1, action2);
		GameAction action3 = new GameDataAction(33, 1);
		cache.addAction(1, action3);
		
		assertEquals(1, cache.cache.size());
		state = cache.getActions(1);
		assertEquals(3, state.size());
		
		assertEquals(11, ((GameDataAction)state.get(0)).getPlayerId());
		assertEquals(22, ((GameDataAction)state.get(1)).getPlayerId());
		assertEquals(33, ((GameDataAction)state.get(2)).getPlayerId());
		
		cache.clear(1);
		
		assertEquals(0, cache.cache.size());
		state = cache.getActions(1);
		assertEquals(0, state.size());
	}
	
}
