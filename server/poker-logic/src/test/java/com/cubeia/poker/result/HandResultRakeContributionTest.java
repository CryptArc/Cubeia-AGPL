package com.cubeia.poker.result;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
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
        HandResult result = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), 
            Collections.<PotTransition>emptyList(), rakeInfoContainer);
        
        assertThat(result.getRakeContributionByPlayer(player1), is(50L));
        assertThat(result.getRakeContributionByPlayer(player2), is(150L));
        assertThat(result.getRakeContributionByPlayer(player3), is(150L));
    }

    
}
