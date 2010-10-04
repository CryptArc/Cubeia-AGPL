package com.cubeia.poker;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;

/**
 * Integration test for poker logic.
 */
public class TournamentTablePokerLogicTest extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testAddPlayerInTheMiddleOfHand() {
	    game.setTournamentTable(true);
		MockPlayer[] mp = testUtils.createMockPlayers(4, 500);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		addPlayers(game, mp, 4);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.startHand();

		// Blinds
		game.timeout();
		game.timeout();
		assertTrue(mp[2].hasOption());
		assertAllPlayersHaveCards(mp, 2, 4);

		assertEquals(0, game.getCommunityCards().size());

		// Pre flop round
		assertEquals(103, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		assertTrue(mp[3].hasActed());
		assertEquals(100, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CHECK);

		// Trigger deal community cards
		game.timeout();
		
		assertEquals(3, game.getCommunityCards().size());

		// Player joins in the middle of the hand.
		MockPlayer p5 = new MockPlayer(5);
		p5.setSeatId(5);		
		game.addPlayer(p5);
		
		// Check that we still only have 4 players.
		assertEquals(4, game.getGameType().getSeatingMap().size());
		
		// Flop round
		act(PokerActionType.BET);
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);

		// Trigger deal community cards
		game.timeout();
		assertEquals(4, game.getCommunityCards().size());
		
		// Turn round
		act(PokerActionType.CHECK);
		act(PokerActionType.BET);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.CALL);

		// Trigger deal community cards
		game.timeout();
		assertEquals(5, game.getCommunityCards().size());

		// River round
		act(PokerActionType.CHECK);
		act(PokerActionType.BET);
		act(PokerActionType.FOLD);

		// Assertions
		assertTrue(game.isFinished());
		
		// Start next hand.
		game.startHand();
		
		// Check that we now have 5 players
		assertEquals(5, game.getSeatedPlayers().size());
		
		// Auto blinds, as usual
		game.timeout();
		game.timeout();
		
		// Run next hand
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		
		assertTrue(game.isFinished());
		
		// Still 5 players.
		assertEquals(5, game.getSeatedPlayers().size());
		assertEquals(5, game.getGameType().getSeatingMap().size());
	}
	
	public void testSitAndLeave() {
		MockPlayer[] mp = testUtils.createMockPlayers(4);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		addPlayers(game, mp, 4);
		assertEquals(4, game.getSeatedPlayers().size());

		// Force start
		game.timeout();

		// Blinds
		act(PokerActionType.SMALL_BLIND);

		assertTrue(mp[2].isActionPossible(PokerActionType.BIG_BLIND));
		assertEquals(102, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.BIG_BLIND);
		
		assertTrue(mp[2].hasOption());
		assertAllPlayersHaveCards(mp, 2, 4);

		assertEquals(0, game.getCommunityCards().size());

		// Pre flop round
		assertEquals(103, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		assertTrue(mp[3].hasActed());
		assertEquals(100, mockServerAdapter.getActionRequest().getPlayerId());
		act(PokerActionType.CALL);
		act(PokerActionType.CALL);
		act(PokerActionType.CHECK);

		// Trigger deal community cards
		game.timeout();
		assertEquals(3, game.getCommunityCards().size());

		// Player joins in the middle of the hand.
		MockPlayer p5 = new MockPlayer(5);
		p5.setSeatId(5);		
		game.addPlayer(p5);
		
		// Check that we still only have 4 players.
		assertEquals(5, game.getSeatedPlayers().size());
		assertEquals(4, game.getGameType().getSeatingMap().size());
		
		game.removePlayer(p5.getId());
		assertEquals(4, game.getSeatedPlayers().size());
	}
	
	public void testSmallBlindTimeoutOnTournamentTable() {
	    game.setTournamentTable(true);
        MockPlayer[] mp = testUtils.createMockPlayers(2);
        addPlayers(game, mp, 2);

        // Force start
        game.startHand();
        
        game.timeout();
        game.timeout();
        assertEquals(2, mockServerAdapter.getTimeoutRequests());
        
        assertFalse(mockServerAdapter.getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND));
    }   
	
	private void assertAllPlayersHaveCards(PokerPlayer[] p, int expectedNumberOfCards, int count) {
	    for (int i = 0; i < count; i++) {
			assertEquals(expectedNumberOfCards, p[i].getPocketCards().size());
		}
	}

	private void addPlayers(PokerState game, PokerPlayer[] p, int count) {
		for (int i = 0; i < count; i++) {
			game.addPlayer(p[i]);
		}
	}
	
	private void act(PokerActionType choice) {
		ActionRequest request = mockServerAdapter.getActionRequest();
		PossibleAction option = request.getOption(choice);
		PokerAction action = new PokerAction(request.getPlayerId(), choice, option.getMinAmount());
		game.act(action);
	}	

}
