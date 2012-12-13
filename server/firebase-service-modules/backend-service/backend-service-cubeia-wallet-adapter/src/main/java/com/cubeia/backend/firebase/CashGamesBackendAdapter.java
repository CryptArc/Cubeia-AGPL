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

import static com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse.ErrorCode.UNSPECIFIED_ERROR;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.TransactionId;
import com.cubeia.backend.cashgame.dto.AllowJoinResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.HandResult;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.dto.TransactionUpdate;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;
import com.cubeia.backoffice.accounting.api.UnbalancedTransactionException;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionResult;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.games.poker.common.Money;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.network.wallet.firebase.domain.TransactionBuilder;
import com.google.common.annotations.VisibleForTesting;

/**
 * Adapter from the Backend Service Contract to the Cubeia Wallet Service.
 * 
 * @author w
 */
public class CashGamesBackendAdapter implements CashGamesBackend {

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

	private final AtomicLong idSequence = new AtomicLong(0);

	@VisibleForTesting
	protected WalletServiceContract walletService;

	protected AccountLookupUtil accountLookupUtil;

	@VisibleForTesting
	protected long rakeAccountId;
	
	public CashGamesBackendAdapter(WalletServiceContract walletService, AccountLookupUtil accountLookupUtil) throws SystemException {
		this.walletService = walletService;
		rakeAccountId = accountLookupUtil.lookupRakeAccountId(walletService);
		log.debug("system rake account id = {}", rakeAccountId);
		this.accountLookupUtil = accountLookupUtil;
	}
	
	@Override
	public boolean isSystemShuttingDown() {
		return false;
	}

	private long nextId() {
		return idSequence.getAndIncrement();
	}

	@Override
	public String generateHandId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public AllowJoinResponse allowJoinTable(int playerId) {
		log.trace("allow join not implemented, will always return ok");
		return new AllowJoinResponse(true, -1);
	}

	@Override
	public AnnounceTableResponse announceTable(AnnounceTableRequest request) {
		String extid = UUID.randomUUID().toString();
		final AnnounceTableResponse response = new AnnounceTableResponse(new TableId(request.tableId, extid));
		response.setProperty(
				CashGamesBackendService.MARKET_TABLE_REFERENCE_KEY,
				"CUBEIA-TABLE-ID::" + extid);
		return response;
	}

	@Override
	public OpenSessionResponse openSession(final OpenSessionRequest request) throws OpenSessionFailedException {
		try {
			Long walletSessionId = walletService.startSession(request
					.getOpeningBalance().getCurrencyCode(), LICENSEE_ID,
					request.getPlayerId(), String.valueOf(request.getTableId().tableId),
					GAME_ID, "unknown-" + request.getPlayerId());

			PlayerSessionId sessionId = new PlayerSessionId(request.playerId, String.valueOf(walletSessionId));
			OpenSessionResponse response = new OpenSessionResponse(sessionId, Collections.<String, String> emptyMap());
			log.debug("new session opened, tId = {}, pId = {}, sId = {}", new Object[] { request.getTableId(), request.getPlayerId(), response.getSessionId() });
			return response;
		} catch (Exception e) {
			String msg = "error opening session for player " + request.getPlayerId() + ": " + e.getMessage();
			throw new OpenSessionFailedException(msg, UNSPECIFIED_ERROR);
		}
	}

	@Override
	public void closeSession(CloseSessionRequest request) {
		PlayerSessionId sid = request.getPlayerSessionId();
		long walletSessionId = getWalletSessionIdByPlayerSessionId(sid);
		com.cubeia.backoffice.accounting.api.Money amountDeposited = walletService
				.endSessionAndDepositAll(LICENSEE_ID, walletSessionId,
						"session ended by game " + GAME_ID + ", player id = "
								+ sid.playerId);

		log.debug("wallet session {} closed for player {}, amount deposited: {}",
				new Object[] { walletSessionId, sid.playerId,
						amountDeposited });
	}

	private long getWalletSessionIdByPlayerSessionId(PlayerSessionId sid) {
		return Long.valueOf(sid.integrationSessionId);
	}

