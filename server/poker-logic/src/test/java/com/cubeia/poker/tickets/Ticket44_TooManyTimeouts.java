package com.cubeia.poker.tickets;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.action.PokerActionType;

/**
 * Test for Ticket 44:
 * When all players are all-in pre-flop it seems that (sometimes) there is one too many timeouts scheduled. 
 * This causes the river and turn to come directly and the next hand to start right away without any pause. 
 * 
 */
public class Ticket44_TooManyTimeouts extends AbstractTexasHandTester {

	/**
	 * Mock Game is staked at 10/5'
	 * Player default balance: 5000
	 */
	public void testAllInHoldemHand() {
		game.setAnteLevel(10);
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		mp[0].setBalance(9000);
		
		// Force start
		game.timeout();
		// Blinds
		act(p[1], PokerActionType.SMALL_BLIND);	
		act(p[2], PokerActionType.BIG_BLIND); 	
		act(p[3], PokerActionType.RAISE, 5000);	// ALL IN
		act(p[0], PokerActionType.CALL);		// 4000 remaining
		act(p[1], PokerActionType.CALL);		// ALL IN
		mockServerAdapter.clear();
		act(p[2], PokerActionType.CALL);		// ALL IN
		
		assertEquals(4000, mp[0].getBalance());
		assertEquals(1, mockServerAdapter.getTimeoutRequests());
		
		// Trigger deal community cards
		game.timeout();
		
		// FLOP
		// THIS IS THE BUG. The assert below should not be true since mp[0] is not all-in
		// assertTrue(mp[0].isActionPossible(PokerActionType.CHECK));
		assertFalse(mp[0].isActionPossible(PokerActionType.CHECK));
		assertFalse(mp[3].isActionPossible(PokerActionType.CHECK));
		assertEquals(3, game.getCommunityCards().size());
		
		// Trigger all-in FLOP round timeout
		game.timeout();
		// Trigger deal community cards
		game.timeout();
		
		// TURN
		assertFalse(mp[0].isActionPossible(PokerActionType.CHECK));
		assertFalse(mp[3].isActionPossible(PokerActionType.CHECK));
		assertEquals(4, game.getCommunityCards().size());
		
	}

}
