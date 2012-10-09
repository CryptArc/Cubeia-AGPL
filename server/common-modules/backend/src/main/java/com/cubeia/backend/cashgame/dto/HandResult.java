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

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.games.poker.common.Money;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HandResult implements Serializable {
    private final PlayerSessionId playerSession;

    /**
     * Sum of all players bets in the hand.
     */
    private final Money aggregatedBet;

    /**
     * Player winnings including own bets.
     */
    private final Money win;
    private final Money rake;

    private final int seat;
    private final Money startingBalance;

    public HandResult(PlayerSessionId playerSession, Money aggregatedBet,
                      Money win, Money rake, int seat, Money startingBalance) {

        this.playerSession = playerSession;
        this.aggregatedBet = aggregatedBet;
        this.win = win;
        this.rake = rake;
        this.seat = seat;
        this.startingBalance = startingBalance;
    }

    public PlayerSessionId getPlayerSession() {
        return playerSession;
    }

    public Money getAggregatedBet() {
        return aggregatedBet;
    }

    public Money getWin() {
        return win;
    }

    public Money getRake() {
        return rake;
    }

    public int getSeat() {
        return seat;
    }

    public Money getStartingBalance() {
        return startingBalance;
    }
}
