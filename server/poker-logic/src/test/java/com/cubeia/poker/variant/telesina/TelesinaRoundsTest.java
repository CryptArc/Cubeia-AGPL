package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.TelesinaDeck;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.telesina.TelesinaDeckFactory;
import com.cubeia.poker.variant.telesina.TelesinaRoundFactory;


public class TelesinaRoundsTest {
    
    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
    @Mock private Hand player1Hand;
    @Mock private Hand player2Hand;
    @Mock private Hand player3Hand;
    
    private SortedMap<Integer, PokerPlayer> seatingMap;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        when(player1.getPocketCards()).thenReturn(player1Hand);
        when(player2.getPocketCards()).thenReturn(player2Hand);
        when(player3.getPocketCards()).thenReturn(player3Hand);
        
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(state.getPotHolder()).thenReturn(potHolder);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testRoundSequence() {
        Telesina telesina = new Telesina(state, deckFactory, roundFactory);

        // ante round
        AnteRound anteRound = mock(AnteRound.class);
        when(roundFactory.createAnteRound(telesina)).thenReturn(anteRound);
        telesina.startHand();
        assertThat((AnteRound) telesina.getCurrentRound(), is(anteRound));
        assertThat(telesina.getBettingRoundId(), is(0));
        
        when(anteRound.isFinished()).thenReturn(true);
        telesina.timeout();
        verify(anteRound).timeout();
        verify(anteRound).visit(telesina);
        
        // betting round 1
        BettingRound bettingRound0 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound0);
        telesina.visit(anteRound);
        verify(potHolder).moveChipsToPot(Mockito.anyCollection());
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound0));
        assertThat(telesina.getBettingRoundId(), is(1));
        
        // deal pocket card round 1
        
//        telesina.visit(bettingRound0);
        
        
    }
    
    

}
