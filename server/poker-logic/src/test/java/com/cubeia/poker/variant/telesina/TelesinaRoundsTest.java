package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.TelesinaDeck;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rounds.DealPocketCardsRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;


public class TelesinaRoundsTest {
    
    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private ServerAdapter serverAdapter;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
//    @Mock private Hand player1Hand;
//    @Mock private Hand player2Hand;
//    @Mock private Hand player3Hand;
    private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    private PokerPlayer player3 = new DefaultPokerPlayer(1003);
    
    private SortedMap<Integer, PokerPlayer> seatingMap;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
        Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(player1.getId(), player1);
        playerMap.put(player2.getId(), player2);
        playerMap.put(player3.getId(), player3);
        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
        
        when(state.getTimingProfile()).thenReturn(new DefaultTimingProfile());
        when(state.getServerAdapter()).thenReturn(serverAdapter);
        when(state.getPotHolder()).thenReturn(potHolder);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        when(deck.deal()).thenAnswer(new Answer<Card>() {
            @Override
            public Card answer(InvocationOnMock invocation) throws Throwable {
                return Mockito.mock(Card.class);
            }
        });
    }
    
    /**
     * Run through a game and make sure the round sequence is correct.
     */
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
        
        // betting round 0
        BettingRound bettingRound0 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound0);
        telesina.visit(anteRound);
        verify(potHolder).moveChipsToPot(Mockito.anyCollection());
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound0));
        assertThat(telesina.getBettingRoundId(), is(1));
        assertCardsDealt(2, 1, player1, player2, player3);
        
        // deal pocket card round 0
        DealPocketCardsRound dealPocketCardsRound0 = mock(DealPocketCardsRound.class);
        when(roundFactory.createDealPocketCardsRound(telesina)).thenReturn(dealPocketCardsRound0);
        when(potHolder.getPots()).thenReturn(new ArrayList<Pot>());
        when(state.countNonFoldedPlayers()).thenReturn(3);
        assertThat(telesina.isHandFinished(), is(false));
        telesina.visit(bettingRound0);
        verify(serverAdapter).scheduleTimeout(Mockito.anyLong());
        assertThat((DealPocketCardsRound) telesina.getCurrentRound(), is(dealPocketCardsRound0));
        assertThat(telesina.getBettingRoundId(), is(1));
        assertCardsDealt(2, 1, player1, player2, player3);
        
        // betting round 1
        BettingRound bettingRound1 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound1);
        telesina.visit(dealPocketCardsRound0);
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound1));
        assertThat(telesina.getBettingRoundId(), is(2));
        assertCardsDealt(3, 2, player1, player2, player3);
        
        // deal pocket card after round 1
        DealPocketCardsRound dealPocketCardsRound1 = mock(DealPocketCardsRound.class);
        when(roundFactory.createDealPocketCardsRound(telesina)).thenReturn(dealPocketCardsRound1);
        assertThat(telesina.isHandFinished(), is(false));
        telesina.visit(bettingRound1);
        verify(serverAdapter, Mockito.times(2)).scheduleTimeout(Mockito.anyLong());
        assertThat((DealPocketCardsRound) telesina.getCurrentRound(), is(dealPocketCardsRound1));
        assertThat(telesina.getBettingRoundId(), is(2));
        assertCardsDealt(3, 2, player1, player2, player3);
        
        // betting round 2
        BettingRound bettingRound2 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound2);
        telesina.visit(dealPocketCardsRound1);
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound2));
        assertThat(telesina.getBettingRoundId(), is(3));
        assertCardsDealt(4, 3, player1, player2, player3);
        
        // deal pocket card after round 2
        DealPocketCardsRound dealPocketCardsRound2 = mock(DealPocketCardsRound.class);
        when(roundFactory.createDealPocketCardsRound(telesina)).thenReturn(dealPocketCardsRound2);
        assertThat(telesina.isHandFinished(), is(false));
        telesina.visit(bettingRound2);
        verify(serverAdapter, Mockito.times(3)).scheduleTimeout(Mockito.anyLong());
        assertThat((DealPocketCardsRound) telesina.getCurrentRound(), is(dealPocketCardsRound2));
        assertThat(telesina.getBettingRoundId(), is(3));
        assertCardsDealt(4, 3, player1, player2, player3);
        
        // betting round 3
        BettingRound bettingRound3 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound3);
        telesina.visit(dealPocketCardsRound2);
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound3));
        assertThat(telesina.getBettingRoundId(), is(4));
        assertCardsDealt(5, 4, player1, player2, player3);
        
        // deal pocket card after round 3
        DealPocketCardsRound dealPocketCardsRound3 = mock(DealPocketCardsRound.class);
        when(roundFactory.createDealPocketCardsRound(telesina)).thenReturn(dealPocketCardsRound3);
        assertThat(telesina.isHandFinished(), is(false));
        telesina.visit(bettingRound3);
        verify(serverAdapter, Mockito.times(4)).scheduleTimeout(Mockito.anyLong());
        assertThat((DealPocketCardsRound) telesina.getCurrentRound(), is(dealPocketCardsRound3));
        assertThat(telesina.getBettingRoundId(), is(4));
        assertCardsDealt(5, 4, player1, player2, player3);
        
        // TODO: vela round!
        // ...
    }

    private void assertCardsDealt(int totalCards, int exposedCards, PokerPlayer... pokerPlayers) {
        for (PokerPlayer player : pokerPlayers) {
            assertThat(player.getPocketCards().getCards().size(), is(totalCards));
            assertThat(player.getPublicPocketCards().size(), is(exposedCards));
        }
    }
    
    

}
