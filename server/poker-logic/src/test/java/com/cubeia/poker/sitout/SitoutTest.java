package com.cubeia.poker.sitout;

import com.cubeia.poker.GuiceTest;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;

/**
 * Integration test for poker logic.
 */
public class SitoutTest extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDeclinedPlayerIsSittingOut() {
		mockServerAdapter.clear();
		MockPlayer[] mp = testUtils.createMockPlayers(3);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();

		// Blinds
		act(p[1], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(mp[1].isSittingOut());
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
		
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(mp[2].isSittingOut());
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[2]));
	}
	
	public void testBlindsTimeout() {
		mockServerAdapter.clear();
		MockPlayer[] mp = testUtils.createMockPlayers(3);
		int[] p = testUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);

		// Force start
		game.timeout();

		// Blinds
		game.timeout();
		assertTrue(mp[1].isSittingOut());
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
		
		System.out.println("Next request: "+mockServerAdapter.getActionRequest());
		
		game.timeout();
		assertTrue(mp[2].isSittingOut());
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[2]));
	}
	
	private void act(int playerId, PokerActionType actionType) {
		act(playerId, actionType, mockServerAdapter.getActionRequest().getOption(actionType).getMinAmount());
	}
	
	private void act(int playerId, PokerActionType actionType, long amount) {
		PokerAction action = new PokerAction(playerId, actionType);
		action.setBetAmount(amount);
		game.act(action);
	}	
	
	private void addPlayers(PokerState game, PokerPlayer[] p) {
		for (PokerPlayer pl : p) {
			game.addPlayer(pl);
		}
	}

}
