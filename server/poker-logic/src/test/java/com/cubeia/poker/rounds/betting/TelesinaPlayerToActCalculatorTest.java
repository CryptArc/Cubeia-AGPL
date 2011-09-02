package com.cubeia.poker.rounds.betting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.hand.PokerEvaluator;
import com.cubeia.poker.model.PlayerHand;
import com.cubeia.poker.player.PokerPlayer;

public class TelesinaPlayerToActCalculatorTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testFirstPlayerToActNotInVelaRound() {
        PokerEvaluator pokerEvaluator = Mockito.mock(PokerEvaluator.class);
        
        TelesinaPlayerToActCalculator pac = new TelesinaPlayerToActCalculator(pokerEvaluator);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(0);
        when(player2.getId()).thenReturn(1337);
        when(player3.getId()).thenReturn(0);
        
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);  // best hand
        seatingMap.put(2, player3);
        
        when(pokerEvaluator.rankHands(Mockito.anyCollection())).thenReturn(Arrays.asList(new PlayerHand(1337, null)));
        
        PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap);
        assertThat(playerToAct, is(player2));

        playerToAct = pac.getFirstPlayerToAct(2, seatingMap);
        assertThat(playerToAct, is(player2));
        
        playerToAct = pac.getFirstPlayerToAct(0, seatingMap);
        assertThat(playerToAct, is(player2));
    }
    
    @Test
    @Ignore
    public void testFirstPlayerToActVelaRound() {
        fail("implement me!");
    }    

    @Test
    public void testNextPlayerToAct() {
        DefaultPlayerToActCalculator pac = new DefaultPlayerToActCalculator();
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        
        PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap);
        assertThat(playerToAct, is(player3));

        playerToAct = pac.getFirstPlayerToAct(2, seatingMap);
        assertThat(playerToAct, is(player1));
    }
    
}
