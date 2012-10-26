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

import com.cubeia.backend.cashgame.Asynchronous;
import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.dto.AllowJoinResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.games.poker.common.Money;

public interface AsynchronousCashGamesBackend {

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    String generateHandId();

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    AllowJoinResponse allowJoinTable(int playerId);

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    boolean isSystemShuttingDown();

    
    /**
     * This is an asynchronous call, the response
     * will be sent as object action to the table. 
     * 
     * <p>See {@link CashGamesBackend} for more 
     * documentation.</p> 
     */
    @Asynchronous
    void announceTable(AnnounceTableRequest request);

    /**
     * This is an asynchronous call, the response
     * will be sent as object action to the table. 
     * 
     * <p>See {@link CashGamesBackend} for more 
     * documentation.</p> 
     */
    @Asynchronous
    void openSession(OpenSessionRequest request);

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

    /**
     * This is an asynchronous call, the response
     * will be sent as object action to the table. 
     * 
     * <p>See {@link CashGamesBackend} for more 
     * documentation.</p> 
     */
    @Asynchronous
    void reserve(ReserveRequest request);

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

    /**
     * See {@link CashGamesBackend} for documentation. 
     */
    Money getMainAccountBalance(int playerId) throws GetBalanceFailedException;

    /**
     * See {@link CashGamesBackend} for documentation. 
     */    
    BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException;

}
