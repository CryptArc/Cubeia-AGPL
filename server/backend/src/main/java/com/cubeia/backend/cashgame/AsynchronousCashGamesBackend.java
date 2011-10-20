package com.cubeia.backend.cashgame;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.SynchronousCashGamesBackend;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseTableFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;

public class AsynchronousCashGamesBackend implements CashGamesBackend {

	private SynchronousCashGamesBackend backingImpl;
	private ExecutorService executor;

	public AsynchronousCashGamesBackend(SynchronousCashGamesBackend backingImpl, ExecutorService executor) {
		this.backingImpl = backingImpl;
		this.executor = executor;
	}

	public void announceTable(final AnnounceTableRequest request, final AnnounceTableCallback callback) {
		executor.submit(new Callable<Void>() {
			public Void call() {
				processAnnounceTable(request, callback);
				return null;
			}
		});
	}

	private void processAnnounceTable(AnnounceTableRequest request, AnnounceTableCallback callback) {
		try {
			AnnounceTableResponse response = backingImpl.announceTable(request);
			callback.requestSucceded(response);
		} catch (AnnounceTableFailedException e) {
			AnnounceTableFailedResponse.ErrorCode errorCode = e.getErrorCode();
			String message = e.getMessage();
			callback.requestFailed(new AnnounceTableFailedResponse(errorCode, message));
		} catch (Throwable t) {
			AnnounceTableFailedResponse.ErrorCode errorCode = null;
			String message = t.getMessage();
			callback.requestFailed(new AnnounceTableFailedResponse(errorCode, message));
		}
	}

	public void closeTable(CloseTableRequest request) throws CloseTableFailedException {
		backingImpl.closeTable(request);
	}

	public void openSession(final OpenSessionRequest request, final OpenSessionCallback callback) {
		executor.submit(new Callable<Void>() {
			public Void call() {
				processOpenSession(request, callback);
				return null;
			}
		});
	}

	private void processOpenSession(OpenSessionRequest request, OpenSessionCallback callback) {
		try {
			OpenSessionResponse response = backingImpl.openSession(request);
			callback.requestSucceded(response);
		} catch (OpenSessionFailedException e) {
			OpenSessionFailedResponse.ErrorCode errorCode = e.getErrorCode();
			String message = e.getMessage();
			callback.requestFailed(new OpenSessionFailedResponse(errorCode, message));
		} catch (Throwable t) {
			OpenSessionFailedResponse.ErrorCode errorCode = null;
			String message = t.getMessage();
			callback.requestFailed(new OpenSessionFailedResponse(errorCode, message));
		}
	}
	
	public void closeSession(CloseSessionRequest request) throws CloseSessionFailedException {
		backingImpl.closeSession(request);
	}

	public void reserve(final ReserveRequest request, final ReserveCallback callback) {
		executor.submit(new Callable<Void>() {
			public Void call() {
				processReserve(request, callback);
				return null;
			}
		});
	}
	
	private void processReserve(ReserveRequest request, ReserveCallback callback) {
		try {
			ReserveResponse response = backingImpl.reserve(request);
			callback.requestSucceded(response);
		} catch (ReserveFailedException e) {
			ReserveFailedResponse.ErrorCode errorCode = e.getErrorCode();
			String message = e.getMessage();
			callback.requestFailed(new ReserveFailedResponse(request.playerSessionId, errorCode, message));
		} catch (Throwable t) {
			ReserveFailedResponse.ErrorCode errorCode = null;
			String message = t.getMessage();
			callback.requestFailed(new ReserveFailedResponse(request.playerSessionId, errorCode, message));
		}
	}
	
	public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException {
		return backingImpl.batchHand(request);
	}

	public long getMainAccountBalance(int playerId) throws GetBalanceFailedException {
		return backingImpl.getMainAccountBalance(playerId);
	}

	public BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException {
		return backingImpl.getSessionBalance(sessionId);
	}
}