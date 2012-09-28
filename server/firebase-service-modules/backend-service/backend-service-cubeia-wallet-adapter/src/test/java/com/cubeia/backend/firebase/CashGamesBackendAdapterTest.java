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

package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.*;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionEntry;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionResult;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.games.poker.common.Money;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.cubeia.backend.firebase.CashGamesBackendAdapter.GAME_ID;
import static com.cubeia.backend.firebase.CashGamesBackendAdapter.LICENSEE_ID;
import static com.cubeia.backend.firebase.CashGamesBackendContract.MARKET_TABLE_REFERENCE_KEY;
import static com.cubeia.backend.firebase.CashGamesBackendContract.MARKET_TABLE_SESSION_REFERENCE_KEY;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.*;

public class CashGamesBackendAdapterTest {

    private CashGamesBackendAdapter backend;
    @Mock
    private AccountLookupUtil accountLookupUtil;
    @Mock
    private WalletServiceContract walletService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CashGamesBackendAdapter.CALLBACK_GRACE_DELAY_MS = 0;
        backend = new CashGamesBackendAdapter();
        backend.accountLookupUtil = accountLookupUtil;
        backend.walletService = walletService;
        backend.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Test
    public void testGenerateHandId() {
        String handId = backend.generateHandId();
        assertThat(handId, notNullValue());
        UUID handIdUUID = UUID.fromString(handId);
        assertThat(handIdUUID.toString(), is(handId));
    }

    @Test
    public void testIsSystemShuttingDown() {
        assertThat(backend.isSystemShuttingDown(), is(false));
    }

    @Test
    public void testAllowJoinTable() {
        int playerId = 1235;
        AllowJoinResponse resp = backend.allowJoinTable(playerId);
        assertThat(resp.allowed, is(true));
        assertThat(resp.responseCode, is(-1));
    }

    @Test
    public void testGetCallbackFactory() {
        FirebaseCallbackFactory callbackFactory = backend.getCallbackFactory();
        assertThat(callbackFactory, notNullValue());
    }

    @Test
    public void testAnnounceTable() {
        int platformTableId = 1337;
        AnnounceTableRequest request = new AnnounceTableRequest(platformTableId);
        AnnounceTableCallback callback = mock(AnnounceTableCallback.class);

        backend.announceTable(request, callback);

        waitForCallback();

        ArgumentCaptor<AnnounceTableResponse> responseCaptor = ArgumentCaptor.forClass(AnnounceTableResponse.class);
        verify(callback).requestSucceeded(responseCaptor.capture());

        AnnounceTableResponse response = responseCaptor.getValue();
        assertThat(response.getTableId(), notNullValue());
        assertThat(response.getProperty(MARKET_TABLE_REFERENCE_KEY), containsString("CUBEIA-TABLE-ID::"));
    }

    @Test
    public void testOpenSession() {
        int playerId = 3434;
        int tableIdInt = 8888;
        TableId tableId = new TableIdImpl(tableIdInt);
        Money openingBalance = new Money(100, "EUR", 2);
        int roundNumber = 4;
        OpenSessionRequest request = new OpenSessionRequest(playerId, tableId, openingBalance, roundNumber);
        OpenSessionCallback callback = mock(OpenSessionCallback.class);

        long walletSessionId = 12234444L;
        when(walletService.startSession(openingBalance.getCurrencyCode(), LICENSEE_ID,
                playerId, tableIdInt, GAME_ID, "unknown-" + playerId)).thenReturn(walletSessionId);

        backend.openSession(request, callback);

        waitForCallback();

        ArgumentCaptor<OpenSessionResponse> responseCaptor = ArgumentCaptor.forClass(OpenSessionResponse.class);
        verify(callback).requestSucceeded(responseCaptor.capture());

        OpenSessionResponse response = responseCaptor.getValue();
        PlayerSessionIdImpl playerSessionIdImpl = (PlayerSessionIdImpl) response.getSessionId();
        assertThat(playerSessionIdImpl.getPlayerId(), is(playerId));
        assertThat(playerSessionIdImpl.getSessionId(), is(walletSessionId));
    }

