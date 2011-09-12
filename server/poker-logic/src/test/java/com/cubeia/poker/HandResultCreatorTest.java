package com.cubeia.poker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.util.HandResultCalculator;
import com.cubeia.poker.variant.HandResultCreator;
import com.cubeia.poker.variant.telesina.TelesinaHandComparator;
import com.cubeia.poker.variant.telesina.TelesinaHandStrengthEvaluator;

public class HandResultCreatorTest {

	private Answer<Object> THROW_EXCEPTION = new Answer<Object>() {
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			throw new RuntimeException("Called unmocked method: " + invocation.getMethod().getName());
		}
	}; 
	
	@Test
	public void testCreateHandResultTelesinaStyle() {
		
		TelesinaHandStrengthEvaluator hte = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		HandResultCreator creator = new HandResultCreator(hte);
		HandResultCalculator resultCalculator = new HandResultCalculator(new TelesinaHandComparator(hte));
		
		Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();

		PokerPlayer pp1 = mockPlayer(1, 50, false, false, new Hand("7S 8S JC QC QH"));
		PokerPlayer pp2 = mockPlayer(2, 50, false, false, new Hand("7H 8D JD QS 9H")); // pp2 wins using vela card
		
		playerMap.put(1, pp1);
		playerMap.put(2, pp2);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(playerMap.values());

		List<Card> communityCards = Card.list("TS");
		
		HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap);

		assertNotNull(result);
		
		Map<Integer, Long> resultsSimplified = new HashMap<Integer, Long>();
		for (Entry<PokerPlayer, Result> entry : result.getResults().entrySet()) {
			resultsSimplified.put(entry.getKey().getId(), entry.getValue().getNetResult());
		}
		
		assertEquals(-50L, (long) resultsSimplified.get(1));
		assertEquals(50L, (long) resultsSimplified.get(2));
	}
	
	private PokerPlayer mockPlayer(int playerId, long betStack, boolean allIn, boolean folded, Hand pocketCards) {
		PokerPlayer pp = mock(PokerPlayer.class, THROW_EXCEPTION);
		doReturn(playerId).when(pp).getId();
		doReturn(betStack).when(pp).getBetStack();
		doReturn(allIn).when(pp).isAllIn();
		doReturn(folded).when(pp).hasFolded();
		doReturn(pocketCards).when(pp).getPocketCards();
		
		doReturn("Player" + playerId).when(pp).toString();
		
		return pp;
	}
}
