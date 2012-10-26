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

package com.cubeia.backend.firebase.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.service.ServiceRouter;

public class FirebaseCallbackFactoryImpl implements FirebaseCallbackFactory {

    private static Logger log = LoggerFactory.getLogger(FirebaseCallbackFactoryImpl.class);

    private final ServiceRouter router;

    public FirebaseCallbackFactoryImpl(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public OpenSessionCallback createOpenSessionCallback(TableId table) {
    	final int gameId = table.gameId;
    	final int tableId = table.tableId;
    	return new OpenSessionCallback() {
            @Override
            public void requestSucceeded(OpenSessionResponse response) {
                log.debug("open session succeeded: gId = {}, tId = {}, sId = {}", new Object[]{ gameId, tableId, response.getSessionId()});
                sendGameObjectActionToTable(response.getSessionId().playerId, gameId, tableId, response);
            }

            @Override
            public void requestFailed(OpenSessionFailedResponse response) {
                log.debug("open session failed: gId = {}, tId = {}, error = {}, msg = {}", new Object[]{ gameId, tableId, response.getErrorCode(), response.getMessage()});
                sendGameObjectActionToTable(-1, gameId, tableId, response);
            }
        };
    }

    @Override
    public ReserveCallback createReserveCallback(TableId table) {
    	final int gameId = table.gameId;
    	final int tableId = table.tableId;
        return new ReserveCallback() {
            @Override
            public void requestSucceeded(ReserveResponse response) {
                log.debug("reserve succeeded: gId = {}, tId = {}, sId = {}", new Object[]{ gameId, tableId, response.getPlayerSessionId()});
                sendGameObjectActionToTable(response.getPlayerSessionId().playerId, gameId, tableId, response);
            }

            @Override
            public void requestFailed(ReserveFailedResponse response) {
                log.debug("reserve failed: gId = {}, tId = {}, error = {}, msg = {}", new Object[]{ gameId, tableId, response.getErrorCode(), response.getMessage()});
                sendGameObjectActionToTable(response.getSessionId().playerId, gameId, tableId, response);
            }
        };
    }

    @Override
    public AnnounceTableCallback createAnnounceTableCallback(TableId table) {
    	final int gameId = table.gameId;
    	final int tableId = table.tableId;
    	return new AnnounceTableCallback() {
            @Override
            public void requestSucceeded(final AnnounceTableResponse response) {
                log.debug("announce suceeded: gId = {}, tId = {}, response = {}", new Object[]{ tableId, gameId, response});
                sendGameObjectActionToTable(-1, gameId, tableId, response);
            }

            @Override
            public void requestFailed(AnnounceTableFailedResponse response) {
                log.debug("announce failed: gId = {}, tId = {}, error = {}, msg = {}", new Object[]{ gameId, tableId, response.getErrorCode(), response.getMessage()});
                sendGameObjectActionToTable(-1, gameId, tableId, response);
            }
        };
    }

    private void sendGameObjectActionToTable(int playerId, int gameId, int tableId, Object object) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(object);
        router.dispatchToGame(gameId, action);
    }
}
