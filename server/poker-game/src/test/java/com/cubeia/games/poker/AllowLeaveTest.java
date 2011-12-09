package com.cubeia.games.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;

public class AllowLeaveTest {
    
    private static final int playerId = 2343;
    @Mock private Table table;
    @Mock private PokerPlayer pokerPlayer;
    @Mock private PokerState state;
    @Mock private StateInjector stateInjector;
    private PokerTableInterceptor tableInterceptor;
    

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        
        tableInterceptor = new PokerTableInterceptor();
        tableInterceptor.state = state;
        tableInterceptor.stateInjector = stateInjector;
        
        when(state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
    }

    @Test
    public void okIfPlayerHasNoRunningWalletRequestAndNotPlaying() {
        when(state.getGameState()).thenReturn(PokerState.NOT_STARTED);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
        
        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
        assertThat(response.isAllowed(), is(true));
    }

    @Test
    public void donwAllowIfPlaying() {
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
        
        when(state.getGameState()).thenReturn(PokerState.PLAYING);
        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
        assertThat(response.isAllowed(), is(false));
        
        when(state.getGameState()).thenReturn(PokerState.WAITING_TO_START);
        response = tableInterceptor.allowLeave(table, playerId);
        assertThat(response.isAllowed(), is(false));
        
        when(state.getGameState()).thenReturn(PokerState.SHUTDOWN);
        response = tableInterceptor.allowLeave(table, playerId);
        assertThat(response.isAllowed(), is(false));
    }
    
    @Test
    public void dontAllowIfWalletRequestActive() {
        when(state.getGameState()).thenReturn(PokerState.NOT_STARTED);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(true);
        
        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
        assertThat(response.isAllowed(), is(false));
    }

}
