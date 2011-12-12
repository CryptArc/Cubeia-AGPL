package com.cubeia.poker.rake;

import java.util.Map;

import com.cubeia.poker.pot.Pot;

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
