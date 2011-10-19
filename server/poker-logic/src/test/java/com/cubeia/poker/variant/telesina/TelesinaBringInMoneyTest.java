package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CALL;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import static com.cubeia.poker.action.PokerActionType.FOLD;
import junit.framework.Assert;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaBringInMoneyTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(10);
	}

	@Test
	public void testBringInMoneyInHand() {
		setAnteLevel(10);
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);


		act(p[2], BET, 90);
		act(p[0], CALL);
		act(p[1], CALL);

		// Progress until hand is complete
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		bringInMoney(mp, p);
		game.timeout();
		// End of hand

		assertEquals(0, mp[0].getBalance());
		assertEquals(300, mp[1].getBalance());
		assertEquals(50, mp[2].getBalance());

		assertTrue(mp[0].isSittingOut());
		assertFalse(mp[2].isSittingOut());
		
		game.timeout();
		
		act(p[2], ANTE);
		act(p[1], ANTE);
		
		// Now game should progress to betting round or we have a bug!
		Assert.assertNotNull(mp[2].getActionRequest().getOption(PokerActionType.CHECK));
		
	}
	
	@Test
	public void testBringInMoneyBetweenHands() {
		setAnteLevel(10);
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);


		act(p[2], BET, 90);
		act(p[0], CALL);
		act(p[1], CALL);

		// Progress until hand is complete
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		// End of hand

		assertEquals(0, mp[0].getBalance());
		assertEquals(300, mp[1].getBalance());
		assertEquals(0, mp[2].getBalance());

		assertTrue(mp[0].isSittingOut());
		assertTrue(mp[2].isSittingOut());
		
		bringInMoney(mp, p);
		
		game.timeout();
		
		act(p[2], ANTE);
		act(p[1], ANTE);
		
		// Now game should progress to betting round or we have a bug!
		Assert.assertNotNull(mp[2].getActionRequest().getOption(PokerActionType.CHECK));
		
	}
	
	
	@Test
	public void testSitInNextHand() {
		setAnteLevel(10);
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);


		act(p[2], BET, 90);
		act(p[0], CALL);
		act(p[1], CALL);

		// Progress until hand is complete
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		bringInMoney(mp, p);
		game.timeout();
		// End of hand
		
		assertEquals(0, mp[0].getBalance());
		assertEquals(300, mp[1].getBalance());
		assertEquals(50, mp[2].getBalance());
		assertTrue(mp[0].isSittingOut());
		
		game.timeout();
		
		act(p[2], ANTE);
		act(p[1], ANTE);
		
		game.playerIsSittingIn(p[0]);
		
		act(p[2], CHECK);
		act(p[1], FOLD); 
		
		assertTrue(game.isFinished());
	}

	private void bringInMoney(MockPlayer[] mp, int[] p) {
		// Player 2 brings in more cash between hands
		// Mimic the logic executed in the back end handler, this is brittle - if the back end handler
		// implementation changes then that behavior will not be used here. Never the less...
		int amountReserved = 50;
		if (game.isPlayerInHand(p[2])) {
			System.out.println("player is in hand, adding reserved amount "+amountReserved+" as pending");
			mp[2].addPendingAmount(amountReserved);
		} else {
			System.out.println("player is not in hand, adding reserved amount "+amountReserved+" to balance");
			mp[2].addChips(amountReserved);
		}

		game.playerIsSittingIn(p[2]);
	}

}
