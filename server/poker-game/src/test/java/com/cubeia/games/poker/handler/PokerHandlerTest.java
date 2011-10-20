package com.cubeia.games.poker.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInRequest;
import se.jadestone.dicearena.game.poker.network.protocol.InternalSerializedObject;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitinRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitoutRequest;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.SitOutStatus;

public class PokerHandlerTest {

    @Mock private PokerState state;
    @Mock private Table table;
    @Mock private GameNotifier notifier;
    @Mock private PokerPlayerImpl pokerPlayer;
    @Mock private CashGamesBackendContract backend;
    @Mock private FirebaseCallbackFactory callbackFactory;
    @Mock private BackendCallHandler backendHandler;
    private PokerHandler pokerHandler;
    private int playerId = 1337;

    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        pokerHandler = new PokerHandler();
        pokerHandler.setPlayerId(playerId);
        pokerHandler.state = state;
        pokerHandler.table = table;
        pokerHandler.cashGameBackend = backend;
        pokerHandler.backendHandler = backendHandler;
        
        when(pokerHandler.table.getNotifier()).thenReturn(notifier);
        when(pokerHandler.state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
        when(backend.getCallbackFactory()).thenReturn(callbackFactory);
    }
    
    @After
    public void cleanUpStuffThatIsSmackedByThisTest() {
        TimeoutCache.instance = new TimeoutCache();
    }
    
    @Test
    public void testVisitPerformAction() {
        TimeoutCache.instance = mock(TimeoutCache.class);
        PerformAction performAction = new PerformAction();
        performAction.seq = 10;
        performAction.betAmount = 3434;
        performAction.action = new PlayerAction();
        
        FirebaseState adapterState = mock(FirebaseState.class);
        when(state.getAdapterState()).thenReturn(adapterState);
        when(adapterState.getCurrentRequestSequence()).thenReturn(performAction.seq);

        pokerHandler.visit(performAction);
        
        verify(TimeoutCache.instance).removeTimeout(Mockito.anyInt(), Mockito.eq(playerId), Mockito.any(TableScheduler.class));
        ArgumentCaptor<PokerAction> captor = ArgumentCaptor.forClass(PokerAction.class);
        verify(state).act(captor.capture());
        PokerAction pokerAction = captor.getValue();
        assertThat((int) pokerAction.getBetAmount(), is(performAction.betAmount));
    }

    @Test
    public void testVisitPlayerSitinRequest() {
        PlayerSitinRequest sitInRequest = new PlayerSitinRequest();
        pokerHandler.visit(sitInRequest);
        verify(pokerHandler.state).playerIsSittingIn(playerId);
    }

    @Test
    public void testVisitPlayerSitoutRequest() {
        PlayerSitoutRequest sitOutRequest = new PlayerSitoutRequest();
        pokerHandler.visit(sitOutRequest);
        verify(pokerHandler.state).playerIsSittingOut(playerId, SitOutStatus.SITTING_OUT);
    }

    @Test
    public void testVisitBuyInInfoRequest() throws IOException {
       	
    	BuyInInfoRequest packet = new BuyInInfoRequest();
		pokerHandler.visit(packet);
		
		verify(state).notifyBuyinInfo(playerId, false);
    }

    @Test
    public void testVisitBuyInRequest() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        BuyInRequest buyInRequest = new BuyInRequest(4000, true);
        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);
        
        pokerHandler.visit(buyInRequest);
        
        ArgumentCaptor<ReserveRequest> reqCaptor = ArgumentCaptor.forClass(ReserveRequest.class);
        verify(pokerPlayer).setSitInAfterSuccessfulBuyIn(true);
        verify(backend).reserve(reqCaptor.capture(), Mockito.eq(reserveCallback));
        ReserveRequest reserveReq = reqCaptor.getValue();
        assertThat(reserveReq.amount, is(buyInRequest.amount));
        assertThat(reserveReq.playerSessionId, is(playerSessionId));
        assertThat(reserveReq.roundNumber, is(-1));
    }

    @Test
    public void testVisitInternalSerializedObject() throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(new OpenSessionResponse(null, null));
        
        InternalSerializedObject packet = new InternalSerializedObject();
        packet.bytes = byteOut.toByteArray();
        pokerHandler.visit(packet);
        verify(backendHandler).handleOpenSessionSuccessfulResponse(Mockito.any(OpenSessionResponse.class));
        
        byteOut = new ByteArrayOutputStream();
        objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(new ReserveResponse(null, 0));
        packet.bytes = byteOut.toByteArray();
        pokerHandler.visit(packet);
        verify(backendHandler).handleReserveSuccessfulResponse(Mockito.eq(playerId), Mockito.any(ReserveResponse.class));
    }
}
