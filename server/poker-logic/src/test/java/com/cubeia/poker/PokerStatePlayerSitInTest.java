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

public class PokerStatePlayerSitInTest {

	@Mock PokerState state;
//	@Mock PokerSettings settings;
	@Mock ServerAdapter serverAdapter;
	@Mock GameType gameType;
	int anteLevel = 100;
	
	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this);
	    
		state = new PokerState();
		state.serverAdapter = serverAdapter;
		state.gameType = gameType;
		
//        when(settings.getRakeSettins()).thenReturn(TestUtils.createOnePercentRakeSettings());
//        when(settings.getAnteLevel()).thenReturn(anteLevel);
//        when(settings.getVariant()).thenReturn(PokerVariant.TELESINA);
	}
	
    @SuppressWarnings("unchecked")
    @Test
    public void testPlayerIsSittingIn() {
        int playerId = 1337;
        state.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.playerMap.get(playerId)).thenReturn(player);
        when(state.gameType.canPlayerBuyIn(Mockito.eq(player), (PokerSettings) Mockito.any())).thenReturn(true);
        
        state.playerIsSittingIn(playerId);
        
        verify(player).sitIn();
        verify(player).setSitOutNextRound(false);
        verify(player).setSitInAfterSuccessfulBuyIn(false);
        verify(serverAdapter).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.NORMAL);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testPlayerIsSittingInWithInsufficientCashToBuyIn() {
        int playerId = 1337;
        state.playerMap = mock(Map.class);
        PokerPlayer player = mock(PokerPlayer.class);
        when(state.playerMap.get(playerId)).thenReturn(player);
        when(state.gameType.canPlayerBuyIn(Mockito.eq(player), (PokerSettings) Mockito.any())).thenReturn(false);
        
        state.playerIsSittingIn(playerId);
        
        verify(player, never()).sitIn();
        verify(serverAdapter, never()).notifyPlayerStatusChanged(playerId, PokerPlayerStatus.NORMAL);
        verify(serverAdapter).notifyBuyInInfo(playerId, true);
    }
    
    
//    public void playerIsSittingIn(int playerId) {
//        
//        log.debug("player {} is sitting in", playerId);
//        
//        PokerPlayer player = playerMap.get(playerId);
//        if (player == null) {
//            log.error("player {} not at table but tried to sit in. Ignoring.", playerId);
//            return;
//        }
//        
//        if (gameType.canPlayerBuyIn(player, settings)) {
//            player.sitIn();
//            player.setSitOutNextRound(false);
//            player.setSitInAfterSuccessfulBuyIn(false);
//            notifyPlayerSittingIn(playerId);
//            
//            // Check if we are waiting for this player (could be a reconnect)
//            // if so then re-send the action request
//            
//            startGame();
//        } else {
//            
//        }
//    }
    
    

}
