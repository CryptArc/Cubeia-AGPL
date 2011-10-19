package com.cubeia.poker.rake;

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
import com.cubeia.poker.pot.PotTransition;

public class LinearSingleLimitRakeCalculatorTest {

    @Test
    public void testCalculateRakeAdditionNoLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearSingleLimitRakeCalculator rc = new LinearSingleLimitRakeCalculator(new RakeSettings(rakeFraction));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        Pot pot3 = new Pot(2);
        
        PotTransition pt1 = new PotTransition(player1, pot1, 10000);
        PotTransition pt2 = new PotTransition(player2, pot1, 10000);
        PotTransition pt3 = new PotTransition(player1, pot2, 3000);
        PotTransition pt4 = new PotTransition(player2, pot2, 2000);
        PotTransition pt5 = new PotTransition(player1, pot3, 1000);
        
        Collection<PotTransition> potTransitions = Arrays.asList(pt1, pt2, pt3, pt4, pt5);
        Map<Pot, BigDecimal> rakes = rc.calculateRakeAddition(BigDecimal.valueOf(20000), potTransitions);
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 10000))));//    (int) ((10000 + 10000) * 0.1)));
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(3000 + 2000))));//  (int) ((3000 + 2000) * 0.1)));
        assertThat(rakes.get(pot3), is(rakeFraction.multiply(BigDecimal.valueOf(1000))));//  (int) ((1000) * 0.1)));
    }
    
    @Test
    public void testCalculateRakeAdditionWithLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearSingleLimitRakeCalculator rc = new LinearSingleLimitRakeCalculator(new RakeSettings(rakeFraction, 4000L));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        Pot pot3 = new Pot(2);
        
        PotTransition pt1 = new PotTransition(player1, pot1, 10000);
        PotTransition pt2 = new PotTransition(player2, pot1, 10000);
        PotTransition pt3 = new PotTransition(player1, pot2, 10000);
        PotTransition pt4 = new PotTransition(player2, pot2,  9000);
        PotTransition pt5 = new PotTransition(player1, pot3, 10000);
        
        Collection<PotTransition> potTransitions = Arrays.asList(pt1, pt2, pt3, pt4, pt5);

        // no prior rake taken
        Map<Pot, BigDecimal> rakes = rc.calculateRakeAddition(BigDecimal.ZERO, potTransitions);
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 10000)))); // (int) ((10000 + 10000) * 0.1))); // 2000
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 9000))));  //(int) ((10000 +  9000) * 0.1))); // 1900
        assertThat(rakes.get(pot3), is(new BigDecimal("100.0"))); //                          // 100 (limited)
        
        // already took some rake
        rakes = rc.calculateRakeAddition(BigDecimal.valueOf(3500), potTransitions);
        assertThat(rakes.get(pot1), is(new BigDecimal("500"))); 
        assertThat(rakes.get(pot2), is(BigDecimal.ZERO)); 
        assertThat(rakes.get(pot3), is(BigDecimal.ZERO)); 
    }
    
    @Test
    public void testCalculateRakeNoLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearSingleLimitRakeCalculator rc = new LinearSingleLimitRakeCalculator(new RakeSettings(rakeFraction));
        
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
        
        Map<Pot, BigDecimal> rakes = rc.calculateRakes(asList(pot1, pot2, pot3));
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 10000))));
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(3000 + 2000))));
        assertThat(rakes.get(pot3), is(rakeFraction.multiply(BigDecimal.valueOf(1000))));
    }
    
    @Test
    public void testCalculateRakeWithLimit() {
        BigDecimal rakeFraction = new BigDecimal("0.1");
        LinearSingleLimitRakeCalculator rc = new LinearSingleLimitRakeCalculator(new RakeSettings(rakeFraction, 4000L));
        
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Pot pot1 = new Pot(0);
        Pot pot2 = new Pot(1);
        Pot pot3 = new Pot(2);
        Pot pot4 = new Pot(3);
        
        pot1.bet(player1, 10000L);
        pot1.bet(player2, 10000L);
        pot2.bet(player1, 10000L);
        pot2.bet(player2,  9000L);
        pot3.bet(player1, 10000L);
        pot4.bet(player2, 10000L);
        
        Collection<Pot> pots = Arrays.asList(pot1, pot2, pot3, pot4);

        // no prior rake taken
        Map<Pot, BigDecimal> rakes = rc.calculateRakes(pots);
        
        assertThat(rakes.get(pot1), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 10000)))); // 2000
        assertThat(rakes.get(pot2), is(rakeFraction.multiply(BigDecimal.valueOf(10000 + 9000))));  // 1900
        assertThat(rakes.get(pot3), is(new BigDecimal("100.0"))); // 100 (limited)
        assertThat(rakes.get(pot4), is(new BigDecimal("0.0")));   // 100 (limited)
    }
    
    
    
    
    

}
