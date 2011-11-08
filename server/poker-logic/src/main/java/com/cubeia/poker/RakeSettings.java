package com.cubeia.poker;

import java.math.BigDecimal;

public class RakeSettings {
    private final BigDecimal rakeFraction;
    private final long rakeLimit;
    private final long rakeLimitHeadsUp;

    /**
     * Constructor.
     * @param rakeFraction fraction to rake (0.01 == 1%)
     * @param rakeLimit rake cap for normal play
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
