package com.cubeia.games.poker;

import static com.cubeia.games.poker.handler.BackendCallHandler.EXT_PROP_KEY_TABLE_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;

public class BackendPlayerSessionHandlerTest {

    @Mock CashGamesBackendContract cashGamesBackendContract;
    @Mock Table table;
    @Mock PokerState state;
    private BackendPlayerSessionHandler backendPlayerSessionHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        backendPlayerSessionHandler = new BackendPlayerSessionHandler();
        backendPlayerSessionHandler.cashGameBackend = cashGamesBackendContract;
    }
    
    @Test
    public void testEndPlayerSessionInBackend() throws CloseSessionFailedException {
        PokerPlayerImpl pokerPlayer = mock(PokerPlayerImpl.class);
        PlayerSessionId sessionId = mock(PlayerSessionId.class);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(sessionId);
        
        backendPlayerSessionHandler.endPlayerSessionInBackend(table, pokerPlayer, -1);

        ArgumentCaptor<CloseSessionRequest> requestCaptor = ArgumentCaptor.forClass(CloseSessionRequest.class);
        verify(cashGamesBackendContract).closeSession(requestCaptor.capture());
        CloseSessionRequest closeSessionRequest = requestCaptor.getValue();
        assertThat(closeSessionRequest.getPlayerSessionId(), is(sessionId));
        assertThat(closeSessionRequest.getRoundNumber(), is(-1));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testEndPlayerSessionInBackendFailIfWrongPlayerType() {
        PokerPlayer pokerPlayer = mock(PokerPlayer.class);
        backendPlayerSessionHandler.endPlayerSessionInBackend(table, pokerPlayer, -1);
    }

    @SuppressWarnings("serial")
    @Test
    public void testStartWalletSession() {
        TableId tableId = new TableId() {};
        Map<String, Serializable> extProps = Collections.singletonMap(EXT_PROP_KEY_TABLE_ID, (Serializable) tableId);
        when(state.getExternalTableProperties()).thenReturn(extProps);
        FirebaseCallbackFactory callbackFactory = mock(FirebaseCallbackFactory.class);
        when(cashGamesBackendContract.getCallbackFactory()).thenReturn(callbackFactory);
        OpenSessionCallback openSessionCallback = mock(OpenSessionCallback.class);
        when(callbackFactory.createOpenSessionCallback(table)).thenReturn(openSessionCallback);
        
        int playerId = 234989;
        backendPlayerSessionHandler.startWalletSession(state, table, playerId, -1);

        verify(callbackFactory).createOpenSessionCallback(table);
        ArgumentCaptor<OpenSessionRequest> requestCaptor = ArgumentCaptor.forClass(OpenSessionRequest.class);
        verify(cashGamesBackendContract).openSession(requestCaptor.capture(), Mockito.eq(openSessionCallback));
        OpenSessionRequest openSessionRequest = requestCaptor.getValue();
        assertThat(openSessionRequest.getPlayerId(), is(playerId));
        assertThat(openSessionRequest.getTableId(), is(tableId));
        assertThat(openSessionRequest.getRoundNumber(), is(-1));
    }

    @Test(expected = NullPointerException.class)
    public void testStartWalletSessionFailIfTableNotAnnounced() {
        Map<String, Serializable> extProps = Collections.emptyMap();
        when(state.getExternalTableProperties()).thenReturn(extProps);
        backendPlayerSessionHandler.startWalletSession(state, table, 234989, -1);
    }
    
}
