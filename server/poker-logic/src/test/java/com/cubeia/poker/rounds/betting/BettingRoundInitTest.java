package com.cubeia.poker.rounds.betting;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.variant.texasholdem.TexasHoldemFutureActionsCalculator;

public class BettingRoundInitTest {

    @Mock private GameType gameType;
    @Mock private PokerState state;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
    @Mock private PlayerToActCalculator playertoActCalculator;
    @Mock private ActionRequestFactory actionRequestFactory;
    private SortedMap<Integer, PokerPlayer> currentHandSeatingMap;

    private Integer player2Id = 1002;
    
    
    @Before
    public void setup() {
        initMocks(this);
        
        when(gameType.getState()).thenReturn(state);
        int entryBetLevel = 20;
        when(state.getEntryBetLevel()).thenReturn(entryBetLevel);
        when(player2.getId()).thenReturn(player2Id);
        
        currentHandSeatingMap = new TreeMap<Integer, PokerPlayer>();
        currentHandSeatingMap.put(0, player1);
        currentHandSeatingMap.put(1, player2);
        currentHandSeatingMap.put(2, player3);
        
        when(state.getCurrentHandSeatingMap()).thenReturn(currentHandSeatingMap);
    }

    @Test
    public void testSimple() {
        int dealerSeatId = 0;
        when(playertoActCalculator.getFirstPlayerToAct(Mockito.eq(dealerSeatId), Mockito.eq(currentHandSeatingMap), 
            Mockito.anyListOf(Card.class))).thenReturn(player2);
        when(state.getPlayersReadyToStartHand()).thenReturn(asList(player1, player2, player3));
        
        ActionRequest actionRequest = mock(ActionRequest.class);
        when(actionRequestFactory.createFoldCheckBetActionRequest(Mockito.any(BettingRound.class), Mockito.eq(player2)))
            .thenReturn(actionRequest);
        when(player2.getActionRequest()).thenReturn(actionRequest);

        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory, new TexasHoldemFutureActionsCalculator());
        
        assertThat(round.playerToAct, is(player2Id));
        verify(player2).setActionRequest(actionRequest);
        verify(gameType).requestAction(actionRequest);
    }
    
    @Test
    public void testShortcutWhenShowdown() {
        int dealerSeatId = 0;
        when(playertoActCalculator.getFirstPlayerToAct(Mockito.eq(dealerSeatId), Mockito.eq(currentHandSeatingMap), 
            Mockito.anyListOf(Card.class))).thenReturn(player2);
        when(state.getPlayersReadyToStartHand()).thenReturn(asList(player1, player2, player3));
        when(player1.isAllIn()).thenReturn(true);
        when(player3.isAllIn()).thenReturn(true);
        
        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory, new TexasHoldemFutureActionsCalculator());
        verify(gameType).scheduleRoundTimeout();
        assertThat(round.isFinished(), is(true));
    }
    
    @Test
    @Ignore // This test is incorrect(?), a betting round should not end due to sit outs
    public void testShortcutWhenAllIsSittingOut() {
        int dealerSeatId = 0;
        when(playertoActCalculator.getFirstPlayerToAct(Mockito.eq(dealerSeatId), Mockito.eq(currentHandSeatingMap), 
            Mockito.anyListOf(Card.class))).thenReturn(player2);
        when(state.isEveryoneSittingOut()).thenReturn(true);
        
        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory, new TexasHoldemFutureActionsCalculator());
        verify(gameType).scheduleRoundTimeout();
        assertThat(round.isFinished(), is(true));
    }

}
