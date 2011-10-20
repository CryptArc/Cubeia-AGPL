package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.DECLINE_ENTRY_BET;

import java.util.Collection;

import junit.framework.Assert;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.sitout.SitoutCalculator;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaSitoutTimeoutTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		sitoutTimeLimitMilliseconds = 1;
		super.setUp();
		setAnteLevel(10);
	}
	
	public void testTimeoutCalculation() throws InterruptedException {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		// Timeout player 2, this will start a scheduled action that should 
		// remove him from the table
		act(p[2], DECLINE_ENTRY_BET);
		assertTrue(mp[2].isSittingOut());
		assertTrue(mp[2].getSitOutTimestamp() <= System.currentTimeMillis());
		// Assert that player 0 has received an action request
		act(p[0], ANTE);
		
		// Make sure we are timing out the sit out timeout
		Thread.sleep(2);
		assertTrue(mp[2].getSitOutTimestamp()+sitoutTimeLimitMilliseconds < System.currentTimeMillis());
		
		SitoutCalculator calculator = new SitoutCalculator();
		Collection<PokerPlayer> timeouts = calculator.checkTimeoutPlayers(game);
		Assert.assertEquals(1, timeouts.size());
		Assert.assertEquals(mp[2], timeouts.iterator().next());
	}
	
	
}
