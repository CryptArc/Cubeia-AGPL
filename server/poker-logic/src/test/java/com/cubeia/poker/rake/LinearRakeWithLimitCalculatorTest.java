package com.cubeia.poker.rake;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.RakeSettings;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;

public class LinearRakeWithLimitCalculatorTest {

    @Test
    public void testCalculateRakeNoLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearRakeWithLimitCalculator rc = new LinearRakeWithLimitCalculator(RakeSettings.createNoLimitRakeSettings(rakeFraction));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        Pot pot3 = new Pot(2);

        pot1.bet(player1, 10000L);
        pot1.bet(player2, 10000L);
        pot2.bet(player1, 3000L);
        pot2.bet(player2, 2000L);
        pot3.bet(player1, 1000L);
        
        RakeInfoContainer rakeInfoContainer = rc.calculateRakes(asList(pot1, pot2, pot3), true);
        assertThat(rakeInfoContainer.getTotalPot(), is(26000));
        assertThat(rakeInfoContainer.getTotalRake(), is(2600));
        
        Map<Pot, BigDecimal> rakes = rakeInfoContainer.getPotRakes();
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 10000))));
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(3000 + 2000))));
        assertThat(rakes.get(pot3), is(rakeFraction.multiply(BigDecimal.valueOf(1000))));
    }
    
    @Test
    public void testCalculateRakeWithLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearRakeWithLimitCalculator rc = new LinearRakeWithLimitCalculator(new RakeSettings(rakeFraction, 4000L, 1000L));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player3 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        Pot pot3 = new Pot(2);
        Pot pot4 = new Pot(3);
        
        pot1.bet(player1,  7000L);
        pot1.bet(player2,  7000L);
        pot1.bet(player3,  7000L);
        
        pot2.bet(player1, 10000L);
        pot2.bet(player2,  5000L);
        pot2.bet(player3,  3000L);
        
        pot3.bet(player1, 10000L);
        pot4.bet(player2, 10000L);
        
        Collection<Pot> pots = Arrays.asList(pot1, pot2, pot3, pot4);

        RakeInfoContainer rakeInfoContainer = rc.calculateRakes(pots, true);
        assertThat(rakeInfoContainer.getTotalPot(), is(7000 * 3 + 10000 + 5000 + 3000 + 10000 + 10000));
        assertThat(rakeInfoContainer.getTotalRake(), is(4000));
        
        Map<Pot, BigDecimal> rakes = rakeInfoContainer.getPotRakes();
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(7000 * 3))));            // 2100
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 5000 + 3000)))); // 1800
        assertThat(rakes.get(pot3), is(new BigDecimal("100.0"))); // 100 (limited)
        assertThat(rakes.get(pot4), is(new BigDecimal("0.0")));   // 0 (over limit)
    }

    @Test
    public void testCalculateRakeWithLimitHeadsUp() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearRakeWithLimitCalculator rc = new LinearRakeWithLimitCalculator(new RakeSettings(rakeFraction, 4000L, 150L));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        
        pot1.bet(player1,   500L);
        pot1.bet(player2,   500L);
        pot2.bet(player1, 50000L);
        pot2.bet(player2, 50000L);
        
        Collection<Pot> pots = asList(pot1, pot2);

        RakeInfoContainer rakeInfoContainer = rc.calculateRakes(pots, true);
        assertThat(rakeInfoContainer.getTotalPot(), is((50000 + 500) * 2));
        assertThat(rakeInfoContainer.getTotalRake(), is(150));
        
        Map<Pot, BigDecimal> rakes = rakeInfoContainer.getPotRakes();
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(500 * 2))));
        assertThat(rakes.get(pot2), is(new BigDecimal("50.0")));
    }
    
    @Test
    public void testCalculateRakeNoRakeBeforeFirstCall() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearRakeWithLimitCalculator rc = new LinearRakeWithLimitCalculator(RakeSettings.createNoLimitRakeSettings(rakeFraction));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);

        pot1.bet(player1, 10000L);
        pot1.bet(player2, 10000L);
        
        RakeInfoContainer rakeInfoContainer = rc.calculateRakes(asList(pot1), false);
        assertThat(rakeInfoContainer.getTotalPot(), is(20000));
        assertThat(rakeInfoContainer.getTotalRake(), is(0));
        
        Map<Pot, BigDecimal> rakes = rakeInfoContainer.getPotRakes();
        assertThat(rakes.get(pot1), is(ZERO));
    }
    
}