	@Override
	public ReserveResponse reserve(final ReserveRequest request) throws ReserveFailedException {
		Money amount = request.getAmount();
		PlayerSessionId sid = request.getPlayerSessionId();
		Long walletSessionId = getWalletSessionIdByPlayerSessionId(sid);
		com.cubeia.backoffice.accounting.api.Money walletAmount = convertToWalletMoney(amount);
		try {
			walletService.withdraw(walletAmount, LICENSEE_ID,
					walletSessionId.longValue(), "reserve " + amount
					+ " to game " + GAME_ID + " by player "
					+ sid.playerId);

			// AccountBalanceResult sessionBalance = walletService.getBalance(walletSessionId);
			// Money newBalance = convertFromWalletMoney(sessionBalance.getBalance());

			// BalanceUpdate balanceUpdate = new BalanceUpdate(request.getPlayerSessionId(), newBalance, nextId());
			ReserveResponse response = new ReserveResponse(request.getPlayerSessionId(), amount);
			log.debug("reserve successful: sId = {}, amount = {}", new Object[] { sid, amount });
			response.setProperty(CashGamesBackendService.MARKET_TABLE_SESSION_REFERENCE_KEY, "CUBEIA-MARKET-SID-" + sid.hashCode());
			return response;
		} catch (Exception e) {
			String msg = "error reserving " + amount + " to session "
							+ walletSessionId + " for player "
							+ sid.playerId + ": " + e.getMessage();

			throw new ReserveFailedException(msg, ErrorCode.UNSPECIFIED_FAILURE, true);
		}
}

	/**
	 * Convert from wallet money type to backend money type.
	 * 
	 * @param amount
	 *            wallet money amount
	 * @return converted amount
	 */
	private Money convertFromWalletMoney(com.cubeia.backoffice.accounting.api.Money amount) {
		Money backendMoney = new Money(amount.getAmount()
				.movePointRight(amount.getFractionalDigits()).longValueExact(),
				amount.getCurrencyCode(), amount.getFractionalDigits());
		return backendMoney;
	}

	/**
	 * Convert from backend money type to wallet money type.
	 * 
	 * @param amount
	 *            amount to convert
	 * @return converted amount
	 */
	private com.cubeia.backoffice.accounting.api.Money convertToWalletMoney(Money amount) {
		return new com.cubeia.backoffice.accounting.api.Money(
				amount.getCurrencyCode(), amount.getFractionalDigits(),
				new BigDecimal(amount.getAmount()).movePointLeft(amount.getFractionalDigits()));
	}

	@Override
	public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException {

		try {
			String currencyCode = request.getTotalRake().getCurrencyCode();
			int fractionalDigits = request.getTotalRake().getFractionalDigits();
			TransactionBuilder txBuilder = new TransactionBuilder(currencyCode, fractionalDigits);

			HashMap<Long, PlayerSessionId> sessionToPlayerSessionMap = new HashMap<Long, PlayerSessionId>();

			for (HandResult hr : request.getHandResults()) {
				log.debug("recording hand result: handId = {}, sessionId = {}, bets = {}, wins = {}, rake = {}",
						new Object[] { request.getHandId(),
								hr.getPlayerSession(), hr.getAggregatedBet(),
								hr.getWin(), hr.getRake() });

				Money resultingAmount = hr.getWin().subtract(hr.getAggregatedBet());

				Long walletSessionId = getWalletSessionIdByPlayerSessionId(hr.getPlayerSession());
				sessionToPlayerSessionMap.put(walletSessionId, hr.getPlayerSession());
				txBuilder.entry(walletSessionId, convertToWalletMoney(resultingAmount).getAmount());
			}

			txBuilder.entry(rakeAccountId, convertToWalletMoney(request.getTotalRake()).getAmount());
			txBuilder.comment("poker hand result"); 
			txBuilder.attribute("pokerTableId", String.valueOf((request.getTableId()).tableId))
					.attribute("pokerGameId", String.valueOf(GAME_ID))
					.attribute("pokerHandId", request.getHandId());

			TransactionRequest txRequest = txBuilder.toTransactionRequest();
			txRequest.getExcludeReturnBalanceForAcconds().add(rakeAccountId); // exclude the rake account
			
			log.debug("sending tx request to wallet: {}", txRequest);
			TransactionResult txResult = walletService.doTransaction(txRequest);

			List<TransactionUpdate> resultingBalances = new ArrayList<TransactionUpdate>();
			for (AccountBalanceResult sb : txResult.getBalances()) {
				if (sb.getAccountId() != rakeAccountId) { // shouldn't be needed (excluded above)
					PlayerSessionId playerSessionId = sessionToPlayerSessionMap.get(sb.getAccountId());
					Money balance = convertFromWalletMoney(sb.getBalance());
					BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId, balance, nextId());
					resultingBalances.add(new TransactionUpdate(new TransactionId(txResult.getTransactionId()),
							balanceUpdate));
				}
			}
			return new BatchHandResponse(resultingBalances);

		} catch (UnbalancedTransactionException ute) {
			throw new BatchHandFailedException("error reporting hand result",
					ute);
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
}
