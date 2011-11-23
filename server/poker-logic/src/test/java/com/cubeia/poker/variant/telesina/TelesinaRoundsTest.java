package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rounds.DealCommunityCardsRound;
import com.cubeia.poker.rounds.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;


public class TelesinaRoundsTest {
    
    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private ServerAdapter serverAdapter;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
    @Mock private TelesinaDealerButtonCalculator dealerButtonCalculator;
    
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
        
        // just return enough cards to make tests happy...
        when(deck.deal()).thenReturn(
            new Card(1, "2H"), new Card(2, "3H"), new Card(3, "4H"), new Card(4, "5H"), new Card(5, "6H"), new Card(6, "7H"), 
            new Card(7, "8H"), new Card(7, "9H"), new Card(7, "JH"), new Card(7, "QH"), new Card(7, "KH"), new Card(7, "AH"),
            new Card(1, "2D"), new Card(2, "3D"), new Card(3, "4D"), new Card(4, "5D"), new Card(5, "6D"), new Card(6, "7D"), 
            new Card(7, "8D"), new Card(7, "9D"), new Card(7, "JD"), new Card(7, "QD"), new Card(7, "KD"), new Card(7, "AD"));
    }
    
    /**
     * Run through a game and make sure the round sequence is correct.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRoundSequence() {
        Telesina telesina = new Telesina(new DummyRNGProvider(), state, deckFactory, roundFactory,dealerButtonCalculator);


        AnteRound anteRound = mock(AnteRound.class);
        when(roundFactory.createAnteRound(telesina)).thenReturn(anteRound);
        // verytime whe run createDealExposedPocketCardsRound() we will create a new DealExposedPocketCardsRound witch in turn will deal cards
        when(roundFactory.createDealExposedPocketCardsRound(telesina)).thenAnswer(new Answer<DealExposedPocketCardsRound>() {
	          public DealExposedPocketCardsRound answer(InvocationOnMock invocation) throws Throwable {
	                  return new DealExposedPocketCardsRound((Telesina)invocation.getArguments()[0]);
	              }
	          });
        
        
        

        // ante -> deal initial cards round
        // verytime whe run createDealExposedPocketCardsRound() we will create a new DealExposedPocketCardsRound witch in turn will deal cards
        when(roundFactory.createDealInitialCardsRound(telesina)).thenAnswer(new Answer<DealInitialPocketCardsRound>() {
	          public DealInitialPocketCardsRound answer(InvocationOnMock invocation) throws Throwable {
	                  return new DealInitialPocketCardsRound((Telesina)invocation.getArguments()[0]);
	              }
	          });
        
        BlindsInfo blindsInfo = mock(BlindsInfo.class);
		when(state.getBlindsInfo()).thenReturn(blindsInfo);
        
        // ante round
        telesina.startHand();
        assertThat((AnteRound) telesina.getCurrentRound(), is(anteRound));
        assertThat(telesina.getBettingRoundId(), is(0));
        when(anteRound.isFinished()).thenReturn(true);
        telesina.timeout();
        verify(anteRound).timeout();
        verify(anteRound).visit(telesina);
        telesina.visit(anteRound); // jump to deal initial pocket cards round
        int newDealerButtonSeatId = 0;
		verify(blindsInfo).setDealerButtonSeatId(newDealerButtonSeatId );
        verify(state).notifyDealerButton(newDealerButtonSeatId);
                
        // deal initial cards round -> betting round 0
        BettingRound bettingRound0 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound0);
        telesina.visit((DealInitialPocketCardsRound)telesina.getCurrentRound()); // Jump to betting round
        verify(serverAdapter).scheduleTimeout(Mockito.anyLong());
        when(potHolder.getPots()).thenReturn(new ArrayList<Pot>());
        when(state.countNonFoldedPlayers()).thenReturn(3);
        assertThat(telesina.isHandFinished(), is(false));
        assertCardsDealt(2, 1, player1, player2, player3);
        verify(potHolder).moveChipsToPot(Mockito.anyCollection());
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound0));
        assertThat(telesina.getBettingRoundId(), is(1));
        
		// betting 0 -> deal exposed		
        telesina.visit(bettingRound0); // bettinground is over. deal new cards
        assertCardsDealt(3, 2, player1, player2, player3);
        verify(serverAdapter,Mockito.times(2)).scheduleTimeout(Mockito.anyLong());
        assertThat(telesina.getCurrentRound(), CoreMatchers.instanceOf(DealExposedPocketCardsRound.class));
        assertThat(telesina.getBettingRoundId(), is(1));
        BettingRound bettingRound1 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound1);

        // deal pocket cards -> betting round 1
        telesina.visit((DealExposedPocketCardsRound)telesina.getCurrentRound()); // Jump to betting round
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound1));
        assertThat(telesina.getBettingRoundId(), is(2));
        assertThat(telesina.isHandFinished(), is(false));

        // betting 1 -> deal pocket cards round
        telesina.visit(bettingRound1); // jump to dealExposedCardsRound
        verify(serverAdapter, Mockito.times(3)).scheduleTimeout(Mockito.anyLong());
        assertCardsDealt(4, 3, player1, player2, player3);        
        assertThat(telesina.getCurrentRound(), CoreMatchers.instanceOf(DealExposedPocketCardsRound.class));
        assertThat(telesina.getBettingRoundId(), is(2));
        BettingRound bettingRound2 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound2); 

        // deal pocket cards -> betting round 2
        telesina.visit((DealExposedPocketCardsRound)telesina.getCurrentRound()); // Jump to betting round
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound2));
        assertThat(telesina.getBettingRoundId(), is(3));
        assertThat(telesina.isHandFinished(), is(false));
                
        // betting 2 -> deal pocket card
        telesina.visit(bettingRound2); // jump to  dealExposedCardsRound
        assertCardsDealt(5, 4, player1, player2, player3);
        verify(serverAdapter, Mockito.times(4)).scheduleTimeout(Mockito.anyLong());
        assertThat(telesina.getCurrentRound(), CoreMatchers.instanceOf(DealExposedPocketCardsRound.class));
        assertThat(telesina.getBettingRoundId(), is(3));
        BettingRound bettingRound3 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound3);
                
        // deal pocket cards -> betting round 3
        telesina.visit((DealExposedPocketCardsRound)telesina.getCurrentRound()); // Jump to betting round
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound3));
        assertThat(telesina.getBettingRoundId(), is(4));
        assertCardsDealt(5, 4, player1, player2, player3);
        DealCommunityCardsRound dealVelaCardRound = mock(DealCommunityCardsRound.class);
        when(roundFactory.createDealCommunityCardsRound(telesina)).thenReturn(dealVelaCardRound);
        assertThat(telesina.isHandFinished(), is(false));
        
        // betting 3 -> deal vela card
        telesina.visit(bettingRound3);
        verify(serverAdapter, Mockito.times(5)).scheduleTimeout(Mockito.anyLong());
        assertThat((DealCommunityCardsRound) telesina.getCurrentRound(), is(dealVelaCardRound));
        assertThat(telesina.getBettingRoundId(), is(4));
        assertCardsDealt(5, 4, player1, player2, player3);
        
        // deal vela card -> betting round 4
        telesina.visit(dealVelaCardRound); // cards: 1 + 4 + vela
        assertThat((BettingRound) telesina.getCurrentRound(), is(bettingRound3));
        assertThat(telesina.getBettingRoundId(), is(5));
        assertCardsDealt(5, 4, player1, player2, player3);
        BettingRound bettingRound4 = mock(BettingRound.class);
        when(roundFactory.createBettingRound(telesina, 0)).thenReturn(bettingRound4);
        assertThat(telesina.isHandFinished(), is(true));
        
        // check how many calls to the state "notify new round" we've had
        verify(state, times(5)).notifyNewRound();
        
        // SHOWDOWN!
    }

    private void assertCardsDealt(int totalCards, int exposedCards, PokerPlayer... pokerPlayers) {
        for (PokerPlayer player : pokerPlayers) {
            assertThat(player.getPocketCards().getCards().size(), is(totalCards));
            assertThat(player.getPublicPocketCards().size(), is(exposedCards));
        }
    }

}
