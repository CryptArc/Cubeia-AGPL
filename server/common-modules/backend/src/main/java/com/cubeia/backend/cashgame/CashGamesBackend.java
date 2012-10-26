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

import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.AllowJoinResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;
import com.cubeia.games.poker.common.Money;

/**
 * Cash game backend abstraction.
 * 
 * <p>This interface contains methods needed by a cash game (poker 
 * for example) when interacting with a backend system (wallet). <p/>
 * 
 * @author w
 */
public interface CashGamesBackend {
	
	/**
	 * Method used to stop tables from starting new hands.
	 */
	boolean isSystemShuttingDown();

    /**
     * Generate a new hand ID. This method is synchronous and
     * should be implemented to return as swiftly as possible as it
     * will be called between all hands.
     *
     * @return A new hand ID, never null
     */
    String generateHandId();

    /**
     * Returns true if the player is allowed to join tables.
     *
     * @param playerId player
     * @return true if allowed
     */
    AllowJoinResponse allowJoinTable(int playerId);

    /**
     * Announce a table created by the game.
     * This call can be used if tables needs to be populated with external
     * data (external table ids for example) before use.
     */
    AnnounceTableResponse announceTable(AnnounceTableRequest request) throws AnnounceTableFailedException;

    /**
     * Open a player session.
     */
    OpenSessionResponse openSession(OpenSessionRequest request) throws OpenSessionFailedException;

    /**
     * Close a player session previously opened with {@link #openSession(OpenSessionRequest, OpenSessionCallback)}.
     */
    void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

    /**
     * Reserve currency for a game. An open session is needed.
     */
    ReserveResponse reserve(ReserveRequest request) throws ReserveFailedException; 

    /**
     * Report the result of a hand.
     */
    BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

    /**
     * Returns the main account balance for the given player. This amount
     * does not include currency locked up in open sessions.
     */
    Money getMainAccountBalance(int playerId) throws GetBalanceFailedException;

    /**
     * Returns the balance of the given session.
     */
    BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException;

}
