package com.cubeia.poker;

import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.GameType;
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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        state = new PokerState();
        state.init(gameType, null);
        state.setServerAdapter(serverAdapter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPlayerIsSittingIn() {
        int playerId = 1337;
        state.pokerContext.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.pokerContext.playerMap.get(playerId)).thenReturn(player);
        when(gameType.canPlayerAffordEntryBet(Mockito.eq(player), (PokerSettings) Mockito.any(), Mockito.eq(true))).thenReturn(true);
        state.pokerContext.currentHandPlayerMap = mock(Map.class);
        when(state.pokerContext.getCurrentHandPlayerMap().containsKey(playerId)).thenReturn(false);
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
        state.pokerContext.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.pokerContext.playerMap.get(playerId)).thenReturn(player);
        when(gameType.canPlayerAffordEntryBet(Mockito.eq(player), (PokerSettings) Mockito.any(), Mockito.eq(true))).thenReturn(false);

        when(player.isSittingOut()).thenReturn(true);
        state.playerIsSittingIn(playerId);

        verify(player, never()).sitIn();
        verify(serverAdapter, never()).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN, false);
        verify(serverAdapter).notifyBuyInInfo(playerId, true);
    }

}
