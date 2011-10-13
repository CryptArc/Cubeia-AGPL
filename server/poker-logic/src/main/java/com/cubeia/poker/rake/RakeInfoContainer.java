package com.cubeia.poker.rake;

public class RakeInfoContainer {
    
    private final int totalPot;
    private final int totalRake;
    
    public RakeInfoContainer(int totalPot, int totalRake) {
        super();
        this.totalPot = totalPot;
        this.totalRake = totalRake;
    }

    public int getTotalPot() {
        return totalPot;
    }

    public int getTotalRake() {
        return totalRake;
    }

    @Override
    public String toString() {
        return "RakeInfoContainer [totalPot=" + totalPot + ", totalRake=" + totalRake + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + totalPot;
        result = prime * result + totalRake;
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
