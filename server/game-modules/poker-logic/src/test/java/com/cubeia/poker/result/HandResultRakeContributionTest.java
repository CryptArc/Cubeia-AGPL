package com.cubeia.poker.result;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;

public class HandResultRakeContributionTest {

    @Test
    public void testGetRakeContributionByPlayer() {
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        Result result1 = new Result(0,  500, new HashMap<Pot, Long>());
        Result result2 = new Result(0, 1500, new HashMap<Pot, Long>());
        Result result3 = new Result(0, 1500, new HashMap<Pot, Long>());
        
        results.put(player1 , result1);
        results.put(player2 , result2);
        results.put(player3 , result3);
        
        int totalPot = 500 * 3 + 1000 * 2;
        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(totalPot, totalPot / 10, null);
        HandResult result = new HandResult(results, Collections.<RatedPlayerHand>emptyList(),Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());
        
        assertThat(result.getRakeContributionByPlayer(player1), is(50L));
        assertThat(result.getRakeContributionByPlayer(player2), is(150L));
        assertThat(result.getRakeContributionByPlayer(player3), is(150L));
    }
    
    @Test
    public void testGetRakeContributionDecimalOverflow() {
    	
    	// here we should test when players own contribution is exactly a third of the total pot
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        Result result1 = new Result(0, 2, new HashMap<Pot, Long>()); // ante
        Result result2 = new Result(0, 2+52, new HashMap<Pot, Long>()); // ante + bet
        Result result3 = new Result(0, 2+52, new HashMap<Pot, Long>()); // ante + call
        
        results.put(player1 , result1);
        results.put(player2 , result2);
        results.put(player3 , result3);
        
        int totalPot = 110;
        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(totalPot, (int)(totalPot * 0.05), null); // 5% rake
        new HandResult(results, Collections.<RatedPlayerHand>emptyList(),Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());
        
    }
    
    @Test
    public void testGetRakeContributionWhenRakeIsTiny() {
    	
    	// here we should test when players own contribution is exactly a third of the total pot
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        Result result1 = new Result(0, 20, new HashMap<Pot, Long>()); // ante
        Result result2 = new Result(0, 20+520, new HashMap<Pot, Long>()); // ante + bet
        Result result3 = new Result(0, 20+520, new HashMap<Pot, Long>()); // ante + call
        
        results.put(player1 , result1);
        results.put(player2 , result2);
        results.put(player3 , result3);
        
        int totalPot = 1100;
        long totalRake = (long)(totalPot * 0.05);
		RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(totalPot, (int)totalRake, null); // 5% rake
        HandResult result = new HandResult(results, Collections.<RatedPlayerHand>emptyList(),Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());
        
        long p1Rake = result.getRakeContributionByPlayer(player1);
        long p2Rake = result.getRakeContributionByPlayer(player2);
        long p3Rake = result.getRakeContributionByPlayer(player3);
        
        // check that its 
        assertThat(p1Rake+p2Rake+p3Rake, is(totalRake));
       
        // check that the rakes is about right for each person
        // this is a bit random so we can not see the absolute exact value for each person since
        // it rounds the rake and someone will have to take the rounding error 
        assertThat(p1Rake, new InRangeMatcher(1L,2L) );
        assertThat(p2Rake, new InRangeMatcher(27L,28L) );
        assertThat(p3Rake, new InRangeMatcher(27L,28L) );

        
    }
    
	public class InRangeMatcher extends BaseMatcher<Long>{

		
		private final Long min;
		private final Long max;

		public InRangeMatcher(Long min, Long max) {
			this.min = min;
			this.max = max;
		}
		
		@Override
		public boolean matches(Object item) {
			Long l = (Long)item;
			
			return (l >=min && l <= max);
			
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("value between " + min + " inclusive and " + max + " inclusive");			
		}

		
	}

    
}


