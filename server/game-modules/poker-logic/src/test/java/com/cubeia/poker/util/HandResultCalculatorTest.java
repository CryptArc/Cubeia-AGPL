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

package com.cubeia.poker.util;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.PlayerHand;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.HandResultCalculator;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class HandResultCalculatorTest extends TestCase {

    private Map<Integer, PokerPlayer> players;

    HandResultCalculator calc = new HandResultCalculator(new TexasHoldemHandCalculator());

    private ArrayList<PlayerHand> hands;
    private LinearRakeWithLimitCalculator rakeCalculator;

    private BigDecimal rakeFraction;

    private int player1Bets = 10;
    private int player2Bets = 20;
    private int player3Bets = 40;


    @Override
    protected void setUp() throws Exception {
        // All players has 100 and bets 10, 20 and 40 respectively
        players = new HashMap<Integer, PokerPlayer>();
        PokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.addChips(100);
        p1.addBet(player1Bets);
        PokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.addChips(100);
        p2.addBet(player2Bets);
        PokerPlayer p3 = new DefaultPokerPlayer(3);
        p3.addChips(100);
        p3.addBet(player3Bets);

        players.put(1, p1);
        players.put(2, p2);
        players.put(3, p3);

        hands = new ArrayList<PlayerHand>();

        String community = "Ac Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("As Ad " + community))); // Best Hand - 3 Aces
        hands.add(new PlayerHand(2, new Hand("2s 7d " + community)));
        hands.add(new PlayerHand(3, new Hand("3s 8d " + community)));

        rakeFraction = new BigDecimal("0.1");
        rakeCalculator = new LinearRakeWithLimitCalculator(RakeSettings.createNoLimitRakeSettings(rakeFraction));
    }


    public void testSimpleCaseWithRake() {
        PotHolder potHolder = new PotHolder(rakeCalculator);
        potHolder.callOrRaise();
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertEquals(1, potHolder.getNumberOfPots());
        assertEquals(50, potHolder.getPotSize(0));
        Pot pot0 = potHolder.getPot(0);
        int pot0Rake = (int) (50 * 0.1); // remember that one of the players overbet the others and took back some of the bets

        long p1stake = pot0.getPotContributors().get(players.get(1));
        assertEquals(10, p1stake);
        long p2stake = pot0.getPotContributors().get(players.get(2));
        assertEquals(20, p2stake);
        long p3stake = pot0.getPotContributors().get(players.get(3));
        assertEquals(20, p3stake);

        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, potHolder.calculateRake(), players);

        Result result1 = playerResults.get(players.get(1));
        assertThat(result1.getNetResult(), is(50L - player1Bets - pot0Rake));
        assertThat(result1.getWinningsIncludingOwnBets(), is(50L - pot0Rake));

        assertThat(result1.getWinningsByPot().size(), is(1));
        assertThat(result1.getWinningsByPot().get(potHolder.getActivePot()), is(50L - pot0Rake));

        assertEquals(3, playerResults.size());

        Result result2 = playerResults.get(players.get(2));
        assertThat(result2.getNetResult(), is((long) -player2Bets));
        assertThat(result2.getWinningsByPot().isEmpty(), is(true));
        Result result3 = playerResults.get(players.get(3));
        assertThat(result3.getNetResult(), is((long) -player2Bets)); // remember that player 3 only bet player 2's bet in the end since he took some back
        assertThat(result3.getWinningsByPot().isEmpty(), is(true));
    }


    public void testGetWinnersWithRake() {
        hands = new ArrayList<PlayerHand>();
        String community = "Ac Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("As Ad " + community))); // SPLIT HAND - 3 Aces
        hands.add(new PlayerHand(2, new Hand("As Ad " + community))); // SPLIT HAND - 3 Aces
        hands.add(new PlayerHand(3, new Hand("3s 8d " + community)));

        PotHolder potHolder = new PotHolder(rakeCalculator);
        potHolder.callOrRaise();
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertThat(potHolder.getNumberOfPots(), is(1));
        long pot0Size = potHolder.getPotSize(0);
        assertThat(pot0Size, is(50L)); // one of the players had a overbet so player 3 actually only bet whan player 2 did.
        Pot pot0 = potHolder.getPot(0);
        int pot0Rake = (int) (pot0Size * 0.1);

        long p1stake = pot0.getPotContributors().get(players.get(1));
        assertEquals(10, p1stake);
        long p2stake = pot0.getPotContributors().get(players.get(2));
        assertEquals(20, p2stake);
        long p3stake = pot0.getPotContributors().get(players.get(3));
        assertEquals(20, p3stake);

        assertEquals(1, potHolder.getNumberOfPots());

        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, potHolder.calculateRake(), players);

        Result result1 = playerResults.get(players.get(1));
        long pot0WinningShare = (long) (50 - pot0Rake) / 2;
        assertThat(result1.getNetResult(), is(pot0WinningShare - player1Bets + 1)); // This guy gets the rounded cent.
        assertThat(result1.getWinningsIncludingOwnBets(), is(pot0WinningShare + 1));
        assertThat(result1.getWinningsByPot().size(), is(1));
        assertThat(result1.getWinningsByPot().get(pot0), is(pot0WinningShare + 1));

        Result result2 = playerResults.get(players.get(2));
        assertThat(result2.getNetResult(), is(pot0WinningShare - player2Bets));
        assertThat(result2.getWinningsIncludingOwnBets(), is(pot0WinningShare));

        assertThat(result2.getWinningsByPot().size(), is(1));
        assertThat(result2.getWinningsByPot().get(pot0), is(pot0WinningShare));

        Result result3 = playerResults.get(players.get(3));
        assertEquals(-20, result3.getNetResult());
        assertEquals(0, result3.getWinningsIncludingOwnBets());
        assertThat(result3.getWinningsByPot().size(), is(0));

        assertThat(playerResults.size(), is(3));
    }

    public void testMultiplePotsWithRake() {
        players = new HashMap<Integer, PokerPlayer>();
        PokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.addChips(100);
        p1.addBet(80);
        PokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.addChips(100);
        p2.addBet(80);
        PokerPlayer p3 = new DefaultPokerPlayer(3);
        p3.addChips(40);
        p3.addBet(40);

        assertTrue(p3.isAllIn());

        players.put(1, p1);
        players.put(2, p2);
        players.put(3, p3);

        PotHolder potHolder = new PotHolder(rakeCalculator);
        potHolder.callOrRaise();
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertEquals(2, potHolder.getNumberOfPots());

        hands = new ArrayList<PlayerHand>();
        String community = " Ac Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("Ks 8d" + community))); // Second best hand - 2 Kings
        hands.add(new PlayerHand(2, new Hand("2s 7d" + community)));
        hands.add(new PlayerHand(3, new Hand("As Ad" + community))); // Best Hand - 3 Aces

        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, potHolder.calculateRake(), players);

        assertEquals(3, playerResults.size());

        Pot pot0 = potHolder.getPot(0);
        assertThat(pot0.getPotSize(), is(120L));


        Result result1 = playerResults.get(players.get(1));
        assertThat(result1.getNetResult(), is(80L - 8 - 80));
        assertThat(result1.getWinningsIncludingOwnBets(), is(80 - 8L));

        assertThat(result1.getWinningsByPot().size(), is(1));
        assertThat(result1.getWinningsByPot().get(potHolder.getPot(1)), is(80L - 8L));


        Result result2 = playerResults.get(players.get(2));
        assertEquals(-80, result2.getNetResult());
        assertEquals(0, result2.getWinningsIncludingOwnBets());
        assertThat(result2.getWinningsByPot().isEmpty(), is(true));


        Result result3 = playerResults.get(players.get(3));
        assertThat(result3.getNetResult(), is(120L - 12L - 40L));
        assertThat(result3.getWinningsIncludingOwnBets(), is(120L - 12L));
        assertThat(result3.getWinningsByPot().size(), is(1));
        assertThat(result3.getWinningsByPot().get(pot0), is(120L - 12L));
    }

    public void testRoundingErrorsWhenSplitPot() {
        players = new HashMap<Integer, PokerPlayer>();
        PokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.addChips(100);
        p1.addBet(80);
        PokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.addChips(100);
        p2.addBet(80);
        PokerPlayer p3 = new DefaultPokerPlayer(3);
        p3.addChips(35);
        p3.addBet(35);

        players.put(1, p1);
        players.put(2, p2);
        players.put(3, p3);

        PotHolder potHolder = new PotHolder(rakeCalculator);
        potHolder.callOrRaise();
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        hands = new ArrayList<PlayerHand>();
        String community = " 2c Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("As Ad" + community))); // Best Hand
        hands.add(new PlayerHand(2, new Hand("Ac Ah" + community))); // Best Hand - split
        hands.add(new PlayerHand(3, new Hand("Ks 8d" + community)));

        RakeInfoContainer totalRake = potHolder.calculateRake();
        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, totalRake, players);

        Result result1 = playerResults.get(players.get(1));
        Result result2 = playerResults.get(players.get(2));
        Result result3 = playerResults.get(players.get(3));

        long netResultSum = result1.getNetResult() + result2.getNetResult() + result3.getNetResult();
        assertThat(netResultSum + totalRake.getTotalRake(), is(0L));
    }

    public void testMultiplePotsNoRake() {
        players = new HashMap<Integer, PokerPlayer>();
        PokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.addChips(100);
        p1.addBet(80);
        PokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.addChips(100);
        p2.addBet(80);
        PokerPlayer p3 = new DefaultPokerPlayer(3);
        p3.addChips(40);
        p3.addBet(40);

        assertTrue(p3.isAllIn());

        players.put(1, p1);
        players.put(2, p2);
        players.put(3, p3);

        PotHolder potHolder = new PotHolder(new LinearRakeWithLimitCalculator(RakeSettings.createNoLimitRakeSettings(ZERO)));
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertEquals(2, potHolder.getNumberOfPots());

        hands = new ArrayList<PlayerHand>();
        String community = " Ac Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("Ks 8d" + community))); // Second best hand - 2 Kings
        hands.add(new PlayerHand(2, new Hand("2s 7d" + community)));
        hands.add(new PlayerHand(3, new Hand("As Ad" + community))); // Best Hand - 3 Aces

        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, potHolder.calculateRake(), players);

        assertEquals(3, playerResults.size());

        Result result1 = playerResults.get(players.get(1));
        assertEquals(0, result1.getNetResult());
        assertEquals(80, result1.getWinningsIncludingOwnBets());
        assertThat(result1.getWinningsByPot().size(), is(1));
        assertThat(result1.getWinningsByPot().get(potHolder.getPot(1)), is(80L));


        Result result2 = playerResults.get(players.get(2));
        assertEquals(-80, result2.getNetResult());
        assertEquals(0, result2.getWinningsIncludingOwnBets());
        assertThat(result2.getWinningsByPot().isEmpty(), is(true));


        Result result3 = playerResults.get(players.get(3));
        assertEquals(80, result3.getNetResult());
        assertEquals(120, result3.getWinningsIncludingOwnBets());
        assertThat(result3.getWinningsByPot().size(), is(1));
        assertThat(result3.getWinningsByPot().get(potHolder.getPot(0)), is(120L));

        assertEquals(0, result1.getNetResult() + result2.getNetResult() + result3.getNetResult());
    }


    public void testMultipleBetsNoRake() {
        players = new HashMap<Integer, PokerPlayer>();
        PokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.addChips(100);
        p1.addBet(10);
        PokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.addChips(100);
        p2.addBet(20);
        PokerPlayer p3 = new DefaultPokerPlayer(3);
        p3.addChips(100);
        p3.addBet(40); // this is a overbet. This player will be returned the difference between p3 and p2's bet

        players.put(1, p1);
        players.put(2, p2);
        players.put(3, p3);

        hands = new ArrayList<PlayerHand>();
        String community = " Ac Kc Qd 6h Th";
        hands.add(new PlayerHand(1, new Hand("As Ad" + community))); // Best Hand - 3 Aces
        hands.add(new PlayerHand(2, new Hand("2s 7d" + community)));
        hands.add(new PlayerHand(3, new Hand("3s 8d" + community)));

        PotHolder potHolder = new PotHolder(new LinearRakeWithLimitCalculator(RakeSettings.createNoLimitRakeSettings(ZERO)));
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertEquals(1, potHolder.getNumberOfPots());
        assertEquals(50, potHolder.getPotSize(0)); // remember the overbet

        // do another bet. This time without overbets
        p1.addBet(10);
        p2.addBet(20);
        p3.addBet(20);
        potHolder.moveChipsToPotAndTakeBackUncalledChips(players.values());

        assertEquals(1, potHolder.getNumberOfPots());
        assertEquals(100, potHolder.getPotSize(0)); // the total is 100
        long p1stake = potHolder.getActivePot().getPotContributors().get(players.get(1));
        assertEquals(20, p1stake);
        long p2stake = potHolder.getActivePot().getPotContributors().get(players.get(2));
        assertEquals(40, p2stake);
        long p3stake = potHolder.getActivePot().getPotContributors().get(players.get(3));
        assertEquals(40, p3stake);

        Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, potHolder.calculateRake(), players);

        Result result1 = playerResults.get(players.get(1));
        assertEquals(80, result1.getNetResult()); // the two other players betted 20+20 each
        assertEquals(100, result1.getWinningsIncludingOwnBets()); // the other players betted 20+20 each and player 1 only bet 10+10
        assertThat(result1.getWinningsByPot().size(), is(1));
        assertThat(result1.getWinningsByPot().get(potHolder.getPot(0)), is(100L));

        assertEquals(3, playerResults.size());

    }
}
