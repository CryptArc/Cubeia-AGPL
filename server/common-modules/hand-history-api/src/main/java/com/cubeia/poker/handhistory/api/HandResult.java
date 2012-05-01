package com.cubeia.poker.handhistory.api;

/**
 * This is the hand result of a single player. It contains
 * winnings, rake contribution and bet size.
 *
 * @author Lars J. Nilsson
 */
public class HandResult {

    private final int playerId;

    private long netWin;
    private long totalWin;
    private long rake;
    private long totalBet;

    public HandResult(int playerId, long netWin, long totalWin, long rake, long totalBet) {
        this.playerId = playerId;
        this.netWin = netWin;
        this.totalWin = totalWin;
        this.rake = rake;
        this.totalBet = totalBet;
    }

    public HandResult(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setNetWin(long netWin) {
        this.netWin = netWin;
    }

    public void setRake(long rake) {
        this.rake = rake;
    }

    public void setTotalBet(long totalBet) {
        this.totalBet = totalBet;
    }

    public void setTotalWin(long totalWin) {
        this.totalWin = totalWin;
    }

    public long getTotalBet() {
        return totalBet;
    }

    public long getNetWin() {
        return netWin;
    }

    public long getRake() {
        return rake;
    }

    public long getTotalWin() {
        return totalWin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (netWin ^ (netWin >>> 32));
        result = prime * result + playerId;
        result = prime * result + (int) (rake ^ (rake >>> 32));
        result = prime * result + (int) (totalBet ^ (totalBet >>> 32));
        result = prime * result + (int) (totalWin ^ (totalWin >>> 32));
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
        HandResult other = (HandResult) obj;
        if (netWin != other.netWin)
            return false;
        if (playerId != other.playerId)
            return false;
        if (rake != other.rake)
            return false;
        if (totalBet != other.totalBet)
            return false;
        if (totalWin != other.totalWin)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HandResult [playerId=" + playerId + ", netWin=" + netWin
                + ", totalWin=" + totalWin + ", rake=" + rake + ", totalBet="
                + totalBet + "]";
    }
}
