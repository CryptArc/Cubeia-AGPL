package com.cubeia.poker.rounds;

import junit.framework.TestCase;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestListener;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;

public class BettingRoundTest extends TestCase implements TestListener {

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

	public void testHeadsUpBetting() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
		verifyAndAct(p[0], PokerActionType.FOLD, 100);

		assertTrue(round.isFinished());
	}

	public void testCallAmount() {
		MockPlayer[] p = testUtils.createMockPlayers(2, 100);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);
		act(p[1], PokerActionType.BET, 70);
		
		PossibleAction bet = requestedAction.getOption(PokerActionType.CALL);
		assertEquals(70, bet.getMaxAmount());
	}	
	
	public void testRaise() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.RAISE));
		verifyAndAct(p[0], PokerActionType.RAISE, 200);
	}
	
	public void testNoRaiseAllowedWhenAllOtherPlayersAreAllIn() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);

		actMax(PokerActionType.BET);
		assertFalse(requestedAction.isOptionEnabled(PokerActionType.RAISE));
	}	
	
	private void actMax(PokerActionType action) {
		PossibleAction option = requestedAction.getOption(action);
		PokerAction a = new PokerAction(requestedAction.getPlayerId(), action, option.getMaxAmount());
		round.act(a);		
	}

	public void testTimeoutTwice() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);

		round.timeout();
		round.timeout();

		assertTrue(round.isFinished());
	}	
	
	public void testTimeout() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0);

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
		round.timeout();
		assertTrue(round.isFinished());
	}	

	public void testDealerLeft() {
		MockPlayer[] p = testUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 3);

		assertFalse(game.roundFinished);

		round.timeout();
		round.timeout();

		assertTrue(round.isFinished());		
	}

	// HELPERS
	
	private void act(MockPlayer player, PokerActionType action, long amount) {
		PokerAction a = new PokerAction(player.getId(), action);
		a.setBetAmount(amount);
		round.act(a);
	}	

	private void verifyAndAct(MockPlayer player, PokerActionType action, long amount) {
		assertTrue("Tried to " + action + " but available actions were: "
				+ player.getActionRequest().getOptions(), player
				.getActionRequest().isOptionEnabled(action));
		assertTrue(requestedAction.isOptionEnabled(action));
		assertEquals(player.getId(), requestedAction.getPlayerId());
		act(player, action, amount);
	}

	public void notifyActionRequested(ActionRequest r) {
		this.requestedAction = r;
	}

}
