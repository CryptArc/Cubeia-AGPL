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
import com.cubeia.poker.pot.PotTransition;

public class HandResultRakeContributionTest {

    @Test
    public void testGetRakeContributionByPlayer() {
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        // Player 1: pot 0 = 500
        // Player 2: pot 0 = 500, pot 1 = 1000
        // Player 3: pot 0 = 500, pot 1 = 1000
        // Total bets: 3500, total rake: 350
        Pot pot0 = new Pot(0);
        Pot pot1 = new Pot(1);
        pot0.bet(player1, 500L);
        pot0.addRake(BigDecimal.valueOf(50));
        pot0.bet(player2, 500L);
        pot0.addRake(BigDecimal.valueOf(50));
        pot0.bet(player3, 500L);
        pot0.addRake(BigDecimal.valueOf(50));

        pot1.bet(player2, 1000L);
        pot1.addRake(BigDecimal.valueOf(100));
        pot1.bet(player3, 1000L);
        pot1.addRake(BigDecimal.valueOf(100));
        
        // this is just a dummy to get all pots into the results
        Map<Pot, Long> winningsByPotDummy = new HashMap<Pot, Long>();
        winningsByPotDummy.put(pot0, null);
        winningsByPotDummy.put(pot1, null);
        
        Result result1 = new Result(0,  500, winningsByPotDummy);
        Result result2 = new Result(0, 1500, winningsByPotDummy);
        Result result3 = new Result(0, 1500, winningsByPotDummy);
        
        results.put(player1 , result1);
        results.put(player2 , result2);
        results.put(player3 , result3);
        
        HandResult result = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList());
        
        assertThat(result.getRakeContributionByPlayer(player1), is(50L));
        assertThat(result.getRakeContributionByPlayer(player2), is(150L));
        assertThat(result.getRakeContributionByPlayer(player3), is(150L));
    }

    /*
    @Test
    public void testRakeContributionRoundingsGoesToLastPlayer() {
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        // Player 1: pot 0 = 500
        // Player 2: pot 0 = 500, pot 1 = 1000
        // Player 3: pot 0 = 500, pot 1 = 1000
        // Total bets: 33, total rake: 3
        Pot pot0 = new Pot(0);
        Pot pot1 = new Pot(1);
        pot0.bet(player1, 11L);
        pot0.bet(player2, 11L);
        pot0.bet(player3, 11L);
        pot0.addRake(3);
        
//        pot1.bet(player2, 1000L);
//        pot1.addRake(100);
//        pot1.bet(player3, 1000L);
//        pot1.addRake(100);
        
        // this is just a dummy to get all pots into the results
        Map<Pot, Long> winningsByPotDummy = new HashMap<Pot, Long>();
        winningsByPotDummy.put(pot0, null);
        winningsByPotDummy.put(pot1, null);
        
        Result result1 = new Result(0, 100, winningsByPotDummy);
        Result result2 = new Result(0, 140, winningsByPotDummy);
        Result result3 = new Result(0, 140, winningsByPotDummy);
        
        results.put(player1 , result1);
        results.put(player2 , result2);
        results.put(player3 , result3);
        
        HandResult result = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList());
        
        assertThat(result.getRakeContributionByPlayer(player1), is(50L));
        assertThat(result.getRakeContributionByPlayer(player2), is(150L));
        assertThat(result.getRakeContributionByPlayer(player3), is(150L));
    }
    */
    
}
