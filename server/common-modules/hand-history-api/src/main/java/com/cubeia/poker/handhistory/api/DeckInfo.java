package com.cubeia.poker.handhistory.api;

import com.cubeia.poker.handhistory.api.GameCard.Rank;

public class DeckInfo {

    private final int size;
    private final Rank lowRank;

    public DeckInfo(int size, Rank lowRank) {
        this.size = size;
        this.lowRank = lowRank;
    }

    public Rank getLowRank() {
        return lowRank;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Deck size: " + size + "; Low rank: " + lowRank;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lowRank == null) ? 0 : lowRank.hashCode());
        result = prime * result + size;
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
        DeckInfo other = (DeckInfo) obj;
        if (lowRank != other.lowRank)
            return false;
        if (size != other.size)
            return false;
        return true;
    }
}
