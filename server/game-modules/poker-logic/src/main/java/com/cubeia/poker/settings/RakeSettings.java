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

package com.cubeia.poker.settings;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class RakeSettings implements Serializable {
    private final BigDecimal rakeFraction;
    private final long rakeLimit;
    private final long rakeLimitHeadsUp;

    /**
     * Constructor.
     *
     * @param rakeFraction     fraction to rake (0.01 == 1%)
     * @param rakeLimit        rake cap for normal play
     * @param rakeLimitHeadsUp rake cap for heads up play (only two players bought in)
     */
    public RakeSettings(BigDecimal rakeFraction, long rakeLimit, long rakeLimitHeadsUp) {
        this.rakeFraction = rakeFraction;
        this.rakeLimit = rakeLimit;
        this.rakeLimitHeadsUp = rakeLimitHeadsUp;
    }

    public static RakeSettings createNoLimitRakeSettings(BigDecimal rakeFraction) {
        return new RakeSettings(rakeFraction, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    public BigDecimal getRakeFraction() {
        return rakeFraction;
    }

    public long getRakeLimit() {
        return rakeLimit;
    }

    public long getRakeLimitHeadsUp() {
        return rakeLimitHeadsUp;
    }

    @Override
    public String toString() {
        return "RakeSettings [rakeFraction=" + rakeFraction + ", rakeLimit=" + rakeLimit + ", rakeLimitHeadsUp="
                + rakeLimitHeadsUp + "]";
    }
}
