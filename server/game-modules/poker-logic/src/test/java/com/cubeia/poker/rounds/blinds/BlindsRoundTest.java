/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.rounds.blinds;

import java.util.Stack;

import junit.framework.TestCase;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestListener;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.SitOutStatus;

public class BlindsRoundTest extends TestCase implements TestListener {

	private MockGame game;

	private BlindsRound round;

	private ActionRequest requestedAction;

	private Stack<ActionRequest> requestedActions = new Stack<ActionRequest>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game = new MockGame();
		game.listeners.add(this);
	}

	public void testBasicBlinds() throws Exception {
		// Seat three players.
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		// Check that the next player gets the small blind.
		assertOptionEnabled(PokerActionType.SMALL_BLIND, p[1]);

		// Post small blind.
		verifyAndAct(p[1], PokerActionType.SMALL_BLIND);

		// Check that the next player gets the big blind.
		assertOptionEnabled(PokerActionType.BIG_BLIND, p[2]);

		// Post big blind.
		verifyAndAct(p[2], PokerActionType.BIG_BLIND);

		// Check that the blinds round is finished.
		assertTrue(round.isFinished());
	}

	public void testHeadsUpBlinds() throws Exception {
		// Seat two players.
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		// Check that the dealer gets the small blind.
		assertOptionEnabled(PokerActionType.SMALL_BLIND, p[0]);

		// Post small blind.
		verifyAndAct(p[0], PokerActionType.SMALL_BLIND);

		// Check that the next player gets the big blind.
		assertOptionEnabled(PokerActionType.BIG_BLIND, p[1]);

		// Post big blind.
		verifyAndAct(p[1], PokerActionType.BIG_BLIND);

		// Check that the blinds round is finished.
		assertTrue(round.isFinished());
	}
	
	public void testHeadsUpBlindsSecondHand() throws Exception {
		// Seat two players.
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		act(p[0], PokerActionType.SMALL_BLIND);
		act(p[1], PokerActionType.BIG_BLIND);
		game.blindsInfo = round.getBlindsInfo();
		round = new BlindsRound(game, false);
		
		verifyAndAct(p[1], PokerActionType.SMALL_BLIND);
		verifyAndAct(p[0], PokerActionType.BIG_BLIND);
		assertEquals(1, round.getBlindsInfo().getDealerButtonSeatId());
	}
	
	public void testMoveFromHeadsUpToNonHeadsUp() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 0, 1);
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		round = new BlindsRound(game, false);

		verifyAndAct(p[1], PokerActionType.SMALL_BLIND);
		verifyAndAct(p[2], PokerActionType.BIG_BLIND);
		
	}
	
	public void testMoveFromNonHeadsUpToHeadsUp() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		round = new BlindsRound(game, false);

		verifyAndAct(p[1], PokerActionType.SMALL_BLIND);
		verifyAndAct(p[0], PokerActionType.BIG_BLIND);		
	}
	
	public void testMoveFromNonHeadsUpToHeadsUpSbLeaves() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		setPreviousBlindsInfo(1, 2, 0);
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		round = new BlindsRound(game, false);

		verifyAndAct(p[0], PokerActionType.SMALL_BLIND);
		verifyAndAct(p[1], PokerActionType.BIG_BLIND);
		assertEquals(0, round.getBlindsInfo().getDealerButtonSeatId());
	}
	
	public void testKeepHeadsUpToHeadsUpBbLeaves() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		
		MockPlayer p2 = new MockPlayer(2);
		p2.setBalance(10000);
		p[1] = p2;
		
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 0, 1);
		p[0].setHasPostedEntryBet(true);
		round = new BlindsRound(game, false);

		verifyAndAct(p[1], PokerActionType.BIG_BLIND);
		assertEquals(1, round.getBlindsInfo().getDealerButtonSeatId());
	}	
	
	private void assertOptionEnabled(PokerActionType option, MockPlayer player) {
		assertTrue(player.getActionRequest().isOptionEnabled(option));
	}

	public void testOutOfOrderActing() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		// Wrong player posts small blind.
		try {
			act(p[0], PokerActionType.SMALL_BLIND);
			fail("Expected exception");
		} catch (IllegalArgumentException expected) {
		}

		try {
			// Right player posts wrong thing
			act(p[1], PokerActionType.BIG_BLIND);
			fail("Expected exception");
		} catch (IllegalStateException expected) {
		}

		try {
			// Right player posts right thing at wrong time
			act(p[2], PokerActionType.BIG_BLIND);
			fail("Expected exception");
		} catch (IllegalStateException expected) {
		}

		// Check that only the small blind has been asked so far.
		assertEquals(1, requestedActions.size());

		// Ok, no more fooling around.
		act(p[1], PokerActionType.SMALL_BLIND);

		// And let's be a pain again.
		// Wrong player posts big blind.
		try {
			act(p[1], PokerActionType.BIG_BLIND);
			fail("Expected exception");
		} catch (IllegalArgumentException expected) {
		}

		try {
			// Right player posts wrong thing
			act(p[2], PokerActionType.SMALL_BLIND);
			fail("Expected exception");
		} catch (IllegalStateException expected) {
		}

		// And be nice.
		act(p[2], PokerActionType.BIG_BLIND);

		// Check that the blinds round is finished.
		assertTrue(round.isFinished());
	}

	public void testDealerButtonPosition() throws Exception {
		// Seat three players.
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		assertEquals(0, round.getBlindsInfo().getDealerButtonSeatId());
	}

	public void testSecondHandsDealerButtonPosition() throws Exception {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		p[0].setHasPostedEntryBet(true);
		game.addPlayers(p);
		
		setPreviousBlindsInfo(0, 1, 2);
		
		round = new BlindsRound(game, false);
		assertEquals(1, round.getBlindsInfo().getDealerButtonSeatId());
	}
	
	public void testDeclineSmallBlindWithTwoPlayersEndsHand() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		assertOptionEnabled(PokerActionType.DECLINE_ENTRY_BET, p[0]);
		verifyAndAct(p[0], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(round.isCanceled());
	}
	
	public void testDeclineSmallBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		assertOptionEnabled(PokerActionType.DECLINE_ENTRY_BET, p[1]);
		verifyAndAct(p[1], PokerActionType.DECLINE_ENTRY_BET);
		verifyAndAct(p[2], PokerActionType.BIG_BLIND);
		assertTrue(round.isFinished());
	}
	
	
	public void testTimeout() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		round.timeout();
		assertTrue(round.isCanceled());		
	}
	
	public void testTimeoutBigBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		verifyAndAct(p[0], PokerActionType.SMALL_BLIND);
		round.timeout();
		assertTrue(round.isFinished());		
	}	
	
	public void testDenyBigBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		verifyAndAct(p[0], PokerActionType.SMALL_BLIND);
		verifyAndAct(p[1], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(round.isFinished());		
	}
	
	public void testPlayerAfterDeniedBBGetsAskedForBB() {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		act(p[1], PokerActionType.SMALL_BLIND);
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		verifyAndAct(p[0], PokerActionType.BIG_BLIND);
		assertTrue(round.isFinished());
	}
	
	public void testSamePlayerDeclinesBBThenPostsBB() {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		act(p[1], PokerActionType.SMALL_BLIND);
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		try {
			act(p[2], PokerActionType.BIG_BLIND);
			fail();
		} catch (IllegalArgumentException expected) {}
	}	
	
	public void testNoMoreBigBlinds() {
		MockPlayer[] p = TestUtils.createMockPlayers(3);
		game.addPlayers(p);
		round = new BlindsRound(game, false);

		act(p[1], PokerActionType.SMALL_BLIND);
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		act(p[0], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(round.isFinished());
	}
	
	public void testSittingOutPlayerIsNotAskedToPostSmallBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		
		p[1].setSitOutStatus(SitOutStatus.MISSED_BIG_BLIND);
		round = new BlindsRound(game, false);
		assertEquals(p[2].getId(), requestedAction.getPlayerId());
	}
	
	public void testSittingOutPlayerIsNotAskedToPostBigBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		
		p[2].setSitOutStatus(SitOutStatus.MISSED_BIG_BLIND);
		round = new BlindsRound(game, false);
		act(p[1], PokerActionType.SMALL_BLIND);
		assertEquals(p[3].getId(), requestedAction.getPlayerId());
	}
	
	public void testSittingOutPlayerIsNotAskedToPostBigBlindAfterInit() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		
		p[3].setSitOutStatus(SitOutStatus.MISSED_BIG_BLIND);
		round = new BlindsRound(game, false);
		act(p[1], PokerActionType.SMALL_BLIND);
		act(p[2], PokerActionType.DECLINE_ENTRY_BET);
		assertEquals(p[0].getId(), requestedAction.getPlayerId());
	}	
	
	public void testConsiderHandFirstOnTableWhenNoPlayersHavePosted() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);		
		round = new BlindsRound(game, false);
		assertEquals(0, round.getBlindsInfo().getDealerButtonSeatId());
	}
	
	public void testNonEnteredPlayerCannotPostSmallBlind() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		p[0].setHasPostedEntryBet(true);
		round = new BlindsRound(game, false);

		// Small blind is on seat 1, but seat 2 has not posted the entry bet, so he should not be asked to post small blind.
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));
		assertEquals(103, requestedAction.getPlayerId());
	}
	
	public void testEntryBet() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 0. 
		p[0].setHasPostedEntryBet(false);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);
		act(p[3], PokerActionType.BIG_BLIND);
		assertEquals(100, requestedAction.getPlayerId());
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));
	}

	public void testDeclineEntryBet() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 0. 
		p[0].setHasPostedEntryBet(false);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);

		act(p[3], PokerActionType.BIG_BLIND);
		act(p[0], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(round.isFinished());
	}	
	
	public void testEntryBetTimeout() {
		MockPlayer[] p = TestUtils.createMockPlayers(4);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 0. 
		p[0].setHasPostedEntryBet(false);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);
		act(p[3], PokerActionType.BIG_BLIND);
		round.timeout();
		assertTrue(round.isFinished());
	}	
	
	public void testTwoEntryBets() {
		MockPlayer[] p = TestUtils.createMockPlayers(6);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 4 and 5. 
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		p[4].setHasPostedEntryBet(false);
		p[5].setHasPostedEntryBet(false);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);
		act(p[3], PokerActionType.BIG_BLIND);
		assertEquals(104, requestedAction.getPlayerId());
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));
		act(p[4], PokerActionType.BIG_BLIND);
		
		assertEquals(105, requestedAction.getPlayerId());
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));		
		act(p[5], PokerActionType.BIG_BLIND);
	}
	
	public void testTwoEntryBetDeclines() {
		MockPlayer[] p = TestUtils.createMockPlayers(6);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 4 and 5. 
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		p[4].setHasPostedEntryBet(false);
		p[5].setHasPostedEntryBet(false);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);
		act(p[3], PokerActionType.BIG_BLIND);
		act(p[4], PokerActionType.DECLINE_ENTRY_BET);
		act(p[5], PokerActionType.DECLINE_ENTRY_BET);
		assertTrue(round.isFinished());
	}	
	
	public void testEntryBetOutOfTurn() {
		MockPlayer[] p = TestUtils.createMockPlayers(6);
		game.addPlayers(p);
		setPreviousBlindsInfo(0, 1, 2);
		
		// Everyone has posted the entry bet, except player 4 and 5. 
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(true);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		p[4].setHasPostedEntryBet(false);
		p[5].setHasPostedEntryBet(false);
		
		round = new BlindsRound(game, false);
		
		act(p[2], PokerActionType.SMALL_BLIND);
		act(p[3], PokerActionType.BIG_BLIND);
		
		try {
			act(p[5], PokerActionType.BIG_BLIND);
			fail();
		} catch (IllegalArgumentException expected) {}
	}	
	
	public void testTwoEntryBetsWithWrap() {
		MockPlayer[] p = TestUtils.createMockPlayers(6);
		game.addPlayers(p);
		setPreviousBlindsInfo(1, 2, 3);
		
		// Everyone has posted the entry bet, except player 5 and 1. 
		p[0].setHasPostedEntryBet(true);
		p[1].setHasPostedEntryBet(false);
		p[2].setHasPostedEntryBet(true);
		p[3].setHasPostedEntryBet(true);
		p[4].setHasPostedEntryBet(true);
		p[5].setHasPostedEntryBet(false);
		
		round = new BlindsRound(game, false);
		
		act(p[3], PokerActionType.SMALL_BLIND);
		act(p[4], PokerActionType.BIG_BLIND);
		assertEquals(105, requestedAction.getPlayerId());
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));
		act(p[5], PokerActionType.BIG_BLIND);
		
		assertEquals(101, requestedAction.getPlayerId());
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.BIG_BLIND));
		assertFalse(round.isFinished());
		act(p[1], PokerActionType.BIG_BLIND);
		assertTrue(round.isFinished());
	}
	
	public void testMissedSmall() {
		
	}

	private void setPreviousBlindsInfo(int dealerSeatId, int smallSeatId, int bigSeatId) {
		BlindsInfo bi = new BlindsInfo();
		bi.setDealerButtonSeatId(dealerSeatId);
		bi.setSmallBlindSeatId(smallSeatId);
		bi.setBigBlindSeatId(bigSeatId);
		bi.setBigBlindPlayerId(bigSeatId + 100);
		
		game.blindsInfo = bi;
	}

	public void notifyActionRequested(ActionRequest r) {
		requestedAction = r;
		requestedActions.add(r);
	}

	private void verifyAndAct(MockPlayer player, PokerActionType action) {
		assertTrue("Player " + player + " should have option: " + action, 
				player.getActionRequest().isOptionEnabled(action));
		assertTrue(requestedAction.isOptionEnabled(action));
		assertEquals(player.getId(), requestedAction.getPlayerId());
		act(player, action);
	}

	private void act(MockPlayer player, PokerActionType action) {
		PokerAction a = new PokerAction(player.getId(), action);
		round.act(a);
	}
}