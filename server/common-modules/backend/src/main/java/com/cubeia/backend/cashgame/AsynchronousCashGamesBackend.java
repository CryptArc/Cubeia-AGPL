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

import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.exceptions.*;
import com.cubeia.games.poker.common.Money;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class AsynchronousCashGamesBackend implements CashGamesBackend {

    private static final long DELAY_BEFORE_ANNOUNCING_TABLE = 1000L;
    private SynchronousCashGamesBackend backingImpl;
    private ExecutorService executor;

    private final Logger log = Logger.getLogger(getClass());

    public AsynchronousCashGamesBackend(SynchronousCashGamesBackend backingImpl, ExecutorService executor) {
        this.backingImpl = backingImpl;
        this.executor = executor;
    }

    @Override
    public String generateHandId() {
        return backingImpl.generateHandId();
    }

    @Override
    public boolean isSystemShuttingDown() {
        return backingImpl.isSystemShuttingDown();
    }

    @Override
    public AllowJoinResponse allowJoinTable(int playerId) {
        return backingImpl.allowJoinTable(playerId);
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
            Thread.sleep(DELAY_BEFORE_ANNOUNCING_TABLE);
            AnnounceTableResponse response = backingImpl.announceTable(request);
            log.debug("processAnnounceTable got response: " + response.toString());
            callback.requestSucceeded(response);
        } catch (AnnounceTableFailedException e) {
            log.error("failed to announce table", e);
            AnnounceTableFailedResponse.ErrorCode errorCode = e.getErrorCode();
            String message = e.getMessage();
            callback.requestFailed(new AnnounceTableFailedResponse(errorCode, message));
        } catch (Throwable t) {
            log.error("failed to announce table", t);
            AnnounceTableFailedResponse.ErrorCode errorCode = null;
            String message = t.getMessage();
            callback.requestFailed(new AnnounceTableFailedResponse(errorCode, message));
        }
    }

    /*public void closeTable(CloseTableRequest request) throws CloseTableFailedException {
         backingImpl.closeTable(request);
     }*/

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
            callback.requestSucceeded(response);
        } catch (OpenSessionFailedException e) {
            log.error("failed to open session", e);
            OpenSessionFailedResponse.ErrorCode errorCode = e.getErrorCode();
            String message = e.getMessage();
            callback.requestFailed(new OpenSessionFailedResponse(errorCode, message, request.getPlayerId()));
        } catch (Throwable t) {
            log.error("failed to open session", t);
            OpenSessionFailedResponse.ErrorCode errorCode = null;
            String message = t.getMessage();
            callback.requestFailed(new OpenSessionFailedResponse(errorCode, message, request.getPlayerId()));
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
            callback.requestSucceeded(response);
        } catch (ReserveFailedException e) {
            log.error("failed to reserve", e);
            ReserveFailedResponse.ErrorCode errorCode = e.getErrorCode();
            String message = e.getMessage();
            callback.requestFailed(new ReserveFailedResponse(request.getPlayerSessionId(), errorCode, message, e.playerSessionNeedsToBeClosed));
        } catch (Throwable t) {
            log.error("failed to reserve (unhandled error)", t);
            ReserveFailedResponse.ErrorCode errorCode = null;
            String message = t.getMessage();
            callback.requestFailed(new ReserveFailedResponse(request.getPlayerSessionId(), errorCode, message, true));
        }
    }

    public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException {
        return backingImpl.batchHand(request);
    }

    public Money getMainAccountBalance(int playerId) throws GetBalanceFailedException {
        return backingImpl.getMainAccountBalance(playerId);
    }

    public BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException {
        return backingImpl.getSessionBalance(sessionId);
    }
}
