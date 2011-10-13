package com.cubeia.poker.rake;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

public class RakeCalculatorImplTest {

    @Test
    public void testCalculateRakeNoLimit() {
        RakeCalculatorImpl rc = new RakeCalculatorImpl(new BigDecimal("0.1"));
        
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
        Map<Pot, Integer> rakes = rc.calculateRakes(20000, potTransitions);
        
        assertThat(rakes.get(pot1), is((int) ((10000 + 10000) * 0.1)));
        assertThat(rakes.get(pot2), is((int) ((3000 + 2000) * 0.1)));
        assertThat(rakes.get(pot3), is((int) ((1000) * 0.1)));
    }
    
    @Test
    public void testCalculateRakeWithLimit() {
        RakeCalculatorImpl rc = new RakeCalculatorImpl(new BigDecimal("0.1"), 4000);
        
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
        Map<Pot, Integer> rakes = rc.calculateRakes(0, potTransitions);
        assertThat(rakes.get(pot1), is((int) ((10000 + 10000) * 0.1))); // 2000
        assertThat(rakes.get(pot2), is((int) ((10000 +  9000) * 0.1))); // 1900
        assertThat(rakes.get(pot3), is(100));                           // 100 (limited)
        
        // already took some rake
        rakes = rc.calculateRakes(3500, potTransitions);
        assertThat(rakes.get(pot1), is(500)); 
        assertThat(rakes.get(pot2), is(0)); 
        assertThat(rakes.get(pot3), is(0)); 
    }

}
