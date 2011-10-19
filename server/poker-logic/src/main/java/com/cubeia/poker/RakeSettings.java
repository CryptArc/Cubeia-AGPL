package com.cubeia.poker;

import java.math.BigDecimal;

public class RakeSettings {
    private final BigDecimal rakeFraction;
    private final Long rakeLimit;

    public RakeSettings(BigDecimal rakeFraction) {
        this(rakeFraction, Long.MAX_VALUE);
    }
    
    public RakeSettings(BigDecimal rakeFraction, Long rakeLimit) {
        this.rakeFraction = rakeFraction;
        this.rakeLimit = rakeLimit;
    }

    public BigDecimal getRakeFraction() {
        return rakeFraction;
    }
    
    public Long getRakeLimit() {
        return rakeLimit;
    }

    @Override
    public String toString() {
        return "RakeSettings [rakeFraction=" + rakeFraction + ", rakeLimit=" + rakeLimit + "]";
    }
}
