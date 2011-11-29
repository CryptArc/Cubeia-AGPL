package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.blinds.BlindsInfo;

public class TelesinaStartHandTest {
    
    @Test
    public void testStartHand() {
        RNGProvider rngProvider = mock(RNGProvider.class);
        Random rng = mock(Random.class);
        when(rngProvider.getRNG()).thenReturn(rng);
        PokerState state = mock(PokerState.class);
        when(state.getTableSize()).thenReturn(4);
        when(state.getAnteLevel()).thenReturn(1000);
        TelesinaDeckFactory deckFactory = mock(TelesinaDeckFactory.class);
        TelesinaDeck deck = mock(TelesinaDeck.class);
        when(deck.getTotalNumberOfCardsInDeck()).thenReturn(40);
        when(deck.getDeckLowestRank()).thenReturn(Rank.FIVE);
        when(deckFactory.createNewDeck(rng, 4)).thenReturn(deck);
        TelesinaRoundFactory roundFactory = mock(TelesinaRoundFactory.class);
        AnteRound anteRound = mock(AnteRound.class);
        when(roundFactory.createAnteRound(Mockito.any(Telesina.class))).thenReturn(anteRound);
        TelesinaDealerButtonCalculator dealerButtonCalculator = mock(TelesinaDealerButtonCalculator.class);
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
		Telesina telesina = new Telesina(rngProvider, state, deckFactory, roundFactory, dealerButtonCalculator );
        telesina.blindsInfo = mock(BlindsInfo.class);
        
        telesina.startHand();

        assertThat(telesina.getCurrentRound(), is((Round) anteRound));
        assertThat(telesina.getBettingRoundId(), is(0));
        verify(deckFactory).createNewDeck(rng, 4);
        verify(state).notifyDeckInfo(40, Rank.FIVE);
        verify(telesina.blindsInfo).setAnteLevel(1000);
//        verify(player1).setHasActed(false);
//        verify(player2).setHasActed(false);
//        verify(player1).setHasFolded(false);
//        verify(player2).setHasFolded(false);
    }

}
