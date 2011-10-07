package com.cubeia.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class PokerStateTest {

    @Test
    public void testNotifyHandFinished() {
        PokerState state = new PokerState();
        state.serverAdapter = mock(ServerAdapter.class);
        state.setTournamentTable(false);
        
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        Result result1 = mock(Result.class);
        when(player1.getBalance()).thenReturn(0L);
        results.put(player1, result1);
        HandResult result = new HandResult(results, null, null);
        state.playerMap = new HashMap<Integer, PokerPlayer>();
        state.playerMap.put(1337, player1);
        Long winningsIncludingOwnBets = 344L;
        when(result1.getWinningsIncludingOwnBets()).thenReturn(winningsIncludingOwnBets );
        
        state.notifyHandFinished(result, HandEndStatus.NORMAL);
        
        verify(player1).addChips(winningsIncludingOwnBets);
        verify(state.serverAdapter).notifyHandEnd(result, HandEndStatus.NORMAL);
        verify(player1).commitPendingBalance();
        verify(player1).setSitOutStatus(SitOutStatus.SITTING_OUT);
        verify(state.serverAdapter).scheduleTimeout(Mockito.anyLong());
        assertThat(state.isFinished(), is(true));
        assertThat(state.currentState, is(PokerState.WAITING_TO_START));
    }

    @Test
    public void testCommitPendingBalances() {
        PokerState state = new PokerState();
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(0, player1);
        playerMap.put(1, player2);
        state.playerMap = playerMap;
        
        state.commitPendingBalances();
        
        verify(player1).commitPendingBalance();
        verify(player2).commitPendingBalance();
    }

}
