package com.cubeia.poker.variant.telesina;

import org.junit.Ignore;
import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaAllinTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(20);
	}
	
	
	/**
	 * Mock Game is staked at 20
	 */
	@Test
	public void testAllInTelesinaHand() {
		
		MockPlayer[] mp = TestUtils.createMockPlayers(2);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(83);
		mp[1].setBalance(63);
		
		// Force start
		game.timeout();
		
		// Blinds
		assertTrue(mp[1].isActionPossible(PokerActionType.ANTE));
		assertFalse(mp[0].isActionPossible(PokerActionType.ANTE));
		act(p[1], PokerActionType.ANTE);	
		act(p[0], PokerActionType.ANTE); 	
		act(p[1], PokerActionType.CHECK);
		act(p[0], PokerActionType.CHECK); 	
		
		game.timeout();
		
		
		PossibleAction betRequest = mp[1].getActionRequest().getOption(PokerActionType.BET);
		assertEquals(40, betRequest.getMinAmount());
		assertEquals(43, betRequest.getMaxAmount());
		act(p[1], PokerActionType.BET, 40);
		
		PossibleAction callRequest = mp[0].getActionRequest().getOption(PokerActionType.CALL);
		assertEquals(40, callRequest.getMinAmount());
		assertEquals(40, callRequest.getMaxAmount());
		
		PossibleAction raiseRequest = mp[0].getActionRequest().getOption(PokerActionType.RAISE);
		assertEquals(63, raiseRequest.getMinAmount());
		assertEquals(63, raiseRequest.getMaxAmount());

		
	}
	
	
	@Test
	@Ignore
	public void testAllInNoBettingAllowed() {
		
		MockPlayer[] mp = TestUtils.createMockPlayers(2);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(500);
		mp[1].setBalance(100);
		
		// Force start
		game.timeout();
		
		// Blinds
		assertTrue(mp[1].isActionPossible(PokerActionType.ANTE));
		assertFalse(mp[0].isActionPossible(PokerActionType.ANTE));
		act(p[1], PokerActionType.ANTE);	
		act(p[0], PokerActionType.ANTE); 	
		act(p[1], PokerActionType.BET, 80); // ALL IN
		act(p[0], PokerActionType.CALL); 	// Calls but is not all in
		
		game.timeout();
		
		// TODO: Verify that player 0 is all in
		// verify that no further betting is allowed or requested
		
		assertTrue(mp[1].isAllIn());
		
		PossibleAction option = mp[1].getActionRequest().getOption(PokerActionType.BET);
		assertNull(option);
		
		option = mp[0].getActionRequest().getOption(PokerActionType.BET);
		assertNull(option);

		
	}

}
