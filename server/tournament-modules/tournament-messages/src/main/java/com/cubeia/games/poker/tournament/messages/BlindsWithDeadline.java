/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.tournament.messages;

import java.io.Serializable;

public class BlindsWithDeadline implements Serializable {

    private final int smallBlindAmount;
    private final int bigBlindAmount;
    private final int anteAmount;
    private final int durationInMinutes;
    private final boolean isBreak;
    private final long deadline;

    public BlindsWithDeadline(int smallBlindAmount, int bigBlindAmount, int anteAmount, int durationInMinutes, boolean isBreak, long deadline) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.anteAmount = anteAmount;
        this.durationInMinutes = durationInMinutes;
        this.isBreak = isBreak;
        this.deadline = deadline;
    }

    public int getAnteAmount() {
        return anteAmount;
    }

    public int getBigBlindAmount() {
        return bigBlindAmount;
    }

    public long getDeadline() {
        return deadline;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public boolean isBreak() {
        return isBreak;
    }
}
