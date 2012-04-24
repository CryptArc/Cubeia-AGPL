package com.cubeia.poker.rounds.betting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.cubeia.poker.player.PokerPlayer;

public class DefaultPlayerToActCalculatorTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testFirstPlayerToAct() {
        DefaultPlayerToActCalculator pac = new DefaultPlayerToActCalculator();
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        
        
        PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player3));

        playerToAct = pac.getFirstPlayerToAct(2, seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player1));
    }

    @SuppressWarnings("unchecked")
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
        
        PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player3));

        playerToAct = pac.getFirstPlayerToAct(2, seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player1));
    }
    
}
