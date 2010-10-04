package com.cubeia.poker;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class PokerTournamentTest extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game.setTournamentTable(true);
		game.setTournamentId(1);
	}
	
	public void testTournamentHand() {
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		testUtils.addPlayers(game, mp);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.startHand();

		// Blinds are auto.
		game.timeout();
		game.timeout();

		assertAllPlayersHaveCards(mp, 2);

		assertEquals(0, game.getCommunityCards().size());

		// Check that tournament players don't sit out.
		game.timeout();
		game.timeout();
		game.timeout();
		
		assertTrue(game.isFinished());
		
		// Next round starts
		game.startHand();
		assertAllPlayersHaveCards(mp, 0);

		// Blinds
		game.timeout();
		game.timeout();
		
		assertAllPlayersHaveCards(mp, 2);
		
		// No response again.
		game.timeout();
		game.timeout();
		game.timeout();
		
		assertTrue(game.isFinished());		
	}
	
	public void testBigBlindMovedFromTheTable() {
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		testUtils.addPlayers(game, mp);

		// Force start
		game.startHand();

		// Blinds are auto.
		game.timeout();
		game.timeout();

		// Check that tournament players don't sit out.
		testUtils.act(game, p[3], PokerActionType.FOLD);
		testUtils.act(game, p[0], PokerActionType.FOLD);
		testUtils.act(game, p[1], PokerActionType.FOLD);
		
		game.removePlayer(p[2]);
		
		game.startHand();
		
		// P3 should post bb here.
		game.timeout();

		// Check that p0 is up to act
		assertEquals(mp[0].getId(), mockServerAdapter.getActionRequest().getPlayerId());
	}	
	
	public void assertAllPlayersHaveCards(PokerPlayer[] p, int expectedNumberOfCards) {
		for (PokerPlayer pl : p) {
			assertEquals(expectedNumberOfCards, pl.getPocketCards().size());
		}
	}	
}
