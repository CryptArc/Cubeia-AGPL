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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AllowJoinResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;
import com.cubeia.backend.firebase.impl.FirebaseCallbackFactoryImpl;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.games.poker.common.Money;

/**
 * Base class for a service implementation. The implementing class
 * only needs to supply a {@link CashGamesBackend} implementation, the 
 * asynchronous calls are taken care of by this class. 
 */
public abstract class CashGamesBackendServiceBase implements CashGamesBackendService {
	
	private final long scheduleGraceDelay;
	private final ScheduledExecutorService executor;

	protected CashGamesBackendServiceBase(int numThread, long scheduleGraceDelay) {
		executor = Executors.newScheduledThreadPool(numThread);
		this.scheduleGraceDelay = scheduleGraceDelay;
	}
	
	protected abstract CashGamesBackend getCashGamesBackend();
	
	protected abstract ServiceRouter getServiceRouter();

	
	@Override
	public boolean isSystemShuttingDown() {
		return getCashGamesBackend().isSystemShuttingDown();
	}
	
	@Override
	public String generateHandId() {
		return getCashGamesBackend().generateHandId();
	}

	@Override
	public AllowJoinResponse allowJoinTable(int playerId) {
		return getCashGamesBackend().allowJoinTable(playerId);
	}

	@Override
	public void announceTable(final AnnounceTableRequest request) {
		scheduleCallback(new SafeRunnable() {
			
			@Override
			protected void execute() {
				AnnounceTableCallback callback = new FirebaseCallbackFactoryImpl(getServiceRouter()).createAnnounceTableCallback(request.tableId);
				try {
					AnnounceTableResponse resp = getCashGamesBackend().announceTable(request);
					callback.requestSucceeded(resp);
				} catch(AnnounceTableFailedException e) {
					AnnounceTableFailedResponse resp = new AnnounceTableFailedResponse(e.errorCode, e.getMessage());
					callback.requestFailed(resp);
				}
			}
		});
	}

	@Override
	public void openSession(final OpenSessionRequest request) {
		scheduleCallback(new SafeRunnable() {
			
			@Override
			protected void execute() {
				OpenSessionCallback callback = new FirebaseCallbackFactoryImpl(getServiceRouter()).createOpenSessionCallback(request.tableId);
				try {
					OpenSessionResponse resp = getCashGamesBackend().openSession(request);
					callback.requestSucceeded(resp);
				} catch(OpenSessionFailedException e) {
					OpenSessionFailedResponse err = new OpenSessionFailedResponse(e.errorCode, e.getMessage(), request.playerId);
					callback.requestFailed(err);
				}
			}
		});
	}
	
	@Override
	public void reserve(final ReserveRequest request) {
		scheduleCallback(new SafeRunnable() {
			
			@Override
			protected void execute() {
				ReserveCallback callback = new FirebaseCallbackFactoryImpl(getServiceRouter()).createReserveCallback(request.tableId);
				try {
					ReserveResponse resp = getCashGamesBackend().reserve(request);
					callback.requestSucceeded(resp);
				} catch(ReserveFailedException e) {
					ReserveFailedResponse resp = new ReserveFailedResponse(request.playerSessionId, e.errorCode, e.getMessage(), e.playerSessionNeedsToBeClosed);
					callback.requestFailed(resp);
				}
			}
		});
	}

	@Override
	public void closeSession(CloseSessionRequest request) throws CloseSessionFailedException {
		getCashGamesBackend().closeSession(request);
	}

	@Override
	public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException {
		return getCashGamesBackend().batchHand(request);
	}

	@Override
	public Money getMainAccountBalance(int playerId) throws GetBalanceFailedException {
		return getCashGamesBackend().getMainAccountBalance(playerId);
	}

	@Override
	public BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException {
		return getCashGamesBackend().getSessionBalance(sessionId);
	}
	
	
	// --- PRIVATE METHODS --- //
	
	private void scheduleCallback(Runnable runnable) {
        executor.schedule(runnable, this.scheduleGraceDelay, MILLISECONDS);
    }
}
