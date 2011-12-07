package com.cubeia.poker.rounds.betting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;

public class BettingRoundInitTest {

    @Mock private GameType gameType;
    @Mock private PokerState state;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
    @Mock private PlayerToActCalculator playertoActCalculator;
    @Mock private ActionRequestFactory actionRequestFactory;
    private SortedMap<Integer, PokerPlayer> currentHandSeatingMap;

//  Integer player1Id = 1001;
    private Integer player2Id = 1002;
//  Integer player3Id = 1003;
    
    
    @Before
    public void setup() {
        initMocks(this);
        
        when(gameType.getState()).thenReturn(state);
        int entryBetLevel = 20;
        when(state.getEntryBetLevel()).thenReturn(entryBetLevel);
//        when(player1.getId()).thenReturn(player1Id);
        when(player2.getId()).thenReturn(player2Id);
//        when(player3.getId()).thenReturn(player3Id);
        
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
        when(state.countSittingInPlayers()).thenReturn(3);
        
        ActionRequest actionRequest = mock(ActionRequest.class);
        when(actionRequestFactory.createFoldCheckBetActionRequest(Mockito.any(BettingRound.class), Mockito.eq(player2)))
            .thenReturn(actionRequest);
        when(player2.getActionRequest()).thenReturn(actionRequest);

        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory);
        
        assertThat(round.playerToAct, is(player2Id));
        verify(player2).setActionRequest(actionRequest);
        verify(gameType).requestAction(actionRequest);
    }
    
    @Test
    public void testShortcutWhenShowdown() {
        int dealerSeatId = 0;
        when(playertoActCalculator.getFirstPlayerToAct(Mockito.eq(dealerSeatId), Mockito.eq(currentHandSeatingMap), 
            Mockito.anyListOf(Card.class))).thenReturn(player2);
        when(state.countSittingInPlayers()).thenReturn(3);
        when(player1.isAllIn()).thenReturn(true);
        when(player3.isAllIn()).thenReturn(true);
        
        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory);
        verify(gameType).scheduleRoundTimeout();
        assertThat(round.isFinished(), is(true));
    }
    
    @Test
    public void testShortcutWhenAllIsSittingOut() {
        int dealerSeatId = 0;
        when(playertoActCalculator.getFirstPlayerToAct(Mockito.eq(dealerSeatId), Mockito.eq(currentHandSeatingMap), 
            Mockito.anyListOf(Card.class))).thenReturn(player2);
        when(state.countSittingInPlayers()).thenReturn(0);
        
        BettingRound round = new BettingRound(gameType, dealerSeatId, playertoActCalculator, actionRequestFactory);
        verify(gameType).scheduleRoundTimeout();
        assertThat(round.isFinished(), is(true));
    }

}
