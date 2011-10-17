package com.cubeia.poker.pot;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;

import com.cubeia.poker.RakeSettings;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rake.RakeCalculatorImpl;

public class PotRakeTest {
    
    @Test
    public void testMoveChipsToPotWithRakeSimple() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        long bet = 1000L;
        when(player1.getBetStack()).thenReturn(bet);
        when(player2.getBetStack()).thenReturn(bet);
        BigDecimal rakeFraction = new BigDecimal("0.1");
        PotHolder ph = new PotHolder(new RakeCalculatorImpl(new RakeSettings(rakeFraction)));
        
        ph.moveChipsToPot(asList(player1, player2));
        
        assertThat(ph.getTotalPotSize(), is(bet * 2));
        assertThat(ph.getTotalRake(), is(BigDecimal.valueOf(bet * 2).multiply(rakeFraction)));
        
        assertThat(ph.getNumberOfPots(), is(1));
        Pot pot0 = ph.getPot(0);
        assertThat(pot0.getPotSize(), is(bet + bet));
        assertThat(pot0.getRake(), is(BigDecimal.valueOf(2 * bet).multiply(rakeFraction)));
        assertThat(pot0.getPotSizeWithRakeRemoved(), is(2 * bet - pot0.getRake().intValue()));
    }
    
    @Test
    public void testMoveChipsToPotWithRakeSidePot() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        // Player 1: bet  500 (all in) -> pot 0: 500, pot 1: -
        // Player 2: bet 2000          -> pot 0: 500, pot 1: 1500
        // Player 3: bet 2000          -> pot 0: 500, pot 1: 1500
        long betPot0 =  500L;
        long betPot1 = 1500L;
        when(player1.getBetStack()).thenReturn(betPot0);
        when(player1.isAllIn()).thenReturn(true);
        when(player2.getBetStack()).thenReturn(betPot0 + betPot1);
        when(player3.getBetStack()).thenReturn(betPot0 + betPot1);
        
        BigDecimal rakeFraction = new BigDecimal("0.1");
        PotHolder ph = new PotHolder(new RakeCalculatorImpl(new RakeSettings(rakeFraction )));
        
        ph.moveChipsToPot(asList(player1, player2, player3));
        
        assertThat(ph.getTotalPotSize(), is(betPot0 * 3 + betPot1 * 2));
        assertThat(ph.getTotalRake(), is(BigDecimal.valueOf(betPot0 * 3 + betPot1 * 2).multiply(rakeFraction)));
        
        assertThat(ph.getNumberOfPots(), is(2));
        Pot pot0 = ph.getPot(0);
        assertThat(pot0.getPotContributors().keySet().size(), is(3));
        assertThat(pot0.getPotContributors().keySet(), hasItems(player1, player2, player3));
        assertThat(pot0.getPotSize(), is(betPot0 * 3));
        assertThat(pot0.getRake(), is(BigDecimal.valueOf(3 * betPot0).multiply(rakeFraction)));
        assertThat(pot0.getPotSizeWithRakeRemoved(), is(3 * betPot0 - pot0.getRake().intValue()));

        Pot pot1 = ph.getPot(1);
        assertThat(pot1.getPotContributors().keySet().size(), is(2));
        assertThat(pot1.getPotContributors().keySet(), hasItems(player2, player3));
        assertThat(pot1.getPotSize(), is(betPot1 * 2));
        assertThat(pot1.getRake(), is(BigDecimal.valueOf(2 * betPot1).multiply(rakeFraction)));
        assertThat(pot1.getPotSizeWithRakeRemoved(), is(2 * betPot1 - pot1.getRake().intValue()));
    }

    @Test
    public void testMoveChipsToPotWithRakeSidePotAndLimit() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        // Player 1: bet  500 (all in) -> pot 0: 500, pot 1: -
        // Player 2: bet 2000          -> pot 0: 500, pot 1: 1500
        // Player 3: bet 2000          -> pot 0: 500, pot 1: 1500
        long betPot0 =  500L;
        long betPot1 = 1500L;
        when(player1.getBetStack()).thenReturn(betPot0);
        when(player1.isAllIn()).thenReturn(true);
        when(player2.getBetStack()).thenReturn(betPot0 + betPot1);
        when(player3.getBetStack()).thenReturn(betPot0 + betPot1);
        
        // Rake limit 300: pot 0 rake = 150, pot 1 rake = 150
        long rakeLimit = 300L;
        BigDecimal rakeFraction = new BigDecimal("0.1");
        PotHolder ph = new PotHolder(new RakeCalculatorImpl(new RakeSettings(rakeFraction, rakeLimit)));
        
        ph.moveChipsToPot(asList(player1, player2, player3));
        
        assertThat(ph.getTotalPotSize(), is(betPot0 * 3 + betPot1 * 2));
        assertThat(ph.getTotalRake(), is(BigDecimal.valueOf(rakeLimit).setScale(1)));
        
        assertThat(ph.getNumberOfPots(), is(2));
        Pot pot0 = ph.getPot(0);
        assertThat(pot0.getPotSize(), is(betPot0 * 3));
        assertThat(pot0.getRake(), is(BigDecimal.valueOf(3 * betPot0).multiply(rakeFraction)));

        Pot pot1 = ph.getPot(1);
        assertThat(pot1.getPotSize(), is(betPot1 * 2));
        assertThat(pot1.getRake(), is(BigDecimal.valueOf(rakeLimit - pot0.getRake().intValue()).setScale(1)));
    }
    
}
