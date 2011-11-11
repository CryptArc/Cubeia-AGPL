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

package com.cubeia.poker.rounds.betting;

import junit.framework.TestCase;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestListener;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;

public class AllInTest extends TestCase implements TestListener {

	private ActionRequest requestedAction;

	private MockGame game;

	private BettingRound round;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game = new MockGame();
		game.listeners.add(this);
		
	}
	
	public void testAllInBet() {
		// NOTE: This implies no limit betting.
		MockPlayer[] p = TestUtils.createMockPlayers(2, 500);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator());

		PossibleAction bet = requestedAction.getOption(PokerActionType.BET);
		assertEquals(500, bet.getMaxAmount());
		assertEquals(500, p[1].getBalance());
		act(p[1], PokerActionType.BET, bet.getMaxAmount());
		assertEquals(0, p[1].getBalance());
		assertTrue(p[1].isAllIn());
		
		
		
	}
	
	
	// HELPERS
	
	private void act(MockPlayer player, PokerActionType action, long amount) {
		PokerAction a = new PokerAction(player.getId(), action);
		a.setBetAmount(amount);
		round.act(a);
	}	

	public void notifyActionRequested(ActionRequest r) {
		this.requestedAction = r;
	}

}
