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

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class PokerTournamentTest extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game.setTournamentTable(true);
		game.setTournamentId(1);
	}
	
	public void testTournamentHand() {
		MockPlayer[] mp = TestUtils.createMockPlayers(4);
		int[] p = TestUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		TestUtils.addPlayers(game, mp);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.startHand();

		// Blinds are auto.
		game.timeout();
		game.timeout();

		assertAllPlayersHaveCards(mp, 2);

		assertEquals(0, game.getCommunityCards().size());

		// Check that tournament players don't sit out.
		game.timeout();
		game.timeout();
		game.timeout();
		
		assertTrue(game.isFinished());
		
		// Next round starts
		game.startHand();
		assertAllPlayersHaveCards(mp, 0);

		// Blinds
		game.timeout();
		game.timeout();
		
		assertAllPlayersHaveCards(mp, 2);
		
		// No response again.
		game.timeout();
		game.timeout();
		game.timeout();
		
		assertTrue(game.isFinished());		
	}
	
	public void testBigBlindMovedFromTheTable() {
		MockPlayer[] mp = TestUtils.createMockPlayers(4);
		int[] p = TestUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		TestUtils.addPlayers(game, mp);

		
		
		// Force start
		game.startHand();

		// Blinds are auto.
		game.timeout();
		game.timeout();

		// Check that tournament players don't sit out.
		TestUtils.act(game, p[3], PokerActionType.FOLD);
		TestUtils.act(game, p[0], PokerActionType.FOLD);
		TestUtils.act(game, p[1], PokerActionType.FOLD);
		
		game.removePlayer(p[2]);
		
		game.startHand();
		
		// P3 should post bb here.
		game.timeout();

		// Check that p0 is up to act
		assertEquals(mp[0].getId(), mockServerAdapter.getLastActionRequest().getPlayerId());
	}	
	
	public void assertAllPlayersHaveCards(PokerPlayer[] p, int expectedNumberOfCards) {
		for (PokerPlayer pl : p) {
			assertEquals(expectedNumberOfCards, pl.getPocketCards().getCards().size());
		}
	}	
}
