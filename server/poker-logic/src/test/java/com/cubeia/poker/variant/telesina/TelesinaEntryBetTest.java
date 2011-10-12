package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaEntryBetTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(10);
	}
	
	
	@Test
	public void testPlayerDeclinesAnte() {
		MockPlayer[] mp = testUtils.createMockPlayers(3,100);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
	
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);
		
		ActionRequest actionRequest = mp[2].getActionRequest();
		assertEquals(20, actionRequest.getOption(BET).getMinAmount());
		
	}
}
