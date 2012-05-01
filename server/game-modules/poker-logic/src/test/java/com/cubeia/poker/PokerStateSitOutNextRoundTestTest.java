package com.cubeia.poker;

import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.mockito.Mockito.*;

public class PokerStateSitOutNextRoundTestTest {

    @Mock
    PokerState state;
    @Mock
    ServerAdapter serverAdapter;
    @Mock
    GameType gameType;
    int anteLevel = 100;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        state = new PokerState();
        state.serverAdapter = serverAdapter;
        state.gameType = gameType;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPlayerIsSittingIn() {
        int playerId = 1337;
        state.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.playerMap.get(playerId)).thenReturn(player);
        when(state.gameType.canPlayerAffordEntryBet(Mockito.eq(player), (PokerSettings) Mockito.any(), Mockito.eq(true))).thenReturn(true);
        state.currentHandPlayerMap = mock(Map.class);
        when(state.currentHandPlayerMap.containsKey(playerId)).thenReturn(false);
        when(player.isSittingOut()).thenReturn(true);
        state.playerIsSittingIn(playerId);

        verify(player).sitIn();
        verify(player).setSitOutNextRound(false);
        verify(player).setSitInAfterSuccessfulBuyIn(false);
        verify(serverAdapter).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN, false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPlayerIsSittingInWithInsufficientCashToBuyIn() {
        int playerId = 1337;
        state.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.playerMap.get(playerId)).thenReturn(player);
        when(state.gameType.canPlayerAffordEntryBet(Mockito.eq(player), (PokerSettings) Mockito.any(), Mockito.eq(true))).thenReturn(false);

        when(player.isSittingOut()).thenReturn(true);
        state.playerIsSittingIn(playerId);

        verify(player, never()).sitIn();
        verify(serverAdapter, never()).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN, false);
        verify(serverAdapter).notifyBuyInInfo(playerId, true);
    }

}
