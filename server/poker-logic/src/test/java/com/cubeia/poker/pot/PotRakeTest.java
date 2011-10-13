package com.cubeia.poker.pot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rake.RakeCalculator;
import com.cubeia.poker.rake.RakeCalculatorImpl;

public class PotRakeTest {

    
    @Test
    public void testMoveChipsToPotWithRake() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player1.isAllIn()).thenReturn(false);
        when(player2.isAllIn()).thenReturn(false);
        
        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        
        PotHolder ph = new PotHolder(new RakeCalculatorImpl(BigDecimal.ZERO));
        
        Collection<PotTransition> transitions = ph.moveChipsToPot(Arrays.asList(player1, player2));
        
        
        
        
        
    }
    
}
