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

package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;
import com.cubeia.games.poker.common.Money;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AsynchBackendTest {

    private CashGamesBackend backend;

    private SynchronousCashGamesBackend backingMock = mock(SynchronousCashGamesBackend.class);

    @Before
    public void setUp() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        backend = new AsynchronousCashGamesBackend(backingMock, executor);
    }

    @Test
    public void testAnnounceTable() throws Exception {
        AnnounceTableRequest request = new AnnounceTableRequest(
                1234);

        AnnounceTableCallbackHandler callback = new AnnounceTableCallbackHandler();

        TableId tableId = new TableIdImpl();
        when(backingMock.announceTable(any(AnnounceTableRequest.class))).thenReturn(new AnnounceTableResponse(tableId));

        backend.announceTable(request, callback);

        Object response = callback.getResponse(2000);

        assertTrue(response instanceof AnnounceTableResponse);
        assertEquals(tableId, ((AnnounceTableResponse) response).getTableId());
    }

    @Test
    public void testAnnounceTableFail() throws Exception {
        AnnounceTableRequest request = new AnnounceTableRequest(
                1234);

        AnnounceTableCallbackHandler callback = new AnnounceTableCallbackHandler();

        when(backingMock.announceTable(any(AnnounceTableRequest.class))).thenThrow(new AnnounceTableFailedException("no fun", AnnounceTableFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID));

        backend.announceTable(request, callback);

        Object response = callback.getResponse(2000);

        assertTrue(response instanceof AnnounceTableFailedResponse);
        AnnounceTableFailedResponse announceTableFailedResponse = (AnnounceTableFailedResponse) response;
        assertEquals("no fun", announceTableFailedResponse.getMessage());
        assertEquals(AnnounceTableFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID, announceTableFailedResponse.getErrorCode());
    }


    @Test
    public void testOpenSession() throws Exception {

        OpenSessionRequest request = new OpenSessionRequest(123, new TableIdImpl(),
                new Money(0, "SEK", 2), 123);

        int playerId = 42;
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);

        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("MAGIC_KEY", "MAGIC_VALUE");

        OpenSessionCallbackHandler callback = new OpenSessionCallbackHandler();

        when(backingMock.openSession(any(OpenSessionRequest.class))).thenReturn(new OpenSessionResponse(playerSessionId, propertiesMap));

        backend.openSession(request, callback);

        Object response = callback.getResponse(100);
        assertTrue(response instanceof OpenSessionResponse);
        OpenSessionResponse openSessionResponse = (OpenSessionResponse) response;
        assertEquals(playerSessionId, openSessionResponse.getSessionId());
        assertEquals(1, openSessionResponse.getSessionProperties().size());
        assertEquals("MAGIC_VALUE", openSessionResponse.getSessionProperties().get("MAGIC_KEY"));
    }

    @Test
    public void testOpenSessionFail() throws Exception {

        OpenSessionRequest request = new OpenSessionRequest(123, new TableIdImpl(),
                new Money(0, "SEK", 2), 123);

        OpenSessionCallbackHandler callback = new OpenSessionCallbackHandler();

        when(backingMock.openSession(any(OpenSessionRequest.class))).thenThrow(new OpenSessionFailedException("fail fail fail", OpenSessionFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID));

        backend.openSession(request, callback);

        Object response = callback.getResponse(100);
        assertTrue(response instanceof OpenSessionFailedResponse);
        OpenSessionFailedResponse openSessionFailedResponse = (OpenSessionFailedResponse) response;
        assertEquals("fail fail fail", openSessionFailedResponse.getMessage());
        assertEquals(OpenSessionFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID, openSessionFailedResponse.getErrorCode());

    }

    @Test
    public void testReserve() throws Exception {
        int playerId = 42;
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);

        Money amountReserved = new Money(1000, "USD", 2);
        int roundNumber = 2;
        long balanceVersionNumber = 102030;
        Money newBalance = new Money(5000, "USD", 2);

        ReserveRequest request = new ReserveRequest(playerSessionId, roundNumber, amountReserved);
        ReserveCallbackHandler callback = new ReserveCallbackHandler();

        BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId, newBalance, balanceVersionNumber);

        when(backingMock.reserve(any(ReserveRequest.class))).thenReturn(new ReserveResponse(balanceUpdate, amountReserved));

        backend.reserve(request, callback);

        Object response = callback.getResponse(100);
        assertTrue(response instanceof ReserveResponse);
        ReserveResponse reserveResponse = (ReserveResponse) response;

        assertEquals(amountReserved, reserveResponse.getAmountReserved());
        assertEquals(balanceVersionNumber, reserveResponse.getBalanceUpdate().getBalanceVersionNumber());
        assertEquals(newBalance, reserveResponse.getBalanceUpdate().getBalance());
    }

    @Test
    public void testReserveFail() throws Exception {

        int playerId = 42;
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        Money amountReserved = new Money(1000, "SEK", 2);
        int roundNumber = 2;

        ReserveRequest request = new ReserveRequest(playerSessionId, roundNumber, amountReserved);
        ReserveCallbackHandler callback = new ReserveCallbackHandler();

        when(backingMock.reserve(any(ReserveRequest.class))).thenThrow(
                new ReserveFailedException("fail reserve", ReserveFailedResponse.ErrorCode.UNSPECIFIED_FAILURE, true));

        backend.reserve(request, callback);

        Object response = callback.getResponse(100);
        assertTrue(response instanceof ReserveFailedResponse);
        ReserveFailedResponse reserveFailedResponse = (ReserveFailedResponse) response;

        assertEquals("fail reserve", reserveFailedResponse.getMessage());
        assertEquals(ReserveFailedResponse.ErrorCode.UNSPECIFIED_FAILURE, reserveFailedResponse.getErrorCode());
        assertTrue(reserveFailedResponse.isPlayerSessionNeedsToBeClosed());
    }
}
