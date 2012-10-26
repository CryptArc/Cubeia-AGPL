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

import com.cubeia.poker.timing.TimingProfile;

import java.io.Serializable;
import java.util.Map;

public class PokerSettings implements Serializable {

    private static final long serialVersionUID = -8524532061876453809L;

	private int anteAmount;

    private int smallBlindAmount;

    private int bigBlindAmount;

    private final int minBuyIn;

    private final int maxBuyIn;

    private final TimingProfile timing;

    private final int tableSize;

    private final BetStrategyName betStrategy;

    private final RakeSettings rakeSettings;

    private final Map<Serializable, Serializable> attributes;

    private long sitoutTimeLimitMilliseconds = 1 * 60 * 1000;

    public PokerSettings(
            int anteAmount,
            int smallBlindAmount,
            int bigBlindAmount,
            int minBuyIn,
            int maxBuyIn,
            TimingProfile timing,
            int tableSize,
            BetStrategyName betStrategy,
            RakeSettings rakeSettings,
            Map<Serializable, Serializable> attributes) {

        this.anteAmount = anteAmount;
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.minBuyIn = minBuyIn;
        this.maxBuyIn = maxBuyIn;
        this.timing = timing;
        this.tableSize = tableSize;
        this.betStrategy = betStrategy;
        this.rakeSettings = rakeSettings;
        this.attributes = attributes;
    }

    public Map<Serializable, Serializable> getAttributes() {
        return attributes;
    }

    public int getAnteAmount() {
        return anteAmount;
    }

    public int getMaxBuyIn() {
        return maxBuyIn;
    }

    public TimingProfile getTiming() {
        return timing;
    }

    public int getTableSize() {
        return tableSize;
    }

    public BetStrategyName getBetStrategy() {
        return betStrategy;
    }

    public int getMinBuyIn() {
        return minBuyIn;
    }

    public RakeSettings getRakeSettings() {
        return rakeSettings;
    }

    public long getSitoutTimeLimitMilliseconds() {
        return sitoutTimeLimitMilliseconds;
    }

    public void setSitoutTimeLimitMilliseconds(long sitoutTimeLimitMilliseconds) {
        this.sitoutTimeLimitMilliseconds = sitoutTimeLimitMilliseconds;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public int getBigBlindAmount() {
        return bigBlindAmount;
    }

    public void setBlindsLevels(int smallBlindAmount, int bigBlindAmount, int ante) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.anteAmount = ante;
    }
}
