package com.cubeia.games.poker.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;

public class BackendCallHandlerTest {

    @Mock private PokerState state;
    @Mock private Table table;
    @Mock private GameNotifier notifier;
    @Mock private PokerPlayerImpl pokerPlayer;
    @Mock private CashGamesBackendContract backend;
    @Mock private FirebaseCallbackFactory callbackFactory;
    private BackendCallHandler callHandler;
    private int playerId = 1337;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        callHandler = new BackendCallHandler(state, table);
        when(table.getNotifier()).thenReturn(notifier);
        when(state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
        when(backend.getCallbackFactory()).thenReturn(callbackFactory);
    }
    
    @Test
    public void testHandleReserveSuccessfulResponse() throws IOException {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        int balanceOnRemoteWallet = 10000;
        BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId , balanceOnRemoteWallet, -1);
        int amount = 500;
        ReserveResponse reserveResponse = new ReserveResponse(balanceUpdate , amount);
        when(pokerPlayer.getBalance()).thenReturn((long) amount);
        when(state.isPlayerInHand(playerId)).thenReturn(false);
        
        callHandler.handleReserveSuccessfulResponse(playerId, reserveResponse);
        
        verify(pokerPlayer).addChips(amount);
        verify(pokerPlayer, Mockito.never()).addPendingAmount(Mockito.anyLong());
        ArgumentCaptor<GameDataAction> buyInResponseCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(notifier).notifyPlayer(Mockito.eq(playerId), buyInResponseCaptor.capture());
        GameDataAction buyInDataAction = buyInResponseCaptor.getValue();
        BuyInResponse buyInRespPacket = (BuyInResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(buyInDataAction.getData());
        assertThat(buyInRespPacket.balance, is(amount));
        assertThat(buyInRespPacket.pendingBalance, is(0));
        assertThat(buyInRespPacket.resultCode, is(Enums.BuyInResultCode.OK));
        
        verify(state).playerIsSittingIn(playerId);
        verify(state).notifyPlayerBalance(playerId);
    }
    
    @Test
    public void testHandleReserveSuccessfulResponseWhenInHand() throws IOException {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        int balanceOnRemoteWallet = 10000;
        BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId , balanceOnRemoteWallet, -1);
        int amount = 500;
        ReserveResponse reserveResponse = new ReserveResponse(balanceUpdate , amount);
        when(pokerPlayer.getBalance()).thenReturn(0L);
        when(pokerPlayer.getPendingBalance()).thenReturn((long) amount);
        when(state.isPlayerInHand(playerId)).thenReturn(true);

        callHandler.handleReserveSuccessfulResponse(playerId, reserveResponse);
        
        verify(pokerPlayer, Mockito.never()).addChips(Mockito.anyLong());
        verify(pokerPlayer).addPendingAmount(amount);
        
        ArgumentCaptor<GameDataAction> buyInResponseCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        
        verify(notifier).notifyPlayer(Mockito.eq(playerId), buyInResponseCaptor.capture());
        GameDataAction buyInDataAction = buyInResponseCaptor.getValue();
        BuyInResponse buyInRespPacket = (BuyInResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(buyInDataAction.getData());
        assertThat(buyInRespPacket.balance, is(0));
        assertThat(buyInRespPacket.pendingBalance, is(amount));
        assertThat(buyInRespPacket.resultCode, is(Enums.BuyInResultCode.OK));
        
        verify(state).playerIsSittingIn(playerId);
        verify(state).notifyPlayerBalance(playerId);
    }
    

    @Test
    public void testHandleOpenSessionSuccessfulResponse() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        OpenSessionResponse openSessionResponse = new OpenSessionResponse(playerSessionId, Collections.<String, String>emptyMap());
        callHandler.handleOpenSessionSuccessfulResponse(openSessionResponse);
        verify(pokerPlayer).setPlayerSessionId(playerSessionId);
    }
    
}