    @Test
    public void testCloseSession() {

        int playerId = 5555;
        long sessionId = 3939393L;
        PlayerSessionIdImpl playerSessionId = new PlayerSessionIdImpl(playerId, sessionId);
        int roundNumber = 43;
        CloseSessionRequest request = new CloseSessionRequest(playerSessionId, roundNumber);

        backend.closeSession(request);

        verify(walletService).endSessionAndDepositAll(Mockito.eq(LICENSEE_ID), Mockito.eq(sessionId),
                Mockito.anyString());
    }

    @Test
    public void testReserve() {
        int playerId = 5555;
        long sessionId = 3939393L;
        PlayerSessionIdImpl playerSessionId = new PlayerSessionIdImpl(playerId, sessionId);
        int roundNumber = 43;
        Money amount = new Money(223, "EUR", 2);
        com.cubeia.backoffice.accounting.api.Money walletAmount = new com.cubeia.backoffice.accounting.api.Money("EUR", 2, new BigDecimal("2.23"));
        ReserveRequest request = new ReserveRequest(playerSessionId, roundNumber, amount);
        ReserveCallback callback = mock(ReserveCallback.class);

        AccountBalanceResult sessionBalance = mock(AccountBalanceResult.class);
        com.cubeia.backoffice.accounting.api.Money sessionBalanceMoney = new com.cubeia.backoffice.accounting.api.Money("EUR", 2, new BigDecimal("500"));
        when(sessionBalance.getBalance()).thenReturn(sessionBalanceMoney);
        when(walletService.getBalance(sessionId)).thenReturn(sessionBalance);

        backend.reserve(request, callback);

        waitForCallback();

        verify(walletService).withdraw(Mockito.eq(walletAmount), Mockito.eq(LICENSEE_ID),
                Mockito.eq(sessionId), Mockito.anyString());

        ArgumentCaptor<ReserveResponse> responseCaptor = ArgumentCaptor.forClass(ReserveResponse.class);
        verify(callback).requestSucceeded(responseCaptor.capture());
        ReserveResponse response = responseCaptor.getValue();
        assertThat(response.getPlayerSessionId(), is((PlayerSessionId) playerSessionId));
        assertThat(response.getAmountReserved().getAmount(), is(amount.getAmount()));
        assertThat(response.getBalanceUpdate().getBalance().getAmount(), is(50000L));
        assertThat(response.getReserveProperties().get(MARKET_TABLE_SESSION_REFERENCE_KEY), containsString("CUBEIA-MARKET-SID-"));
    }

