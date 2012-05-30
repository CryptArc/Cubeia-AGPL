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

package com.cubeia.poker.pot;

import com.cubeia.poker.pot.Pot;

import java.util.Map;

public class RakeInfoContainer {

    private final long totalPot;
    private final long totalRake;
    private final Map<Pot, Long> potRakes;

    public RakeInfoContainer(long totalPot, long totalRake, Map<Pot, Long> potRakes) {
        super();
        this.totalPot = totalPot;
        this.totalRake = totalRake;
        this.potRakes = potRakes;
    }

    public long getTotalPot() {
        return totalPot;
    }

    public long getTotalRake() {
        return totalRake;
    }

    public Map<Pot, Long> getPotRakes() {
        return potRakes;
    }

    @Override
    public String toString() {
        return "RakeInfoContainer [totalPot=" + totalPot + ", totalRake=" + totalRake + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) totalPot;
        result = prime * result + (int) totalRake;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RakeInfoContainer other = (RakeInfoContainer) obj;
        if (totalPot != other.totalPot)
            return false;
        if (totalRake != other.totalRake)
            return false;
        return true;
    }

}
