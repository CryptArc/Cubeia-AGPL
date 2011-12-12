package com.cubeia.poker.pot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rake.RakeCalculator;

public class PotHolderTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testRakeCalculationGetsCallFlag() {
        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        PotHolder potHolder = new PotHolder(rakeCalculator);
        
        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(false));
        
        potHolder.call();
        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(true));
    }
    
    
    @Test
    public void calculateRakeIncludingBetStacks() 
    {
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
