package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.BlindsInfo;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.ante.AnteRound;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TelesinaStartHandTest {

    @Mock
    private PokerContext context;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private BlindsInfo blindsInfo;

    @Mock
    private TelesinaDeckFactory deckFactory;

    @Mock
    private RNGProvider rngProvider;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private Random rng;

    @Mock
    private TelesinaDeck deck;

    @Mock
    private TelesinaRoundFactory roundFactory;

    @Mock
    private AnteRound anteRound;

    @Mock
    private TelesinaDealerButtonCalculator dealerButtonCalculator;

    private Telesina telesina;

    @Before
    public void init() {
        initMocks(this);

        when(rngProvider.getRNG()).thenReturn(rng);
        when(context.getTableSize()).thenReturn(4);
        when(context.getAnteLevel()).thenReturn(1000);
        when(deck.getTotalNumberOfCardsInDeck()).thenReturn(40);
        when(deck.getDeckLowestRank()).thenReturn(Rank.FIVE);
        when(deckFactory.createNewDeck(rng, 4)).thenReturn(deck);
        when(roundFactory.createAnteRound(context, serverAdapterHolder)).thenReturn(anteRound);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(context.getBlindsInfo()).thenReturn(blindsInfo);

        telesina = new Telesina(rngProvider, deckFactory, roundFactory, dealerButtonCalculator);
        telesina.setPokerContextAndServerAdapter(context, serverAdapterHolder);
    }

    @Test
    public void testStartHand() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getPlayersInHand()).thenReturn(seatingMap.values());

        telesina.startHand();

        assertThat(telesina.getCurrentRound(), is((Round) anteRound));
        assertThat(telesina.getBettingRoundId(), is(0));
        verify(deckFactory).createNewDeck(rng, 4);
        verify(serverAdapter).notifyDeckInfo(40, Rank.FIVE);
        verify(blindsInfo).setAnteLevel(1000);
    }

    @Test
    public void testThatNewDeckIsCreatedOnStartHand() {
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);

        context.setBlindsInfo(blindsInfo);

        telesina.startHand();
        verify(deckFactory, times(1)).createNewDeck(rng, 4);

        telesina.startHand();
        verify(deckFactory, times(2)).createNewDeck(rng, 4);

        telesina.startHand();
        verify(deckFactory, times(3)).createNewDeck(rng, 4);
    }
}
