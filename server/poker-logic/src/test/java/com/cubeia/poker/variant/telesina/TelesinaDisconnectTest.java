package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CALL;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import junit.framework.Assert;

import org.apache.log4j.Logger;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaDisconnectTest extends AbstractTexasHandTester {

	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		sitoutTimeLimitMilliseconds = 1;
		super.setUp();
		setAnteLevel(10);
	}
	
	public void testDisconnectFolding() throws InterruptedException {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);
		
		// 1. Disconnect player 0
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
		
		// 2. Place bet
		act(p[2], BET);
		// 3. Verify that player 0 is folding
		Assert.assertTrue(mp[0].hasFolded());
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
		
	}
	
	
	public void testDisconnectAndReconnect() throws InterruptedException {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);
		
		// TODO
		// 1. Disconnect player 0
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
		
		// 2. check
		act(p[2], CHECK);
		// 3. Verify that player 0 has checked and not folded
		Assert.assertFalse(mp[0].hasFolded());
		act(p[1], CHECK);
		
		game.timeout();
		
		// 2. Verify that a reconnect lets player 0 act again
		game.playerIsSittingIn(p[0]);
		assertEquals(PokerPlayerStatus.NORMAL, mockServerAdapter.getPokerPlayerStatus(p[0]));
		act(p[1], CHECK);
		act(p[2], CHECK);
		
		game.timeout();
		
		act(p[1], CHECK);
		act(p[2], CHECK);
		act(p[0], CHECK);
		
		game.timeout();
		
		act(p[2], CHECK);
		act(p[0], CHECK);
		act(p[1], CHECK);
		
	}
	
	
	public void testDisconnectBug() throws InterruptedException {
		MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		//  --- ANTE ROUND ---
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);
		
		// --- NEW BETTING ROUND ---
		
		game.playerIsSittingOut(p[1], SitOutStatus.SITTING_OUT);
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
		act(p[2], CHECK);
		act(p[0], CHECK);
		PokerAction latestActionPerformed = mockServerAdapter.getLatestActionPerformed();
		assertEquals(p[1], latestActionPerformed.getPlayerId().intValue());
		assertEquals(PokerActionType.CHECK, latestActionPerformed.getActionType());
		game.timeout();
		
		
		// --- NEW BETTING ROUND ---
		act(p[0], BET);
		act(p[2], CALL);
		
		Assert.assertTrue(mp[1].hasFolded());
		assertEquals(3, mp[2].getPocketCards().getCards().size());
		assertEquals(3, mp[1].getPocketCards().getCards().size());
		
		game.timeout();
		
		// Make sure mp[0] does not get any more cards
		assertEquals(4, mp[2].getPocketCards().getCards().size());
		assertEquals(3, mp[1].getPocketCards().getCards().size());
	}
	
}
