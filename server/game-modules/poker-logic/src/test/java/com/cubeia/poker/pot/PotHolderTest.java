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

package com.cubeia.poker.pot;

import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.RakeCalculator;
import com.cubeia.poker.pot.RakeInfoContainer;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PotHolderTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testRakeCalculationGetsCallFlag() {
        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        PotHolder potHolder = new PotHolder(rakeCalculator);

        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(false));

        potHolder.callOrRaise();
        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(true));
    }


    @Test
    public void calculateRakeIncludingBetStacks() {
        PokerPlayer player0 = mock(PokerPlayer.class);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);

        when(player0.getBetStack()).thenReturn(1000L);
        when(player1.getBetStack()).thenReturn(0L);
        when(player2.getBetStack()).thenReturn(0L);

        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        PotHolder potHolder = new PotHolder(rakeCalculator);

        Pot pot0 = mock(Pot.class);
        Pot pot1 = mock(Pot.class);

        Map<PokerPlayer, Long> pot0Contributors = new HashMap<PokerPlayer, Long>();
        pot0Contributors.put(player0, 10L);
        pot0Contributors.put(player1, 0L);
        pot0Contributors.put(player2, 0L);
        when(pot0.getPotContributors()).thenReturn(pot0Contributors);

        Map<PokerPlayer, Long> pot1Contributors = new HashMap<PokerPlayer, Long>();
        pot1Contributors.put(player0, 50L);
        pot1Contributors.put(player1, 10L);
        pot1Contributors.put(player2, 0L);
        when(pot1.getPotContributors()).thenReturn(pot1Contributors);

        potHolder.addPot(pot0);
        potHolder.addPot(pot1);

        assertThat(potHolder.calculatePlayersContributionToPotIncludingBetStacks(player0), is(1060L));
        assertThat(potHolder.calculatePlayersContributionToPotIncludingBetStacks(player1), is(10L));
        assertThat(potHolder.calculatePlayersContributionToPotIncludingBetStacks(player2), is(0L));

    }

    @Test
    public void testHeadsupRakeIncludingBetStacksWithRakeCap() {

        PokerPlayer player0 = mock(PokerPlayer.class);
        PokerPlayer player1 = mock(PokerPlayer.class);

        when(player0.getBetStack()).thenReturn(400L);
        when(player1.getBetStack()).thenReturn(200L);

        RakeSettings rakeSettings = new RakeSettings(new BigDecimal("0.06"), 500, 150);
        RakeCalculator rakeCalculator = new LinearRakeWithLimitCalculator(rakeSettings);

        PotHolder potHolder = new PotHolder(rakeCalculator);
        Pot mainPot = new Pot(0);
        potHolder.callOrRaiseHasBeenMadeInHand = true;

        potHolder.addPot(mainPot);

        mainPot.bet(player0, 100L); // ante
        mainPot.bet(player1, 100L); // ante

        mainPot.bet(player0, 5000L); // raise
        mainPot.bet(player1, 10000L); // raise
        mainPot.bet(player0, 5000L); // call

        //uncapped rake excluding betstacks should be 1212 that is 20200 (total pot) * 0.06 (rake)

        // check that the total rake is 150 (capped)
        RakeInfoContainer calculatedRake = potHolder.calculateRake();
        assertThat(calculatedRake.getTotalRake(), is(150L));

        //uncapped rake excluding betstacks should be 1248 that is 20800 (total pot) * 0.06 (rake)

        //check that the total rake including betstacks is 150 (capped)
        Collection<PokerPlayer> players = new ArrayList<PokerPlayer>();
        players.add(player0);
        players.add(player1);
        RakeInfoContainer calculatedRakeIncludingBetStacks = potHolder.calculateRakeIncludingBetStacks(players);

        assertThat(calculatedRakeIncludingBetStacks.getTotalRake(), is(150L));

    }

    @Test
    public void testMoveChipsToPotAndTakeBackUncalledChips() {

        PokerPlayer player0 = mock(PokerPlayer.class);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);

        ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
        players.add(player0);
        players.add(player1);
        players.add(player2);

        when(player0.getBetStack()).thenReturn(1000L);
        when(player1.getBetStack()).thenReturn(100L);
        when(player2.getBetStack()).thenReturn(100L);

        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        PotHolder potHolder = new PotHolder(rakeCalculator);

        Collection<PotTransition> transfers = potHolder.moveChipsToPotAndTakeBackUncalledChips(players);

        assertThat(transfers.size(), is(4)); // three moves to main pot. One move back to balance

    }

}
