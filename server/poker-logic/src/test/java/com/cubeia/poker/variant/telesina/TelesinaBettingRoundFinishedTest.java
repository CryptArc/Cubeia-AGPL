package com.cubeia.poker.variant.telesina;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.rounds.betting.BettingRound;


public class TelesinaBettingRoundFinishedTest {
    
    @Mock private PokerState state;
    @Mock private PotHolder potHolder;
    @Mock private TelesinaDeckFactory deckFactory;
    @Mock private TelesinaDeck deck;
    @Mock private TelesinaRoundFactory roundFactory;
    @Mock private TelesinaDealerButtonCalculator dealerButtonCalculator;
    private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    private TelesinaForTesting telesina;
    
    private SortedMap<Integer, PokerPlayer> seatingMap;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);

        when(state.getPotHolder()).thenReturn(potHolder);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        telesina = new TelesinaForTesting(new DummyRNGProvider(), state, deckFactory, roundFactory,dealerButtonCalculator);
    }
    
    @Test
    public void testLastPlayerToBeCalledIsSet() {
    	telesina.startHand();
    	BettingRound bettingRound = mock(BettingRound.class);
    	when(bettingRound.getLastPlayerToBeCalled()).thenReturn(player1);
		telesina.visit(bettingRound);
		verify(state).setLastPlayerToBeCalled(player1);
    }
    
    @Test
    public void testPerformPendingBuyInsWhenRoundFinished() {
        telesina.startHand();
        BettingRound bettingRound = mock(BettingRound.class);
        telesina.visit(bettingRound);
        verify(state).performPendingBuyInsForFoldedPlayers();
    }
    
    
    PokerPlayer mockPlayer1;
    PokerPlayer mockPlayer2;
    BettingRound bettingRound;
    Map<Integer, PokerPlayer> playerMap;
    
    private void setupForHandInfoTests(){
        telesina.startHand();
        
        bettingRound = mock(BettingRound.class);
        
        mockPlayer1 = mock(PokerPlayer.class);
        mockPlayer2 = mock(PokerPlayer.class);
        seatingMap.put(1, player1);
        seatingMap.put(2, player2);
        
        playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(player1.getId(), player1);
        playerMap.put(player2.getId(), player2);
    }
    
    @Test
    public void testNotSendingHandInfoWhenWinOnAllFolds() {
    	setupForHandInfoTests();
        
        when(mockPlayer1.hasFolded()).thenReturn(false);
        when(mockPlayer2.hasFolded()).thenReturn(true);
        when(state.countNonFoldedPlayers()).thenReturn(1);
        
        telesina.visit(bettingRound);
        
        ArgumentCaptor<HandResult> resultCaptor = ArgumentCaptor.forClass(HandResult.class);
        verify(state).notifyHandFinished(resultCaptor.capture(), Mockito.eq(HandEndStatus.NORMAL));
        HandResult hr = resultCaptor.getValue();
        Assert.assertThat(hr.getPlayerHands().size(), CoreMatchers.is(0));
    }
    
  @Test
  public void testSendingHandInfoWhenNormalWin() {
	  setupForHandInfoTests();
	  
	  telesina.currentRoundId = 5;
	
	  when(mockPlayer1.hasFolded()).thenReturn(false);
	  when(mockPlayer2.hasFolded()).thenReturn(false);
	  when(state.countNonFoldedPlayers()).thenReturn(2);
	
	  when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
	  
	  telesina.visit(bettingRound);
	
	  ArgumentCaptor<HandResult> resultCaptor = ArgumentCaptor.forClass(HandResult.class);
	  verify(state).notifyHandFinished(resultCaptor.capture(), Mockito.eq(HandEndStatus.NORMAL));
	  HandResult hr = resultCaptor.getValue();
	  Assert.assertThat(hr.getPlayerHands().size(), CoreMatchers.is(2));
  }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testClearBetStacksOnFoldedPlayersWhenRoundFinishes() {
        telesina.startHand();
        
        BettingRound bettingRound = mock(BettingRound.class);
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(1337);
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player2.getId()).thenReturn(1338);
        
        when(player1.getBetStack()).thenReturn(100L);
        when(player2.getBetStack()).thenReturn(0L);
        
        seatingMap.put(1, player1);
        seatingMap.put(2, player2);
        
        when(player1.hasFolded()).thenReturn(false);
        when(player2.hasFolded()).thenReturn(true);
        when(state.countNonFoldedPlayers()).thenReturn(1);
        
        telesina.visit(bettingRound);
        
        
        verify(state).notifyPotAndRakeUpdates(Mockito.anyCollection());
        
        
    }
    

}
