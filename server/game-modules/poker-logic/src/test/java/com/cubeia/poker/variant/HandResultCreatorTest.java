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

package com.cubeia.poker.variant;

import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.HandResultCalculator;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.variant.telesina.hand.TelesinaHandStrengthEvaluator;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class HandResultCreatorTest {

    public static final BigDecimal RAKE_FRACTION = new BigDecimal("0.04");
    public static final long RAKE_LIMIT = 500;
    public static final long RAKE_LIMIT_HEADS_UP = 150;

    TelesinaHandStrengthEvaluator hte;
    HandResultCalculator resultCalculator;
    Map<Integer, PokerPlayer> playerMap;
    HandResultCreator creator;
    List<Card> communityCards;
    PotHolder potHolder;

    @SuppressWarnings("unused")
    private Answer<Object> THROW_EXCEPTION = new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            throw new RuntimeException("Called unmocked method: " + invocation.getMethod().getName());
        }
    };


    private void setupStuff(String velaCard) {
        hte = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
        resultCalculator = new HandResultCalculator(hte);

        creator = new HandResultCreator(hte);
        playerMap = new HashMap<Integer, PokerPlayer>();
        communityCards = Card.list(velaCard);
        potHolder = new PotHolder(new LinearRakeWithLimitCalculator(new RakeSettings(RAKE_FRACTION, RAKE_LIMIT, RAKE_LIMIT_HEADS_UP)));
    }

    @Test
    public void testFilterMuckingPlayers() {
        setupStuff("7S");
        PokerPlayer pp1 = mockPlayer(1, 50, false, false, new Hand("7S 8S JC QC QH"));
        String player2Hand = "7H 8D JD QS 9H";
        PokerPlayer pp2 = mockPlayer(2, 50, false, false, new Hand(player2Hand)); // pp2 wins using vela card
        playerMap.put(1, pp1);
        playerMap.put(2, pp2);

        potHolder.moveChipsToPotAndTakeBackUncalledChips(playerMap.values());

        Set<PokerPlayer> muckingPlayers = new HashSet<PokerPlayer>();
        muckingPlayers.add(pp2);
        HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>(), muckingPlayers);
        List<RatedPlayerHand> ratedPlayerHands = result.getPlayerHands();
        assertEquals(1, ratedPlayerHands.size());
    }


    @Test
    public void testCreateHandResultTelesinaStyle() {
        setupStuff("TS");
        PokerPlayer pp1 = mockPlayer(1, 50, false, false, new Hand("7S 8S JC QC QH"));
        PokerPlayer pp2 = mockPlayer(2, 50, false, false, new Hand("7H 8D JD QS 9H")); // pp2 wins using vela card
        playerMap.put(1, pp1);
        playerMap.put(2, pp2);
        potHolder.moveChipsToPotAndTakeBackUncalledChips(playerMap.values());

        Set<PokerPlayer> muckingPlayers = new HashSet<PokerPlayer>(playerMap.values());

        HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>(), muckingPlayers);

        assertNotNull(result);

        Map<Integer, Long> resultsSimplified = new HashMap<Integer, Long>();
        for (Entry<PokerPlayer, Result> entry : result.getResults().entrySet()) {
            resultsSimplified.put(entry.getKey().getId(), entry.getValue().getNetResult());
        }

        assertEquals(-50L, (long) resultsSimplified.get(1));
        assertEquals(50L, (long) resultsSimplified.get(2));
    }


    @Test
    public void testCreateHandResultForMultiPot4PlayerHand() {
        setupStuff("TS");
        PokerPlayer pp1 = mockPlayer(1, 456, true, false, new Hand("TD TC TH 8D 9D"));
        PokerPlayer pp2 = mockPlayer(2, 1612, false, false, new Hand("JC JC JC QS KH"));
        PokerPlayer pp3 = mockPlayer(3, 1612, false, false, new Hand("7H 8D JD QS AH"));
        PokerPlayer pp4 = mockPlayer(4, 100, false, true, new Hand("7H 8D JD QS 9H"));


        playerMap.put(1, pp1);
        playerMap.put(2, pp2);
        playerMap.put(3, pp3);
        playerMap.put(4, pp4);
        potHolder.moveChipsToPotAndTakeBackUncalledChips(playerMap.values());
        potHolder.callOrRaise();

        Set<PokerPlayer> muckingPlayers = new HashSet<PokerPlayer>(playerMap.values());

        HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>(), muckingPlayers);

        assertNotNull(result);

        long resultTotalRake = result.getTotalRake();
        long resultTotalNet = 0;
        long totalBets = 0;
        long totalWins = 0;

        for (Result res : result.getResults().values()) {
            resultTotalNet += res.getNetResult();
            totalBets += res.getBets();
            totalWins += res.getWinningsIncludingOwnBets();
        }

        long expectedTotalRake = 150;

        assertEquals(expectedTotalRake, resultTotalRake);
        assertEquals(expectedTotalRake, totalBets - totalWins);
        assertEquals(-expectedTotalRake, resultTotalNet);

        long totalContributedRake = 0;
        for (PokerPlayer player : playerMap.values()) {
            totalContributedRake += result.getRakeContributionByPlayer(player);
        }

        assertEquals(expectedTotalRake, totalContributedRake);
    }

    @Test
    public void testCreateHandResultPairs() {
        setupStuff("7S");
        PokerPlayer pp1 = mockPlayer(1, 50, false, false, new Hand("8S 8C JC TD QH"));
        PokerPlayer pp2 = mockPlayer(2, 50, false, false, new Hand("TH TD QD 9S JH")); // pp2 wins with higher pair
        playerMap.put(1, pp1);
        playerMap.put(2, pp2);
        potHolder.moveChipsToPotAndTakeBackUncalledChips(playerMap.values());

        Set<PokerPlayer> muckingPlayers = new HashSet<PokerPlayer>(playerMap.values());

        HandResult result = creator.createHandResult(communityCards, resultCalculator, potHolder, playerMap, new ArrayList<Integer>(), muckingPlayers);

        System.out.println("HANDS: " + result.getPlayerHands());

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
        PokerPlayer pp = mock(PokerPlayer.class);
        doReturn(playerId).when(pp).getId();
        doReturn(betStack).when(pp).getBetStack();
        doReturn(allIn).when(pp).isAllIn();
        doReturn(folded).when(pp).hasFolded();
        doReturn(pocketCards).when(pp).getPocketCards();


        doReturn("Player" + playerId).when(pp).toString();

        return pp;
    }
}
