package com.cubeia.poker.rounds.betting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.collect.Sets;

public class DefaultPlayerToActCalculatorTest {

    @Test
    public void testFirstPlayerToAct() {
        TelesinaPlayerToActCalculator pac = new TelesinaPlayerToActCalculator();
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        
//        when(player1.getPublicPocketCards()).thenReturn(new HashSet<Card>(Sets.newHashSet(elements))):
        
        
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        
        PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap);
        assertThat(playerToAct, is(player3));

        playerToAct = pac.getFirstPlayerToAct(2, seatingMap);
        assertThat(playerToAct, is(player1));
    }

    @Test
    public void testNextPlayerToAct() {
        TelesinaPlayerToActCalculator pac = new TelesinaPlayerToActCalculator();
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