    @Test
    public void testBatchHand() throws BatchHandFailedException, SystemException {
        backend.rakeAccountId = -5000;
        String handId = "xyx";
        TableId tableId = new TableIdImpl(344);
        Money totalRake = money(1000);
        BatchHandRequest request = new BatchHandRequest(handId, tableId, totalRake);

        int player1Id = 1001;
        long session1Id = 1002L;
        PlayerSessionIdImpl playerSession1 = new PlayerSessionIdImpl(player1Id, session1Id);
        int player2Id = 2001;
        long session2Id = 2002L;
        PlayerSessionIdImpl playerSession2 = new PlayerSessionIdImpl(player2Id, session2Id);

        HandResult handResult1 = new HandResult(playerSession1, money(5000), money(10000 - 1000), money(1000), 1, money(5000));
        HandResult handResult2 = new HandResult(playerSession2, money(5000), money(0), money(0), 2, money(5000));

        request.addHandResult(handResult1);
        request.addHandResult(handResult2);

        ArgumentCaptor<TransactionRequest> txCaptor = ArgumentCaptor.forClass(TransactionRequest.class);
        TransactionResult txResult = mock(TransactionResult.class);
        AccountBalanceResult sessionBalance1 = new AccountBalanceResult(session1Id, walletMoney("11.11"));
        AccountBalanceResult sessionBalance2 = new AccountBalanceResult(session2Id, walletMoney("22.22"));
        AccountBalanceResult rakeAccountBalance = new AccountBalanceResult(backend.rakeAccountId, walletMoney("1232322.22"));
        when(txResult.getBalances()).thenReturn(Arrays.asList(sessionBalance1, sessionBalance2, rakeAccountBalance));

        when(walletService.doTransaction(txCaptor.capture())).thenReturn(txResult);

        BatchHandResponse batchHandResponse = backend.batchHand(request);

        TransactionRequest txRequest = txCaptor.getValue();
        Collection<TransactionEntry> txEntries = txRequest.getEntries();
        assertThat(txEntries.size(), is(3));
        assertThat(findEntryByAccountId(session1Id, txEntries).getAmount().getAmount(), is(new BigDecimal("40.00")));
        assertThat(findEntryByAccountId(session2Id, txEntries).getAmount().getAmount(), is(new BigDecimal("-50.00")));
        assertThat(findEntryByAccountId(backend.rakeAccountId, txEntries).getAmount().getAmount(), is(new BigDecimal("10.00")));

        assertThat(batchHandResponse.getResultingBalances().size(), is(2));
        assertThat(batchHandResponse.getResultingBalances().get(0).getBalance().getPlayerSessionId(), is((PlayerSessionId) playerSession1));
        assertThat(batchHandResponse.getResultingBalances().get(0).getBalance().getBalance().getAmount(), is(1111L));
        assertThat(batchHandResponse.getResultingBalances().get(1).getBalance().getPlayerSessionId(), is((PlayerSessionId) playerSession2));
        assertThat(batchHandResponse.getResultingBalances().get(1).getBalance().getBalance().getAmount(), is(2222L));
    }

    private TransactionEntry findEntryByAccountId(Long accountId, Collection<TransactionEntry> entries) {
        for (TransactionEntry e : entries) {
            if (accountId.equals(e.getAccountId())) {
                return e;
            }
        }
        return null;
    }


    /**
     * Creates a default money object with the given amount
     */
    private Money money(long amount) {
        return new Money(amount, "EUR", 2);
    }

    private com.cubeia.backoffice.accounting.api.Money walletMoney(String amount) {
        return new com.cubeia.backoffice.accounting.api.Money("EUR", 2, new BigDecimal(amount));
    }

    /*@Test
    public void testGetMainAccountBalance() {
        long mainAccountBalance = backend.getMainAccountBalance(434).getAmount();
        // note: not implemented, always 500000 
        assertThat(mainAccountBalance, is(500000L));
    }*/

    @Test
    public void testGetSessionBalance() throws GetBalanceFailedException {
        long sessionId = 3939393L;
        int playerId = 3939;
        PlayerSessionIdImpl playerSessionId = new PlayerSessionIdImpl(playerId, sessionId);

        com.cubeia.backoffice.accounting.api.Money balance = new com.cubeia.backoffice.accounting.api.Money(
                "SEK", 2, new BigDecimal("343434"));
        AccountBalanceResult sessionBalance = new AccountBalanceResult(sessionId, balance);
        when(walletService.getBalance(sessionId)).thenReturn(sessionBalance);

        BalanceUpdate balanceUpdate = backend.getSessionBalance(playerSessionId);
        assertThat(balanceUpdate.getBalance(), is(new Money(34343400L, "SEK", 2)));
        assertThat(balanceUpdate.getPlayerSessionId(), is((PlayerSessionId) playerSessionId));
    }

    @Test
    public void testInit() throws SystemException {
        ServiceContext con = mock(ServiceContext.class);
        ServiceRegistry serviceRegistry = mock(ServiceRegistry.class);
        when(con.getParentRegistry()).thenReturn(serviceRegistry);
        when(serviceRegistry.getServiceInstance(WalletServiceContract.class)).thenReturn(walletService);

        backend.init(con);

        verify(accountLookupUtil).lookupRakeAccountId(walletService);
    }

    private void waitForCallback() {
        backend.executor.shutdown();
        try {
            backend.executor.awaitTermination(2, SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
