package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PotUpdate extends HandHistoryEvent {

    private final List<GamePot> pots = new LinkedList<GamePot>();

    public PotUpdate() {
    }

    public PotUpdate(GamePot... pots) {
        for (GamePot p : pots) {
            this.pots.add(p);
        }
    }

    public List<GamePot> getPots() {
        return pots;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pots == null) ? 0 : pots.hashCode());
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
        PotUpdate other = (PotUpdate) obj;
        if (pots == null) {
            if (other.pots != null)
                return false;
        } else if (!pots.equals(other.pots))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PotUpdate [pots=" + pots + "]";
    }
}
