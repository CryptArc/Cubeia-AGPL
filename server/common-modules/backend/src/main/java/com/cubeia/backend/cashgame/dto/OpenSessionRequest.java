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

package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.games.poker.common.Money;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OpenSessionRequest implements Serializable {

    private final int playerId;
    private final Money openingBalance;
    private final TableId tableId;
    private final int roundNumber; // holds a counter of number of played hands at table

    public OpenSessionRequest(int playerId, TableId tableId, Money openingBalance, int roundNumber) {
        this.playerId = playerId;
        this.tableId = tableId;
        this.roundNumber = roundNumber;
        this.openingBalance = openingBalance;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Money getOpeningBalance() {
        return openingBalance;
    }

    public TableId getTableId() {
        return tableId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
