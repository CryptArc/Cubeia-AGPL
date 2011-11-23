package com.cubeia.poker.variant.telesina;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;

public class TelesinaSendBestHandTest {

    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private ServerAdapter serverAdapter;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
    @Mock private TelesinaDealerButtonCalculator dealerButtonCalculator;
    private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    
    private SortedMap<Integer, PokerPlayer> seatingMap;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
        Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(player1.getId(), player1);
        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
        
        when(state.getTimingProfile()).thenReturn(new DefaultTimingProfile());
        when(state.getServerAdapter()).thenReturn(serverAdapter);
        when(state.getPotHolder()).thenReturn(potHolder);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
    }
    
    @Test
    public void testCalculateAndSendBestHandToPlayer() {
        Telesina telesina = new Telesina(new DummyRNGProvider(), state, deckFactory, roundFactory, dealerButtonCalculator);
        
        TelesinaHandStrengthEvaluator evaluator = Mockito.mock(TelesinaHandStrengthEvaluator.class);
        Hand hand = mock(Hand.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(player.getPocketCards()).thenReturn(hand);
        when(player.isExposingPocketCards()).thenReturn(false);
        Card pocketCard1 = new Card("AS");
        Card pocketCard2 = new Card("5C");
        when(hand.getCards()).thenReturn(asList(pocketCard1, pocketCard2));
        Card velaCard = new Card("2H");
        when(state.getCommunityCards()).thenReturn(asList(velaCard));
        HandStrength handStrength = mock(HandStrength.class);
        when(handStrength.getCards()).thenReturn(Arrays.asList(pocketCard1));
        when(handStrength.getHandType()).thenReturn(HandType.FOUR_OF_A_KIND);
        
        when(evaluator.getBestHandStrength(Mockito.any(Hand.class))).thenReturn(handStrength);
        
        telesina.calculateAndSendBestHandToPlayer(evaluator, player);
        verify(serverAdapter).notifyBestHand(player.getId(), HandType.FOUR_OF_A_KIND, asList(pocketCard1), false);
    }
    
    @Test
    public void testCalculateAndSendBestHandToPlayersWhenExposingHand() {
        Telesina telesina = new Telesina(new DummyRNGProvider(), state, deckFactory, roundFactory,dealerButtonCalculator);
        
        TelesinaHandStrengthEvaluator evaluator = Mockito.mock(TelesinaHandStrengthEvaluator.class);
        Hand hand = mock(Hand.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(player.getPocketCards()).thenReturn(hand);
        when(player.isExposingPocketCards()).thenReturn(true);
        Card pocketCard1 = new Card("AS");
        Card pocketCard2 = new Card("5C");
        when(hand.getCards()).thenReturn(asList(pocketCard1, pocketCard2));
        Card velaCard = new Card("2H");
        when(state.getCommunityCards()).thenReturn(asList(velaCard));
        HandStrength handStrength = mock(HandStrength.class);
        when(handStrength.getCards()).thenReturn(Arrays.asList(pocketCard1));
        when(handStrength.getHandType()).thenReturn(HandType.FOUR_OF_A_KIND);
        
        when(evaluator.getBestHandStrength(Mockito.any(Hand.class))).thenReturn(handStrength);
        
        telesina.calculateAndSendBestHandToPlayer(evaluator, player);
        verify(serverAdapter).notifyBestHand(player.getId(), HandType.FOUR_OF_A_KIND, asList(pocketCard1), true);
        
    }
    
    
    @Ignore
    @Test
    public void testDealExposedCards() {
        Telesina telesina = new Telesina(new DummyRNGProvider(), state, deckFactory, roundFactory,dealerButtonCalculator);
        
        telesina.dealExposedPocketCards();
        
        
    }
    
    @Ignore
    @Test
    public void testDealCommunityCards() {
        fail("Not yet implemented");
    }
    
}
