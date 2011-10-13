package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaHandsTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(10);
	}
	
	
	@Test
	public void testAnteTimeoutHand2() {
		MockPlayer[] mp = testUtils.createMockPlayers(3, 100);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);

		System.out.println("Pocket Cards: "+mp[0].getPocketCards());
		assertEquals(2, mp[1].getPocketCards().getCards().size());
		assertEquals(2, mp[2].getPocketCards().getCards().size());
		assertEquals(2, mp[0].getPocketCards().getCards().size());
		
		act(p[2], PokerActionType.CHECK);
		act(p[0], PokerActionType.FOLD);
		act(p[1], PokerActionType.CHECK);
		
		game.timeout();
		
		assertEquals(3, mp[1].getPocketCards().getCards().size());
		assertEquals(3, mp[2].getPocketCards().getCards().size());
		assertEquals(2, mp[0].getPocketCards().getCards().size());
	}
}
