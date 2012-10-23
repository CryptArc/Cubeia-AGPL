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
import com.cubeia.backend.cashgame.exceptions.*;
import com.cubeia.games.poker.common.Money;

public interface SynchronousCashGamesBackend {

    String generateHandId();

    boolean isSystemShuttingDown();

    AnnounceTableResponse announceTable(AnnounceTableRequest request) throws AnnounceTableFailedException;

    // void closeTable(CloseTableRequest request) throws CloseTableFailedException;

    OpenSessionResponse openSession(OpenSessionRequest request) throws OpenSessionFailedException;

    void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

    ReserveResponse reserve(ReserveRequest request) throws ReserveFailedException;

    BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

    Money getMainAccountBalance(int playerId) throws GetBalanceFailedException;

    BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException;

    AllowJoinResponse allowJoinTable(int playerId);
}
