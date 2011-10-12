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
