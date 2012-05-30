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
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.*;
import com.cubeia.games.poker.io.protocol.Enums.BuyInResultCode;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.SitOutStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PokerHandlerTest {

    @Mock
    private PokerState state;
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
    private TimeoutCache timeoutCache;
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
        pokerHandler.timeoutCache = timeoutCache;

        pokerHandler.actionTransformer = new ActionTransformer();

        FirebaseState state = Mockito.mock(FirebaseState.class);

        when(pokerPlayer.getId()).thenReturn(playerId);
        when(pokerHandler.state.getAdapterState()).thenReturn(state);
        when(pokerHandler.table.getNotifier()).thenReturn(notifier);
        when(pokerHandler.state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
        when(pokerHandler.state.getMaxBuyIn()).thenReturn(6000);
        when(pokerHandler.state.getMinBuyIn()).thenReturn(1000);
        when(backend.getCallbackFactory()).thenReturn(callbackFactory);
    }

    @Test
    public void testVisitPerformAction() {
        PerformAction performAction = new PerformAction();
        performAction.seq = 10;
        performAction.betAmount = 3434;
        performAction.action = new PlayerAction();

        FirebaseState adapterState = mock(FirebaseState.class);
        when(state.getAdapterState()).thenReturn(adapterState);
        when(adapterState.getCurrentRequestSequence()).thenReturn(performAction.seq);

        pokerHandler.visit(performAction);

        verify(timeoutCache).removeTimeout(Mockito.anyInt(), Mockito.eq(playerId), Mockito.any(TableScheduler.class));
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
        verify(pokerHandler.state).playerSitsOut(playerId, SitOutStatus.SITTING_OUT);
    }

    @Test
    public void testVisitBuyInInfoRequest() throws IOException {

        BuyInInfoRequest packet = new BuyInInfoRequest();
        pokerHandler.visit(packet);

        verify(state).notifyBuyinInfo(playerId, false);
    }

    @Test
    public void testVisitBuyInRequest() throws IOException {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);

        long balance = 34L;
        when(pokerPlayer.getBalance()).thenReturn(balance);
        long pending = 44L;
        when(pokerPlayer.getPendingBalanceSum()).thenReturn(pending);

        int buyInAmount = 4000;
        BuyInRequest buyInRequest = new BuyInRequest(buyInAmount, true);
        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        verify(backend, never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(state).handleBuyInRequest(pokerPlayer, buyInAmount);
        verify(pokerPlayer).setSitInAfterSuccessfulBuyIn(true);
        verify(state).playerIsSittingIn(playerId);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(notifier).sendToClient(Mockito.eq(playerId), captor.capture());
        GameDataAction gameDataAction = captor.getValue();

        StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
        BuyInResponse buyInResponse = (BuyInResponse) styx.unpack(gameDataAction.getData());
        assertThat(buyInResponse.amountBroughtIn, is(0));
        assertThat(buyInResponse.balance, is((int) balance));
        assertThat(buyInResponse.pendingBalance, is((int) pending));
        assertThat(buyInResponse.resultCode, is(BuyInResultCode.PENDING));
    }


    @Test
    public void testVisitBuyInRequestAmountTooHigh() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        when(pokerPlayer.getBalance()).thenReturn(0L);
        when(pokerPlayer.getBalanceNotInHand()).thenReturn(0L);

        // Request more money than max buy in
        BuyInRequest buyInRequest = new BuyInRequest(14000, true);

        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        // since amount is higher than max allowed we should never get a call to the backend
        verify(backend, never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(pokerPlayer, never()).addRequestedBuyInAmount(Mockito.anyInt());
        verify(state, never()).playerIsSittingIn(playerId);
        verify(reserveCallback).requestFailed(Mockito.any(ReserveFailedResponse.class));
    }

    @Test
    public void testVisitBuyInRequestAmountTooLow() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        when(pokerPlayer.getBalance()).thenReturn(0L);
        when(pokerPlayer.getBalanceNotInHand()).thenReturn(0L);

        // Request more money than max buy in
        BuyInRequest buyInRequest = new BuyInRequest(10, true);

        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        // since amount is higher than max allowed we should never get a call to the backend
        verify(backend, Mockito.never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(pokerPlayer, never()).addRequestedBuyInAmount(Mockito.anyInt());
        verify(state, never()).playerIsSittingIn(playerId);
        verify(reserveCallback).requestFailed(Mockito.any(ReserveFailedResponse.class));
    }

    @Test
    public void testVisitBuyInRequestAmountTooHighForCurrentBalance() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        when(pokerPlayer.getBalance()).thenReturn(4000L);
        when(pokerPlayer.getBalanceNotInHand()).thenReturn(0L);

        // Request more money than allowed, balance + buyin <= max buyin
        BuyInRequest buyInRequest = new BuyInRequest(3000, true);

        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        // since amount is higher than max allowed we should never get a call to the backend
        verify(backend, Mockito.never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(pokerPlayer, never()).addRequestedBuyInAmount(Mockito.anyInt());
        verify(state, never()).playerIsSittingIn(playerId);
        verify(reserveCallback).requestFailed(Mockito.any(ReserveFailedResponse.class));
    }

    @Test
    public void testVisitBuyInRequestAmountTooHighForCurrentBalanceIncludingPendingBalance() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        when(pokerPlayer.getBalance()).thenReturn(2000L); // balance is ok
        when(pokerPlayer.getBalanceNotInHand()).thenReturn(4000L); // pending will make it fail

        // Request more money than allowed, pendingBalance + balance + buyin <= max buyin
        BuyInRequest buyInRequest = new BuyInRequest(3000, true);

        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        // since amount is higher than max allowed we should never get a call to the backend
        verify(backend, Mockito.never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(pokerPlayer, never()).addRequestedBuyInAmount(Mockito.anyInt());
        verify(state, never()).playerIsSittingIn(playerId);
        verify(reserveCallback).requestFailed(Mockito.any(ReserveFailedResponse.class));
    }

    @Test
    public void testVisitBuyInRequestAmountTooHighForCurrentBalanceIncludingPendingBalanceButJustSlightly() {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        when(pokerPlayer.getPlayerSessionId()).thenReturn(playerSessionId);
        when(pokerPlayer.getBalance()).thenReturn(3000L); // balance is ok
        when(pokerPlayer.getBalanceNotInHand()).thenReturn(2000L); // pending will make it fail

        // Request more money than allowed, pendingBalance + balance + buyin <= max buyin
        // the player can actually buy in 1000 but requests 2000
        BuyInRequest buyInRequest = new BuyInRequest(2000, true);

        ReserveCallback reserveCallback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(table)).thenReturn(reserveCallback);

        pokerHandler.visit(buyInRequest);

        // since amount is higher than max allowed we should never get a call to the backend
        verify(backend, Mockito.never()).reserve(Mockito.any(ReserveRequest.class), Mockito.any(ReserveCallback.class));
        verify(pokerPlayer, never()).addRequestedBuyInAmount(Mockito.anyInt());
        verify(state, never()).playerIsSittingIn(playerId);
        verify(reserveCallback).requestFailed(Mockito.any(ReserveFailedResponse.class));
    }
}
