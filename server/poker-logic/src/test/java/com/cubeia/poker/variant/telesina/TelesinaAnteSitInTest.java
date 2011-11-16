package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaAnteSitInTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(20);
	}


	/**
	 * Mock Game is staked at 20/10'
	 */
	@Test
	public void testAnteSitIns() {
		MockPlayer[] mp = TestUtils.createMockPlayers(6, 100);
		MockPlayer[] startingPlayers = new MockPlayer[]{ mp[0], mp[1] };
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, startingPlayers);

		// Force start
		game.timeout();

		// Blinds

		assertThat(mp[1].isActionPossible(PokerActionType.ANTE), is(true));
		assertThat(mp[0].isActionPossible(PokerActionType.ANTE), is(true));
		act(p[1], PokerActionType.ANTE);	
		
		assertEquals(2, game.countSittingInPlayers());
		assertEquals(2, game.getSeatedPlayers().size());

		game.addPlayer(mp[3]);

		assertEquals(3, game.countSittingInPlayers());
		assertEquals(3, game.getSeatedPlayers().size());

		act(p[0], PokerActionType.ANTE); 
		
		// timeout the DealInitialCardsRound
		game.timeout();

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
		MockPlayer[] mp = TestUtils.createMockPlayers(2, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

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
	
	@Test
	public void testAnteSitOutThenSitIt() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();

		// Blinds
		act(p[1], PokerActionType.ANTE);	
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		
		// Assert that player 2 not in the current players
		assertNull(game.getPlayerInCurrentHand(p[2]));
		
		act(p[0], PokerActionType.ANTE); 
		
		// timeout the DealInitialCardsRound
		game.timeout();
		
		// Now player 2 should not be in the hard nor be awarded cards
		assertEquals(2, game.getPlayerInCurrentHand(p[1]).getPocketCards().getCards().size());
		assertEquals(2, game.getPlayerInCurrentHand(p[0]).getPocketCards().getCards().size());
		
		
		act(p[1], PokerActionType.CHECK);
		act(p[0], PokerActionType.CHECK); 	
	}

}
