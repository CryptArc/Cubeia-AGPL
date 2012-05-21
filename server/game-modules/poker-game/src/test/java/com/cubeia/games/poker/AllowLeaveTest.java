package com.cubeia.games.poker;

import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllowLeaveTest {

    private static final int playerId = 2343;
    @Mock
    private Table table;
    @Mock
    private PokerPlayer pokerPlayer;
    @Mock
    private PokerState state;
    @Mock
    private StateInjector stateInjector;
    private PokerTableInterceptor tableInterceptor;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        tableInterceptor = new PokerTableInterceptor();
        tableInterceptor.state = state;
        tableInterceptor.stateInjector = stateInjector;

        when(state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
    }

    // TODO: FIXTEST!
//    @Test
//    public void okIfPlayerHasNoRunningWalletRequestAndNotPlaying() {
//        when(state.getGameState()).thenReturn(PokerState.NOT_STARTED);
//        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
//
//        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
//        assertThat(response.isAllowed(), is(true));
//    }
//
//    @Test
//    public void okIfWaitingToStart() {
//        when(state.getGameState()).thenReturn(PokerState.WAITING_TO_START);
//        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
//
//        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
//        assertThat(response.isAllowed(), is(true));
//    }
//
//    @Test
//    public void okIfShutdown() {
//        when(state.getGameState()).thenReturn(PokerState.SHUTDOWN);
//        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
//
//        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
//        assertThat(response.isAllowed(), is(true));
//    }
//
//    @Test
//    public void donwAllowIfPlaying() {
//        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
//
//        when(state.getGameState()).thenReturn(PokerState.PLAYING);
//        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
//        assertThat(response.isAllowed(), is(false));
//    }
//
//    @Test
//    public void dontAllowIfWalletRequestActive() {
//        when(state.getGameState()).thenReturn(PokerState.NOT_STARTED);
//        when(pokerPlayer.isBuyInRequestActive()).thenReturn(true);
//
//        InterceptionResponse response = tableInterceptor.allowLeave(table, playerId);
//        assertThat(response.isAllowed(), is(false));
//    }

}
