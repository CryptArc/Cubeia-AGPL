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

package com.cubeia.games.poker.tournament.configuration.blinds;

import java.io.Serializable;

public class BlindsLevel implements Serializable {

    private int smallBlindAmount;

    private int bigBlindAmount;

    private int anteAmount;

    public BlindsLevel(int smallBlindAmount, int bigBlindAmount, int anteAmount) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.anteAmount = anteAmount;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public int getBigBlindAmount() {
        return bigBlindAmount;
    }

    public int getAnteAmount() {
        return anteAmount;
    }

    @Override
    public String toString() {
        return "BlindsLevel{" + "smallBlindAmount=" + smallBlindAmount + ", bigBlindAmount=" + bigBlindAmount + ", anteAmount=" + anteAmount + '}';
    }
}
