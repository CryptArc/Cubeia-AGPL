package com.cubeia.poker.rounds.betting;

import junit.framework.TestCase;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestListener;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.rounds.BettingRound;

public class AllInTest extends TestCase implements TestListener {

	private TestUtils testUtils = new TestUtils();

	private ActionRequest requestedAction;

	private MockGame game;

	private BettingRound round;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game = new MockGame();
		game.listeners.add(this);
		
	}
	
	public void testAllInBet() {
		// NOTE: This implies no limit betting.
		MockPlayer[] p = testUtils.createMockPlayers(2, 500);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		PossibleAction bet = requestedAction.getOption(PokerActionType.BET);
		assertEquals(500, bet.getMaxAmount());
		assertEquals(500, p[1].getBalance());
		act(p[1], PokerActionType.BET, bet.getMaxAmount());
		assertEquals(0, p[1].getBalance());
		assertTrue(p[1].isAllIn());
		
		
		
	}
	
	
	// HELPERS
	
	private void act(MockPlayer player, PokerActionType action, long amount) {
		PokerAction a = new PokerAction(player.getId(), action);
		a.setBetAmount(amount);
		round.act(a);
	}	

	public void notifyActionRequested(ActionRequest r) {
		this.requestedAction = r;
	}

}
