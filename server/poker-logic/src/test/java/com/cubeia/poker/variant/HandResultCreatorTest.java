package com.cubeia.poker.variant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cubeia.poker.RakeSettings;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.LinearSingleLimitRakeCalculator;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.util.HandResultCalculator;
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

		PotHolder potHolder = new PotHolder(new LinearSingleLimitRakeCalculator(new RakeSettings(BigDecimal.ZERO)));
		potHolder.moveChipsToPot(playerMap.values());

		List<Card> communityCards = Card.list("TS");
		
		HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>());

		assertNotNull(result);
		
		Map<Integer, Long> resultsSimplified = new HashMap<Integer, Long>();
		for (Entry<PokerPlayer, Result> entry : result.getResults().entrySet()) {
			resultsSimplified.put(entry.getKey().getId(), entry.getValue().getNetResult());
		}
		
		assertEquals(-50L, (long) resultsSimplified.get(1));
		assertEquals(50L, (long) resultsSimplified.get(2));
	}
	
	
	@Test
	public void testCreateHandResultPairs() {
		
		TelesinaHandStrengthEvaluator hte = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		HandResultCreator creator = new HandResultCreator(hte);
		HandResultCalculator resultCalculator = new HandResultCalculator(new TelesinaHandComparator(hte));
		
		Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();

		PokerPlayer pp1 = mockPlayer(1, 50, false, false, new Hand("8S 8C JC TD QH"));
		PokerPlayer pp2 = mockPlayer(2, 50, false, false, new Hand("TH TD QD 9S JH")); // pp2 wins with higher pair
		
		playerMap.put(1, pp1);
		playerMap.put(2, pp2);

		PotHolder potHolder = new PotHolder(new LinearSingleLimitRakeCalculator(new RakeSettings(BigDecimal.ZERO)));
		potHolder.moveChipsToPot(playerMap.values());

		List<Card> communityCards = Card.list("7S"); // Will not be used
		
		HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>());

		System.out.println("HANDS: "+result.getPlayerHands());
		
		assertNotNull(result);
		
		Map<Integer, Long> resultsSimplified = new HashMap<Integer, Long>();
		for (Entry<PokerPlayer, Result> entry : result.getResults().entrySet()) {
			resultsSimplified.put(entry.getKey().getId(), entry.getValue().getNetResult());
		}
		
		assertEquals(-50L, (long) resultsSimplified.get(1));
		assertEquals(50L, (long) resultsSimplified.get(2));
	}
	
	@Test
	public void testCreatePotTransitionsByResults() {
        HandResultCreator creator = new HandResultCreator(null);
	    
	    Map<PokerPlayer, Result> playerResults = new HashMap<PokerPlayer, Result>();
	    
        Pot pot0 = mock(Pot.class);
        Pot pot1 = mock(Pot.class);
        Pot pot2 = mock(Pot.class);
	    
	    PokerPlayer player1 = mock(PokerPlayer.class);
	    Map<Pot, Long> winningsByPot1 = new HashMap<Pot, Long>();
        winningsByPot1.put(pot0, 20L);
        winningsByPot1.put(pot1, 30L);
        winningsByPot1.put(pot2, 50L);
        Result result1 = new Result(100L, 10L, winningsByPot1);
	    
        PokerPlayer player2 = mock(PokerPlayer.class);
        Result result2 = new Result(0L, 10L, new HashMap<Pot, Long>());
	    
        playerResults.put(player1, result1);
        playerResults.put(player2, result2);
	    
        Collection<PotTransition> potTrans = creator.createPotTransitionsByResults(playerResults);
        
        assertThat(potTrans.size(), is(3));
        assertThat(potTrans, JUnitMatchers.hasItem(new PotTransition(player1, pot0, 20)));
        assertThat(potTrans, JUnitMatchers.hasItem(new PotTransition(player1, pot1, 30)));
        assertThat(potTrans, JUnitMatchers.hasItem(new PotTransition(player1, pot2, 50)));
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
