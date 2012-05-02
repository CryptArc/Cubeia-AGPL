package com.cubeia.poker;

import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

import java.io.Serializable;
import java.util.Map;

public class PokerSettings implements Serializable {

    private final int anteLevel;
    private int entryBetLevel;
    private final int minBuyIn;
    private final int maxBuyIn;
    private final TimingProfile timing;
    private final int tableSize;
    private final BetStrategyName betStrategy;
    private final RakeSettings rakeSettings;
    private final Map<Serializable, Serializable> attributes;
    private long sitoutTimeLimitMilliseconds = 1 * 60 * 1000;

    public PokerSettings(
            int anteLevel,
            int entryBetLevel,
            int minBuyIn,
            int maxBuyIn,
            TimingProfile timing,
            int tableSize,
            BetStrategyName betStrategy,
            RakeSettings rakeSettings,
            Map<Serializable, Serializable> attributes) {

        this.anteLevel = anteLevel;
        this.entryBetLevel = entryBetLevel;
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

    public int getAnteLevel() {
        return anteLevel;
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

    public int getEntryBetLevel() {
        return entryBetLevel;
    }

}
