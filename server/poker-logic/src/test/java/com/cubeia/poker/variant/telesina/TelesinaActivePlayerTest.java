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

public class TelesinaActivePlayerTest extends AbstractTexasHandTester {

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
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(true));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(true));
		act(p[1], PokerActionType.ANTE);	
		act(p[0], PokerActionType.ANTE); 	
		
		// make deal initial pocket cards round end
		game.timeout();
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(false));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(true));
		act(p[1], PokerActionType.CHECK);
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(true));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(false));
		act(p[0], PokerActionType.CHECK); 	
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(false));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(false));
		game.timeout();
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(false));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(true));
		act(p[1], PokerActionType.BET, 40);
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(true));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(false));
		act(p[0], PokerActionType.FOLD, 40);
		
		assertThat(game.isWaitingForPlayerToAct(p[0]), is(false));
		assertThat(game.isWaitingForPlayerToAct(p[1]), is(false));
		
	}
	
	
}
