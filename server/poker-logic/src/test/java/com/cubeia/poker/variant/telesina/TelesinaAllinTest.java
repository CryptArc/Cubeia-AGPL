package com.cubeia.poker.variant.telesina;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaAllinTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		game.setAnteLevel(500);
	}
	
	
	/**
	 * Mock Game is staked at 20/10'
	 */
	@Test
	public void testAllInTelesinaHand() {
		game.setAnteLevel(20);
		MockPlayer[] mp = testUtils.createMockPlayers(2);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(63);
		mp[1].setBalance(43);
		
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
		assertEquals(20, betRequest.getMinAmount());
		assertEquals(23, betRequest.getMaxAmount());
		act(p[1], PokerActionType.BET, 20);
		
		PossibleAction callRequest = mp[0].getActionRequest().getOption(PokerActionType.CALL);
		assertEquals(20, callRequest.getMinAmount());
		assertEquals(20, callRequest.getMaxAmount());
		
		PossibleAction raiseRequest = mp[0].getActionRequest().getOption(PokerActionType.RAISE);
		assertEquals(40, raiseRequest.getMinAmount());
		assertEquals(43, raiseRequest.getMaxAmount());
		
	}

}
