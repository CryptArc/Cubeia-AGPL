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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Results implements Serializable {

    private static final long serialVersionUID = 5742358497502861176L;

	private long totalRake;
    private final Map<Integer, HandResult> results = new HashMap<Integer, HandResult>();

    public Results(long totalRake) {
        this.totalRake = totalRake;
    }

    public Results() { }
    
    public Map<Integer, HandResult> getResults() {
        return results;
    }

    public void setTotalRake(long totalRake) {
        this.totalRake = totalRake;
    }

    public long getTotalRake() {
        return totalRake;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + (int) (totalRake ^ (totalRake >>> 32));
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
		Results other = (Results) obj;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		if (totalRake != other.totalRake)
			return false;
		return true;
	}

    @Override
	public String toString() {
		return "Results [totalRake=" + totalRake + ", results=" + results + "]";
	}
}
