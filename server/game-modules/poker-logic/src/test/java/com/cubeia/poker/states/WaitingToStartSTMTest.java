package com.cubeia.poker.states;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class WaitingToStartSTMTest {

    @Test
    public void testTimeout() {
        WaitingToStartSTM wtss = new WaitingToStartSTM();

        PokerState state = Mockito.mock(PokerState.class);
        when(state.isTournamentTable()).thenReturn(false);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(state.getPlayersReadyToStartHand()).thenReturn(asList(player1, player2));
        ServerAdapter serverAdapter = mock(ServerAdapter.class);
        when(state.getServerAdapter()).thenReturn(serverAdapter);
        ArrayList<PokerPlayer> seatedPlayers = new ArrayList<PokerPlayer>();
        when(state.getSeatedPlayers()).thenReturn(seatedPlayers);

        wtss.timeout(state);

        verify(serverAdapter).performPendingBuyIns(seatedPlayers);
        verify(state).setHandFinished(false);
        verify(state).setPlayersWithoutMoneyAsSittingOut();
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
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.getPlayersReadyToStartHand()).thenReturn(asList(player));
        ServerAdapter serverAdapter = mock(ServerAdapter.class);
        when(state.getServerAdapter()).thenReturn(serverAdapter);
        ArrayList<PokerPlayer> seatedPlayers = new ArrayList<PokerPlayer>();
        when(state.getSeatedPlayers()).thenReturn(seatedPlayers);

        wtss.timeout(state);

        verify(serverAdapter).performPendingBuyIns(seatedPlayers);
        verify(state).commitPendingBalances();
        verify(state).sitOutPlayersMarkedForSitOutNextRound();
        verify(state).setHandFinished(true);
        verify(state).setState(PokerState.NOT_STARTED);
        verify(state).cleanupPlayers();

        verify(state, never()).startHand();
    }

}
