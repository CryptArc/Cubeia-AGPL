package com.cubeia.backend.cashgame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.Money;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;


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
		assertEquals(tableId, ((AnnounceTableResponse) response).tableId);
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
		assertEquals("no fun", announceTableFailedResponse.message);
		assertEquals(AnnounceTableFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID, announceTableFailedResponse.errorCode);
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
		assertEquals(playerSessionId, openSessionResponse.sessionId);
		assertEquals(1, openSessionResponse.sessionProperties.size());
		assertEquals("MAGIC_VALUE", openSessionResponse.sessionProperties.get("MAGIC_KEY"));
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
		assertEquals("fail fail fail", openSessionFailedResponse.message);
		assertEquals(OpenSessionFailedResponse.ErrorCode.UNKOWN_PLATFORM_TABLE_ID, openSessionFailedResponse.errorCode);

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

		assertEquals(amountReserved, reserveResponse.amountReserved);
		assertEquals(balanceVersionNumber, reserveResponse.balanceUpdate.balanceVersionNumber);
		assertEquals(newBalance, reserveResponse.balanceUpdate.balance);
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

		assertEquals("fail reserve", reserveFailedResponse.message);
		assertEquals(ReserveFailedResponse.ErrorCode.UNSPECIFIED_FAILURE, reserveFailedResponse.errorCode);
		assertTrue(reserveFailedResponse.playerSessionNeedsToBeClosed);
	}
}
