package com.cubeia.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mock;

import com.cubeia.poker.player.PokerPlayer;

public class MuckingPlayersTest {

    
    private PokerState state;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
    
    
    @Before
    public void setup() {
        initMocks(this);
        
        state = new PokerState();
        
        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(1001, player1);
        playerMap.put(1002, player2);
        playerMap.put(1003, player3);
        
        state.currentHandPlayerMap = playerMap;
    }
    
    
    @Test
    public void testGetMuckingPlayersNormal() {
        when(player1.hasFolded()).thenReturn(false);
        when(player2.hasFolded()).thenReturn(false);
        when(player3.hasFolded()).thenReturn(false);
        
        Set<PokerPlayer> muckingPlayers = state.getMuckingPlayers();
        assertThat(muckingPlayers.isEmpty(), is(true));
    }
    
    @Test
    public void testGetMuckingPlayersWhenAllButOneFolded() {
        when(player1.hasFolded()).thenReturn(false);
        when(player2.hasFolded()).thenReturn(true);
        when(player3.hasFolded()).thenReturn(true);
        
        Set<PokerPlayer> muckingPlayers = state.getMuckingPlayers();
        assertThat(muckingPlayers.size(), is(3));
        assertThat(muckingPlayers, JUnitMatchers.hasItems(player1, player2, player3));
    }
    
    @Test
    public void testFoldedPlayerMucks() {
        when(player1.hasFolded()).thenReturn(false);
        when(player2.hasFolded()).thenReturn(true);
        when(player3.hasFolded()).thenReturn(false);
        
        Set<PokerPlayer> muckingPlayers = state.getMuckingPlayers();
        assertThat(muckingPlayers.size(), is(1));
        assertThat(muckingPlayers, JUnitMatchers.hasItems(player2));
    }
    
}
