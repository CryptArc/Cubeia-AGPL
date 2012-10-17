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

package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PotUpdate extends HandHistoryEvent {

    private static final long serialVersionUID = -5809886423688004437L;
	
    private final List<GamePot> pots = new LinkedList<GamePot>();

    public PotUpdate() {
    }

    public PotUpdate(GamePot... pots) {
        for (GamePot p : pots) {
            this.pots.add(p);
        }
    }

    public List<GamePot> getPots() {
        return pots;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pots == null) ? 0 : pots.hashCode());
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
        PotUpdate other = (PotUpdate) obj;
        if (pots == null) {
            if (other.pots != null)
                return false;
        } else if (!pots.equals(other.pots))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PotUpdate [pots=" + pots + "]";
    }
}
