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

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;

/**
 * Integration test for poker logic.
 */
public class TournamentTablePokerLogicTest extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testAddPlayerInTheMiddleOfHand() {
	    game.setTournamentTable(true);
		MockPlayer[] mp = testUtils.createMockPlayers(4, 500);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		addPlayers(game, mp, 4);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.startHand();

		// Blinds
		game.timeout();
		game.timeout();
		assertTrue(mp[2].hasOption());
		assertAllPlayersHaveCards(mp, 2, 4);

		assertEquals(0, game.getCommunityCards().size());

		// Pre flop round
		assertEquals(103, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		assertTrue(mp[3].hasActed());
		assertEquals(100, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CHECK);

		// Trigger deal community cards
		game.timeout();
		
		assertEquals(3, game.getCommunityCards().size());

		// Player joins in the middle of the hand.
		MockPlayer p5 = new MockPlayer(5);
		p5.setSeatId(5);		
		game.addPlayer(p5);
		
		// Check that we still only have 4 players.
		assertEquals(4, game.getCurrentHandPlayerMap().size());
		
		// Flop round
		act(PokerActionType.BET);
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);

		// Trigger deal community cards
		game.timeout();
		assertEquals(4, game.getCommunityCards().size());
		
		// Turn round
		act(PokerActionType.CHECK);
		act(PokerActionType.BET);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.CALL);

		// Trigger deal community cards
		game.timeout();
		assertEquals(5, game.getCommunityCards().size());

		// River round
		act(PokerActionType.CHECK);
		act(PokerActionType.BET);
		act(PokerActionType.FOLD);

		// Assertions
		assertTrue(game.isFinished());
		
		// Start next hand.
		game.startHand();
		
		// Check that we now have 5 players
		assertEquals(5, game.getSeatedPlayers().size());
		
		// Auto blinds, as usual
		game.timeout();
		game.timeout();
		
		// Run next hand
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		
		assertTrue(game.isFinished());
		
		// Still 5 players.
		assertEquals(5, game.getSeatedPlayers().size());
		assertEquals(5, game.getCurrentHandSeatingMap().size());
	}
	
	public void testSitAndLeave() {
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		addPlayers(game, mp, 4);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.timeout();

		// Blinds
		act(PokerActionType.SMALL_BLIND);

		assertTrue(mp[2].isActionPossible(PokerActionType.BIG_BLIND));
		assertEquals(102, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.BIG_BLIND);
		
		assertTrue(mp[2].hasOption());
		assertAllPlayersHaveCards(mp, 2, 4);

		assertEquals(0, game.getCommunityCards().size());

		// Pre flop round
		assertEquals(103, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		assertTrue(mp[3].hasActed());
		assertEquals(100, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CHECK);

		// Trigger deal community cards
		game.timeout();
		assertEquals(3, game.getCommunityCards().size());

		// Player joins in the middle of the hand.
		MockPlayer p5 = new MockPlayer(5);
		p5.setSeatId(5);		
		game.addPlayer(p5);
		
		// Check that we still only have 4 players.
		assertEquals(5, game.getSeatedPlayers().size());
		assertEquals(4, game.getCurrentHandSeatingMap().size());
		
		game.removePlayer(p5.getId());
		assertEquals(4, game.getSeatedPlayers().size());
	}
	
	public void testSmallBlindTimeoutOnTournamentTable() {
	    game.setTournamentTable(true);
        MockPlayer[] mp = testUtils.createMockPlayers(2);
        addPlayers(game, mp, 2);

        // Force start
        game.startHand();
        
        game.timeout();
        game.timeout();
        assertEquals(2, mockServerAdapter.getTimeoutRequests());
        
        assertFalse(mockServerAdapter.getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND));
    }   
	
	private void assertAllPlayersHaveCards(PokerPlayer[] p, int expectedNumberOfCards, int count) {
	    for (int i = 0; i < count; i++) {
			assertEquals(expectedNumberOfCards, p[i].getPocketCards().getCards().size());
		}
	}

	private void addPlayers(PokerState game, PokerPlayer[] p, int count) {
		for (int i = 0; i < count; i++) {
			game.addPlayer(p[i]);
		}
	}
	
	private void act(PokerActionType choice) {
		ActionRequest request = mockServerAdapter.getActionRequest();
		PossibleAction option = request.getOption(choice);
		PokerAction action = new PokerAction(request.getPlayerId(), choice, option.getMinAmount());
		game.act(action);
	}	

}
