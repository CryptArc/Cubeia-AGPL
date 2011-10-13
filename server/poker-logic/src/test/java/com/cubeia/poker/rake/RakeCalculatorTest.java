package com.cubeia.poker.rake;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;

public class RakeCalculatorTest {

    @Test
    public void testCalculateRake() {
        BigDecimal rakeFraction = new BigDecimal("0.01");
        RakeCalculatorImpl rc = new RakeCalculatorImpl(rakeFraction);
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(1337);
        when(player2.getId()).thenReturn(1338);
        when(player3.getId()).thenReturn(1339);
        
        Pot pot0 = new Pot(0);
        Pot pot1 = new Pot(0);
        Collection<Pot> pots = Arrays.asList(pot0, pot1);
        
        pot0.bet(player1, 1000L);
        pot0.bet(player2, 1000L);
        pot0.bet(player3, 1000L);

        pot1.bet(player2, 300L);
        pot1.bet(player3, 300L);
        
//        RakeInfoContainer rakeInfo = rc.calculateRake(pots);
//        
//        assertThat(rakeInfo.getTotalPot(), is(3600));
//        assertThat(rakeInfo.getTotalRake(), is(3600 / 100));
    }

}
