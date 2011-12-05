package com.cubeia.poker.states;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.PokerState;

public class WaitingToStartSTMTest {

    @Test
    public void testTimeout() {
        WaitingToStartSTM wtss = new WaitingToStartSTM();
        
        PokerState state = Mockito.mock(PokerState.class);
        when(state.isTournamentTable()).thenReturn(false);
        when(state.countSittingInPlayers()).thenReturn(2);
        
        wtss.timeout(state);
        
        verify(state).setHandFinished(false);
        verify(state).commitPendingBalances();
        verify(state).sitOutPlayersMarkedForSitOutNextRound();
        verify(state).startHand();
        verify(state).cleanupPlayers();
    }
    
    @Test
    public void testTimeoutTooFewPlayers() {
        WaitingToStartSTM wtss = new WaitingToStartSTM();
        
        PokerState state = Mockito.mock(PokerState.class);
        when(state.isTournamentTable()).thenReturn(false);
        when(state.countSittingInPlayers()).thenReturn(1);
        
        wtss.timeout(state);
        
        verify(state).commitPendingBalances();
        verify(state).sitOutPlayersMarkedForSitOutNextRound();
        verify(state).setHandFinished(true);
        verify(state).setState(PokerState.NOT_STARTED);
        verify(state).cleanupPlayers();
        
        verify(state, never()).startHand();
    }

}
