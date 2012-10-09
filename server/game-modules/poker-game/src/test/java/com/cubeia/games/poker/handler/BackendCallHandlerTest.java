/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.handler;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.adapter.FirebaseServerAdapter;
import com.cubeia.games.poker.common.Money;
import com.cubeia.games.poker.io.protocol.BuyInResponse;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.io.protocol.ErrorPacket;
import com.cubeia.games.poker.io.protocol.ProtocolObjectFactory;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.telesina.Telesina;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BackendCallHandlerTest {

    private static final int maxBuyIn = 1000;
    @Mock
    private PokerState state;
    @Mock
    private FirebaseState firebaseState;
    @Mock
    private Telesina gameType;
    @Mock
    private Table table;
    @Mock
    private GameNotifier notifier;
    @Mock
    private PokerPlayerImpl pokerPlayer;
    @Mock
    private CashGamesBackendContract backend;
    @Mock
    private FirebaseCallbackFactory callbackFactory;
    @Mock
    private FirebaseServerAdapter serverAdapter;
    @Mock
    private BackendPlayerSessionHandler backendPlayerSessionHandler;
    private BackendCallHandler callHandler;
    private int playerId = 1337;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        callHandler = new BackendCallHandler(state, table, backendPlayerSessionHandler);
        when(table.getNotifier()).thenReturn(notifier);
        when(state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
        when(backend.getCallbackFactory()).thenReturn(callbackFactory);
        when(state.getServerAdapter()).thenReturn(serverAdapter);
        when(state.getMaxBuyIn()).thenReturn(maxBuyIn);
        when(pokerPlayer.getId()).thenReturn(playerId);
        when(state.getAdapterState()).thenReturn(firebaseState);
    }

    PlayerSessionId playerSessionId;
    ReserveResponse reserveResponse;
    long balanceNotInHand = 33;
    long amountRequested;
    BuyInResponse buyInRespPacket;
    private String tableSessionReference = "xSessionRef";
    private String tableReference = "xTableRef";

    @SuppressWarnings("unchecked")
    private void setupForHandleReserveSuccessfulResponse(boolean isSitInAfterBuyIn) throws IOException {
        tableReference = "tableRef";
        when(state.getExternalTableProperties()).thenReturn(singletonMap(CashGamesBackendContract.MARKET_TABLE_REFERENCE_KEY, (Serializable) tableReference));
        amountRequested = 500;
        playerSessionId = new PlayerSessionIdImpl(playerId);
        int balanceOnRemoteWallet = 10000;
        BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId, new Money(balanceOnRemoteWallet, "USD", 2), -1);
        reserveResponse = new ReserveResponse(balanceUpdate, new Money(amountRequested, "USD", 2));
        reserveResponse.setProperty(CashGamesBackendContract.MARKET_TABLE_SESSION_REFERENCE_KEY, tableSessionReference);

        when(pokerPlayer.getPendingBalanceSum()).thenReturn(balanceNotInHand + amountRequested);
        when(pokerPlayer.isSitInAfterSuccessfulBuyIn()).thenReturn(isSitInAfterBuyIn);
        Map<Serializable, Serializable> attributes = mock(Map.class);
        when(pokerPlayer.getAttributes()).thenReturn(attributes);

        callHandler.handleReserveSuccessfulResponse(reserveResponse);

        verify(pokerPlayer).addNotInHandAmount(amountRequested);
        verify(attributes).put(PokerPlayerImpl.ATTR_PLAYER_EXTERNAL_SESSION_ID, tableSessionReference);
        verify(pokerPlayer).clearRequestedBuyInAmountAndRequest();

        ArgumentCaptor<GameDataAction> buyInResponseCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(notifier).notifyPlayer(Mockito.eq(playerId), buyInResponseCaptor.capture());
        GameDataAction buyInDataAction = buyInResponseCaptor.getValue();
        buyInRespPacket = (BuyInResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(buyInDataAction.getData());

        assertThat(buyInRespPacket.balance, is(0));
        assertThat(buyInRespPacket.pendingBalance, is((int) (amountRequested + balanceNotInHand)));
        assertThat(buyInRespPacket.resultCode, is(Enums.BuyInResultCode.OK));
        verify(serverAdapter).notifyPlayerBalance(pokerPlayer);
    }

    @Test
    public void testHandleReserveFailed() throws IOException {
        PlayerSessionId sessionId = mock(PlayerSessionId.class);
        when(sessionId.getPlayerId()).thenReturn(playerId);
        ReserveFailedResponse response = new ReserveFailedResponse(sessionId, ErrorCode.MAX_LIMIT_REACHED, "fallör", false);

        callHandler.handleReserveFailedResponse(response);

        verify(pokerPlayer).clearRequestedBuyInAmountAndRequest();
        ArgumentCaptor<GameDataAction> actionCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(notifier).notifyPlayer(Mockito.eq(playerId), actionCaptor.capture());

        GameDataAction action = actionCaptor.getValue();
        BuyInResponse buyInResponse = (BuyInResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(action.getData());
        assertThat(buyInResponse.amountBroughtIn, is(0));
        assertThat(buyInResponse.pendingBalance, is(0));
        assertThat(buyInResponse.resultCode, is(Enums.BuyInResultCode.MAX_LIMIT_REACHED));
    }

    @Test
    public void testHandleReserveFailedWithSessionCloseForced() throws IOException {
        int roundNumber = 43434;
        String handId = "h1111";
        PlayerSessionId sessionId = mock(PlayerSessionId.class);
        when(sessionId.getPlayerId()).thenReturn(playerId);
        when(firebaseState.getHandCount()).thenReturn(roundNumber);
        when(serverAdapter.getIntegrationHandId()).thenReturn(handId);
        ReserveFailedResponse response = new ReserveFailedResponse(sessionId, ErrorCode.MAX_LIMIT_REACHED, "fallör", true);

        callHandler.handleReserveFailedResponse(response);

        verify(pokerPlayer).clearRequestedBuyInAmountAndRequest();
        verify(state).unseatPlayer(playerId, false);


        StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
        ArgumentCaptor<GameDataAction> actionCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(notifier, Mockito.times(1)).notifyPlayer(Mockito.eq(playerId), actionCaptor.capture());
//        
//        GameDataAction action = actionCaptor.getAllValues().get(0);
//        BuyInResponse buyInResponse = (BuyInResponse) styx.unpack(action.getData());
//        assertThat(buyInResponse.amountBroughtIn, is(0));
//        assertThat(buyInResponse.pendingBalance, is(0));
//        assertThat(buyInResponse.resultCode, is(Enums.BuyInResultCode.MAX_LIMIT_REACHED));

        GameDataAction action = actionCaptor.getValue();
        ErrorPacket errorPacket = (ErrorPacket) styx.unpack(action.getData());
        assertThat(errorPacket.code, is(Enums.ErrorCode.CLOSED_SESSION_DUE_TO_FATAL_ERROR));
        assertThat(errorPacket.referenceId, is(handId));

        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer, roundNumber);
    }

    @Test
    public void testHandleReserveSuccessfulResponse() throws IOException {
        when(state.isPlayerInHand(playerId)).thenReturn(true);

        setupForHandleReserveSuccessfulResponse(false);
        verify(serverAdapter).notifyExternalSessionReferenceInfo(playerId, tableReference, tableSessionReference);
        verify(state, never()).playerIsSittingIn(playerId);
        verify(pokerPlayer, never()).commitBalanceNotInHand(Mockito.anyInt());
    }

    @Test
    public void testHandleReserveSuccessfulCommitPendingIfNotInHand() throws IOException {
        when(state.isPlayerInHand(playerId)).thenReturn(false);

        setupForHandleReserveSuccessfulResponse(false);
        verify(serverAdapter).notifyExternalSessionReferenceInfo(playerId, tableReference, tableSessionReference);
        verify(state, never()).playerIsSittingIn(playerId);

        verify(pokerPlayer).commitBalanceNotInHand(maxBuyIn);
    }

    @Test
    public void testHandleReserveSuccessfulResponseSitInIfSuccessful() throws IOException {
        setupForHandleReserveSuccessfulResponse(true);
        verify(state).playerIsSittingIn(playerId);
    }

    @Test
    public void testHandleOpenSessionSuccessfulResponse() {
        when(gameType.canPlayerAffordEntryBet(any(PokerPlayer.class), any(PokerSettings.class), Mockito.eq(true))).thenReturn(false);
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        OpenSessionResponse openSessionResponse = new OpenSessionResponse(playerSessionId, Collections.<String, String>emptyMap());
        callHandler.handleOpenSessionSuccessfulResponse(openSessionResponse);
        verify(pokerPlayer).setPlayerSessionId(playerSessionId);
    }

    @SuppressWarnings({"serial", "unchecked"})
    @Test
    public void testHandleAnnounceTableSuccessfulResponse() {
        Map<String, Serializable> extProps = Mockito.mock(Map.class);
        when(state.getExternalTableProperties()).thenReturn(extProps);
        LobbyTableAttributeAccessor attributeAccessor = mock(LobbyTableAttributeAccessor.class);
        when(table.getAttributeAccessor()).thenReturn(attributeAccessor);

        TableId tableId = new TableId() {
        };
        AnnounceTableResponse announceTableResponse = new AnnounceTableResponse(tableId);
        announceTableResponse.setProperty("test", "klyka");
        callHandler.handleAnnounceTableSuccessfulResponse(announceTableResponse);
        verify(extProps).put("tableId", tableId);
        verify(extProps).putAll(announceTableResponse.getTableProperties());
        verify(attributeAccessor).setIntAttribute("VISIBLE_IN_LOBBY", 1);
    }

}
