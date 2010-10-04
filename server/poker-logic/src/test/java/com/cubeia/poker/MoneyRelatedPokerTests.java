package com.cubeia.poker;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;

public class MoneyRelatedPokerTests extends GuiceTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSmallBlindCost() {
		long startingChips = 10000;
		MockPlayer[] mp = testUtils.createMockPlayers(4, startingChips);
		int[] p = testUtils.createPlayerIdArray(mp);
		assertEquals(4, p.length);
		testUtils.addPlayers(game, mp, 0);
		assertEquals(startingChips, mp[0].getBalance());

		// Force start
		game.timeout();
		
		// Blinds
//		mockServerAdapter.getActionRequest()
		act(PokerActionType.SMALL_BLIND);
		long balance = mp[2].getBalance();
		act(PokerActionType.BIG_BLIND);
		assertEquals(balance - 100, mp[2].getBalance());
		
		System.out.println(mp[1].getBalance());
		System.out.println(mp[3].getBalance());

		// Everyone folds and bb wins
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
		act(PokerActionType.FOLD);
	}
	
	private void act(PokerActionType choice) {
		ActionRequest request = mockServerAdapter.getActionRequest();
		PossibleAction option = request.getOption(choice);
		PokerAction action = new PokerAction(request.getPlayerId(), choice, option.getMinAmount());
		game.act(action);
	}
}
