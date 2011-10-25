package com.cubeia.poker.variant.telesina;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rounds.betting.BettingRound;


public class TelesinaBettingRoundFinishedTest {
    
    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
    private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    
    private SortedMap<Integer, PokerPlayer> seatingMap;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
        Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(player1.getId(), player1);
        playerMap.put(player2.getId(), player2);
        when(state.getPotHolder()).thenReturn(potHolder);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        
    }
    
    @Test
    public void testSomething()
    {
    	Telesina telesina = new Telesina(new DummyRNGProvider(), state, deckFactory, roundFactory);
    	telesina.startHand();
    	BettingRound bettingRound = mock(BettingRound.class);
    	when(bettingRound.getLastPlayerToBeCalled()).thenReturn(player1);
		telesina.visit(bettingRound);
		verify(state).setLastPlayerToBeCalled(player1);
    }

}
