package com.cubeia.poker.betlevel;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.action.PokerActionType;

/**
 * Integration test for poker logic.
 */
public class BlindsLevelTest extends AbstractTexasHandTester {
	
	public void testSimpleHoldemHand() {
		game.setAnteLevel(100);
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		addPlayers(game, mp);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.timeout();
		assertEquals(101, mockServerAdapter.getActionRequest().getPlayerId());
		assertEquals(50, mockServerAdapter.getActionRequest().getOption(PokerActionType.SMALL_BLIND).getMinAmount());
		assertEquals(50, mockServerAdapter.getActionRequest().getOption(PokerActionType.SMALL_BLIND).getMaxAmount());

		// Blinds
		act(p[1], PokerActionType.SMALL_BLIND);

		assertTrue(mp[2].isActionPossible(PokerActionType.BIG_BLIND));
		assertEquals(102, mockServerAdapter.getActionRequest().getPlayerId());
		assertEquals(100, mockServerAdapter.getActionRequest().getOption(PokerActionType.BIG_BLIND).getMinAmount());
		assertEquals(100, mockServerAdapter.getActionRequest().getOption(PokerActionType.BIG_BLIND).getMaxAmount());
		act(p[2], PokerActionType.BIG_BLIND);
		
	}

}
