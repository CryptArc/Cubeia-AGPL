package com.cubeia.poker.variant.telesina;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaAnteSitInTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		game.setAnteLevel(20);
	}


	/**
	 * Mock Game is staked at 20/10'
	 */
	@Test
	public void testAnteSitIns() {
		MockPlayer[] mp = testUtils.createMockPlayers(6);
		MockPlayer[] startingPlayers = new MockPlayer[]{ mp[0], mp[1] };
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, startingPlayers);

		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);

		// Force start
		game.timeout();

		// Blinds

		assertTrue(mp[1].isActionPossible(PokerActionType.ANTE));
		assertFalse(mp[0].isActionPossible(PokerActionType.ANTE));
		act(p[1], PokerActionType.ANTE);	


		assertEquals(2, game.countSittingInPlayers());
		assertEquals(2, game.getSeatedPlayers().size());

		game.addPlayer(mp[3]);

		assertEquals(3, game.countSittingInPlayers());
		assertEquals(3, game.getSeatedPlayers().size());

		act(p[0], PokerActionType.ANTE); 

		act(p[1], PokerActionType.CHECK);
		act(p[0], PokerActionType.CHECK); 	

		game.timeout();
		act(p[1], PokerActionType.CHECK);
		act(p[0], PokerActionType.CHECK); 	

		//assertFalse(mp[1].isActionPossible(PokerActionType.ANTE));
		//		act(p[0], PokerActionType.ANTE); 
		//		
		//		act(p[1], PokerActionType.CHECK);
		//		
		//		act(p[0], PokerActionType.CHECK); 	

	}


	@Test
	public void testDoubleAnte() {
		MockPlayer[] mp = testUtils.createMockPlayers(2);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);

		// Force start
		game.timeout();
		try {
			// Blinds
			act(p[1], PokerActionType.ANTE);
			act(p[1], PokerActionType.ANTE);
			fail("Should not be able to post Ante two times in a row");
		} catch (IllegalArgumentException e) {
			// Expected
		}

	}

}
