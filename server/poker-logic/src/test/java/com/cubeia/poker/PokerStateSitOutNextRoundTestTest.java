package com.cubeia.poker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;

public class PokerStateSitOutNextRoundTestTest {

	@Mock PokerState state;
	@Mock ServerAdapter serverAdapter;
	@Mock GameType gameType;
	int anteLevel = 100;
	
	@Before
	public void setup(){
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

        when(player.isSittingOut()).thenReturn(true);
        state.playerIsSittingIn(playerId);
        
        verify(player).sitIn();
        verify(player).setSitOutNextRound(false);
        verify(player).setSitInAfterSuccessfulBuyIn(false);
        verify(serverAdapter).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN);
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
        verify(serverAdapter, never()).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN);
        verify(serverAdapter).notifyBuyInInfo(playerId, true);
    }

}
