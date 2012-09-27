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

import com.cubeia.backend.cashgame.AllowJoinResponse;
import com.cubeia.backend.cashgame.LongTransactionId;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.TableIdImpl;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.impl.FirebaseCallbackFactoryImpl;
import com.cubeia.backend.firebase.jmx.MockController;
import com.cubeia.backoffice.accounting.api.UnbalancedTransactionException;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionResult;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.games.poker.common.Money;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.network.wallet.firebase.domain.TransactionBuilder;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse.ErrorCode.UNSPECIFIED_ERROR;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Adapter from the Backend Service Contract to the Cubeia Wallet Service.
 *
 * @author w
 */
public class CashGamesBackendAdapter implements CashGamesBackendContract, Service, RoutableService {

    /**
     * Hardcoded licensee id, should be part of the open session request
     */
    public static final int LICENSEE_ID = 0;

    /**
     * Hardcoded game id, should be configurable or part of requests
     */
    public static final int GAME_ID = 1;

    static final Long RAKE_ACCOUNT_USER_ID = -1000L;

    private Logger log = LoggerFactory.getLogger(CashGamesBackendAdapter.class);

    @VisibleForTesting
    protected ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);

    private ServiceRouter router;

    private final AtomicLong idSequence = new AtomicLong(0);

    @VisibleForTesting
    protected WalletServiceContract walletService;

    protected AccountLookupUtil accountLookupUtil = new AccountLookupUtil();

    @VisibleForTesting
    protected long rakeAccountId;

    /**
     * Grace delay before callback is called. If called immediately some messages seems to be dropped. :-(
     */
    @VisibleForTesting
    protected static int CALLBACK_GRACE_DELAY_MS = 500;

    private long nextId() {
        return idSequence.getAndIncrement();
    }

    @Override
    public String generateHandId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean isSystemShuttingDown() {
        log.warn("shutting down check not implemented");
        return false;
    }

    @Override
    public AllowJoinResponse allowJoinTable(int playerId) {
        log.warn("allow join not implemented, will always return ok");
        return new AllowJoinResponse(true, -1);
    }

    @Override
    public FirebaseCallbackFactory getCallbackFactory() {
        return new FirebaseCallbackFactoryImpl(router);
    }

    @Override
    public void announceTable(AnnounceTableRequest request, final AnnounceTableCallback callback) {
        final AnnounceTableResponse response = new AnnounceTableResponse(new TableIdImpl());
        response.setProperty(MARKET_TABLE_REFERENCE_KEY, "CUBEIA-TABLE-ID::" + UUID.randomUUID());

        Runnable announceTask = new Runnable() {
            @Override
            public void run() {
                callback.requestSucceeded(response);
            }
        };
        scheduleCallback(announceTask);
    }

    private void scheduleCallback(Runnable runnable) {
        executor.schedule(wrapWithTryCatch(runnable), CALLBACK_GRACE_DELAY_MS, MILLISECONDS);
    }

    @Override
    public void openSession(final OpenSessionRequest request, final OpenSessionCallback callback) {
        Runnable openSessionTask = new Runnable() {
            @Override
            public void run() {
                try {
                    Long walletSessionId = walletService.startSession(
                            request.getOpeningBalance().getCurrencyCode(),
                            LICENSEE_ID,
                            request.getPlayerId(),
                            (int) ((TableIdImpl) request.getTableId()).getId(),
                            GAME_ID,
                            "unknown-" + request.getPlayerId());

                    PlayerSessionId sessionId = new PlayerSessionIdImpl(request.getPlayerId(), walletSessionId);
                    OpenSessionResponse response = new OpenSessionResponse(sessionId, Collections.<String, String>emptyMap());
                    log.debug("new session opened, tId = {}, pId = {}, sId = {}",
                            new Object[]{request.getTableId(), request.getPlayerId(), response.getSessionId()});
                    callback.requestSucceeded(response);
                } catch (Exception e) {
                    String msg = "error opening session for player " + request.getPlayerId() + ": "
                            + e.getMessage();
                    OpenSessionFailedResponse failResponse = new OpenSessionFailedResponse(UNSPECIFIED_ERROR, msg, request.getPlayerId());
                    callback.requestFailed(failResponse);
                }
            }
        };

        scheduleCallback(openSessionTask);
    }

    @Override
    public void closeSession(CloseSessionRequest request) {
        PlayerSessionId sid = request.getPlayerSessionId();

        long walletSessionId = getWalletSessionIdByPlayerSessionId(sid);

        com.cubeia.backoffice.accounting.api.Money amountDeposited = walletService
                .endSessionAndDepositAll(LICENSEE_ID, walletSessionId, "session ended by game " + GAME_ID +
                        ", player id = " + sid.getPlayerId());

        log.debug("wallet session {} closed for player {}, amount deposited: {}",
                new Object[]{walletSessionId, sid.getPlayerId(), amountDeposited});
    }

    private long getWalletSessionIdByPlayerSessionId(PlayerSessionId sid) {
        return ((PlayerSessionIdImpl) sid).getSessionId();
    }

    @Override
    public void reserve(final ReserveRequest request, final ReserveCallback callback) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Money amount = request.getAmount();
                PlayerSessionId sid = request.getPlayerSessionId();
                Long walletSessionId = getWalletSessionIdByPlayerSessionId(sid);

                com.cubeia.backoffice.accounting.api.Money walletAmount = convertToWalletMoney(amount);
                try {
                    walletService.withdraw(walletAmount, LICENSEE_ID, walletSessionId.longValue(),
                            "reserve " + amount + " to game " + GAME_ID + " by player " + sid.getPlayerId());

                    AccountBalanceResult sessionBalance = walletService.getBalance(walletSessionId);
                    Money newBalance = convertFromWalletMoney(sessionBalance.getBalance());

                    BalanceUpdate balanceUpdate = new BalanceUpdate(request.getPlayerSessionId(), newBalance, nextId());
                    ReserveResponse response = new ReserveResponse(balanceUpdate, amount);
                    log.debug("reserve successful: sId = {}, amount = {}, new balance = {}", new Object[]{sid, amount, newBalance});
                    response.setProperty(MARKET_TABLE_SESSION_REFERENCE_KEY, "CUBEIA-MARKET-SID-" + sid.hashCode());
                    callback.requestSucceeded(response);
                } catch (Exception e) {
                    String msg = "error reserving " + amount + " to session " + walletSessionId + " for player " + sid.getPlayerId() + ": "
                            + e.getMessage();

                    ReserveFailedResponse failResponse = new ReserveFailedResponse(request.getPlayerSessionId(),
                            ReserveFailedResponse.ErrorCode.UNSPECIFIED_FAILURE, msg, true);
                    callback.requestFailed(failResponse);
                }
            }
        };

        scheduleCallback(task);
    }

    /**
     * Convert from wallet money type to backend money type.
     *
     * @param amount wallet money amount
     * @return converted amount
     */
    private Money convertFromWalletMoney(com.cubeia.backoffice.accounting.api.Money amount) {
        Money backendMoney = new Money(amount.getAmount().movePointRight(amount.getFractionalDigits()).longValueExact(),
                amount.getCurrencyCode(), amount.getFractionalDigits());
        return backendMoney;
    }

    /**
     * Convert from backend money type to wallet money type.
     *
     * @param amount amount to convert
     * @return converted amount
     */
    private com.cubeia.backoffice.accounting.api.Money convertToWalletMoney(Money amount) {
        return new com.cubeia.backoffice.accounting.api.Money(amount.getCurrencyCode(), amount.getFractionalDigits(),
                new BigDecimal(amount.getAmount()).movePointLeft(amount.getFractionalDigits()));
    }

    @Override
    public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException {

        try {
            String currencyCode = request.getTotalRake().getCurrencyCode();
            int fractionalDigits = request.getTotalRake().getFractionalDigits();
            TransactionBuilder txBuilder = new TransactionBuilder(currencyCode, fractionalDigits);

            HashMap<Long, PlayerSessionIdImpl> sessionToPlayerSessionMap = new HashMap<Long, PlayerSessionIdImpl>();

            for (HandResult hr : request.getHandResults()) {
                log.debug("recording hand result: handId = {}, sessionId = {}, bets = {}, wins = {}, rake = {}",
                        new Object[]{request.getHandId(), hr.getPlayerSession(), hr.getAggregatedBet(), hr.getWin(), hr.getRake()});

                Money resultingAmount = hr.getWin().subtract(hr.getAggregatedBet());

                Long walletSessionId = getWalletSessionIdByPlayerSessionId(hr.getPlayerSession());
                sessionToPlayerSessionMap.put(walletSessionId, (PlayerSessionIdImpl) hr.getPlayerSession());
                txBuilder.entry(walletSessionId, convertToWalletMoney(resultingAmount).getAmount());
            }

            txBuilder.entry(rakeAccountId, convertToWalletMoney(request.getTotalRake()).getAmount());
            txBuilder.comment("poker hand result"); //: game = " + GAME_ID + ", hand id = " + request.getHandId() + ", table id = " + request.getTableId());
            txBuilder.attribute("pokerTableId", String.valueOf(((TableIdImpl)request.getTableId()).getId()))
            			.attribute("pokerGameId", String.valueOf(GAME_ID))
            			.attribute("pokerHandId", request.getHandId());
            
            TransactionRequest txRequest = txBuilder.toTransactionRequest();

            log.debug("sending tx request to wallet: {}", txRequest);
            TransactionResult txResult = walletService.doTransaction(txRequest);

            List<TransactionUpdate> resultingBalances = new ArrayList<TransactionUpdate>();
            for (AccountBalanceResult sb : txResult.getBalances()) {
                if (sb.getAccountId() != rakeAccountId) {
                    PlayerSessionIdImpl playerSessionId = sessionToPlayerSessionMap.get(sb.getAccountId());
                    Money balance = convertFromWalletMoney(sb.getBalance());
                    BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId, balance, nextId());
                    resultingBalances.add(new TransactionUpdate(new LongTransactionId(txResult.getTransactionId()), balanceUpdate));
                }
            }
            return new BatchHandResponse(resultingBalances);

        } catch (UnbalancedTransactionException ute) {
            throw new BatchHandFailedException("error reporting hand result", ute);
        } catch (Exception e) {
            throw new BatchHandFailedException("error reporting hand result", e);
        }
    }

    @Override
    public Money getMainAccountBalance(int playerId) {
    	long accountId = this.accountLookupUtil.lookupMainAccountIdForPLayer(walletService, playerId);
    	log.debug("Found account ID {} for player {}", accountId, playerId);
    	Money m = convertFromWalletMoney(walletService.getBalance(accountId).getBalance());
    	log.debug("Found balance {} for player {}", m, playerId);
    	return m;
    }

    @Override
    public BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException {
        AccountBalanceResult sessionBalance = walletService.getBalance(getWalletSessionIdByPlayerSessionId(sessionId));
        Money balanceMoney = convertFromWalletMoney(sessionBalance.getBalance());
        return new BalanceUpdate(sessionId, balanceMoney, nextId());
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    public ServiceRouter getRouter() {
        return router;
    }

    @Override
    public void onAction(ServiceAction e) {
        // nothing should arrive here
    }

    /**
     * Wrap the given runnable with a catch all block.
     */
    public Runnable wrapWithTryCatch(final Runnable delegateRunnable) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    delegateRunnable.run();
                } catch (Throwable t) {
                    log.error("wrapped runnable throw uncaught throwable", t);
                }
            }
        };
    }

    @SuppressWarnings("unused")
    @Override
    public void init(ServiceContext con) throws SystemException {
        log.debug("backend wallet adapter service init");

        walletService = con.getParentRegistry().getServiceInstance(WalletServiceContract.class);
        log.debug("found wallet service: {}", walletService.getClass().getSimpleName());

        rakeAccountId = accountLookupUtil.lookupRakeAccountId(walletService);
        log.debug("system rake account id = {}", rakeAccountId);

        log.debug("initializing jmx stuff");
        MockController mockController = new MockController(this);
    }

    @Override
    public void destroy() {
        log.debug("backend wallet adapter service  destroy");
    }

    @Override
    public void start() {
        log.debug("backend wallet adapter service  start");
    }

    @Override
    public void stop() {
        log.debug("backend wallet adapter service  stop");
    }

}
