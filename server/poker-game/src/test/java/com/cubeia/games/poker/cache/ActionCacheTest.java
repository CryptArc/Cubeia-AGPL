/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
