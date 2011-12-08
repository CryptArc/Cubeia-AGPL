package com.cubeia.poker.variant.telesina;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaTableFreezeTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(2);
	}

	@Test
	public void testBettingRoundEndFreeze() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		mp[2].setBalance(20);
		
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();

		// Blinds
		act(p[1], PokerActionType.ANTE);	
		act(p[0], PokerActionType.ANTE);
		act(p[2], PokerActionType.ANTE);
		game.timeout();
		
		act(p[2], PokerActionType.BET, 18); // ALL IN
		act(p[0], PokerActionType.CALL);
		act(p[1], PokerActionType.CALL);
		
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		game.playerIsSittingOut(p[1], SitOutStatus.SITTING_OUT);
		
		int numberOfTimeoutsRequested = mockServerAdapter.getTimeoutRequests();
		
		game.timeout();
		
		int numberOfTimeoutsRequestedAfterCall = mockServerAdapter.getTimeoutRequests();
		
		// Check that we have a new scheduled timeout.
		assertEquals(numberOfTimeoutsRequested+1, numberOfTimeoutsRequestedAfterCall);
		
	}

}
