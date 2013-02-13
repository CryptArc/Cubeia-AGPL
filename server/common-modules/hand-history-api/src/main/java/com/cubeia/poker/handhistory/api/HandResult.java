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

/**
 * This is the hand result of a single player. It contains
 * winnings, rake contribution and bet size.
 *
 * @author Lars J. Nilsson
 */
public class HandResult implements Serializable {

    private static final long serialVersionUID = 7495444478185154491L;

    private int playerId;
    private long netWin;
    private long totalWin;
    private long rake;
    private long totalBet;
    private String transactionId;

    public HandResult() {
    }

    public HandResult(int playerId, long netWin, long totalWin, long rake, long totalBet) {
        this.playerId = playerId;
        this.netWin = netWin;
        this.totalWin = totalWin;
        this.rake = rake;
        this.totalBet = totalBet;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getNetWin() {
        return netWin;
    }

    public void setNetWin(long netWin) {
        this.netWin = netWin;
    }

    public long getTotalWin() {
        return totalWin;
    }

    public void setTotalWin(long totalWin) {
        this.totalWin = totalWin;
    }

    public long getRake() {
        return rake;
    }

    public void setRake(long rake) {
        this.rake = rake;
    }

    public long getTotalBet() {
        return totalBet;
    }

    public void setTotalBet(long totalBet) {
        this.totalBet = totalBet;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HandResult that = (HandResult) o;

        if (netWin != that.netWin) return false;
        if (playerId != that.playerId) return false;
        if (rake != that.rake) return false;
        if (totalBet != that.totalBet) return false;
        if (totalWin != that.totalWin) return false;
        if (transactionId != null ? !transactionId.equals(that.transactionId) : that.transactionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playerId;
        result = 31 * result + (int) (netWin ^ (netWin >>> 32));
        result = 31 * result + (int) (totalWin ^ (totalWin >>> 32));
        result = 31 * result + (int) (rake ^ (rake >>> 32));
        result = 31 * result + (int) (totalBet ^ (totalBet >>> 32));
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HandResult{" +
                "playerId=" + playerId +
                ", netWin=" + netWin +
                ", totalWin=" + totalWin +
                ", rake=" + rake +
                ", totalBet=" + totalBet +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
