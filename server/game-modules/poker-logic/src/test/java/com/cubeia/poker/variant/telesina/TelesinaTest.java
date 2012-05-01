package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.MockServerAdapter;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.DealExposedPocketCardsRound;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelesinaTest {

    @Mock
    private PokerState pokerState;

    private SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();

    PokerPlayer dealer;
    @Mock
    TelesinaDeckFactory deckFactory;
    @Mock
    TelesinaDeck deck;
    @Mock
    private TelesinaDealerButtonCalculator dealerButtonCalculator;
    TelesinaForTesting telesina;

    private void setup() {
        MockitoAnnotations.initMocks(this);
        TelesinaRoundFactory roundFactory = mock(TelesinaRoundFactory.class);

        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        when(deck.deal()).thenReturn(
                new Card(1, "2H"), new Card(2, "3H"), new Card(3, "4H"), new Card(4, "5H"), new Card(5, "6H"), new Card(6, "7H"),
                new Card(7, "8H"), new Card(7, "9H"), new Card(7, "JH"), new Card(7, "QH"), new Card(7, "KH"), new Card(7, "AH"),
                new Card(1, "2D"), new Card(2, "3D"), new Card(3, "4D"), new Card(4, "5D"), new Card(5, "6D"), new Card(6, "7D"),
                new Card(7, "8D"), new Card(7, "9D"), new Card(7, "JD"), new Card(7, "QD"), new Card(7, "KD"), new Card(7, "AD"));
        telesina = new TelesinaForTesting(new DummyRNGProvider(), pokerState, deckFactory, roundFactory, dealerButtonCalculator);
    }

    @SuppressWarnings("unused")
    @Test
    public void testSendAllNonFoldedPlayersBestHand() {
        setup();

        createAndAddPlayer(1, true);
        createAndAddPlayer(2, false);
        createAndAddPlayer(3, false);

        when(pokerState.getCurrentHandSeatingMap()).thenReturn(playerMap);
        when(pokerState.getTimingProfile()).thenReturn(new DefaultTimingProfile());
        when(pokerState.getServerAdapter()).thenReturn(new MockServerAdapter());

        telesina.startHand();

        DealExposedPocketCardsRound dealPocketCardsRound = new DealExposedPocketCardsRound(telesina);

        //two times when dealing new cards
        int numberOfTimeHandStrengthShouldBeSent = 2;
        assertEquals(numberOfTimeHandStrengthShouldBeSent, telesina.getNumberOfSentBestHands());
    }

    private DefaultPokerPlayer createAndAddPlayer(int playerId, boolean folded) {
        DefaultPokerPlayer p = new DefaultPokerPlayer(playerId);
        p.setHasFolded(folded);
        playerMap.put(playerId, p);
        return p;
    }
}
