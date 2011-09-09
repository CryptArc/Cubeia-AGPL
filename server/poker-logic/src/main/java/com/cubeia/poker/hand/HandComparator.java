package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * Compares hands. The most valued hand is greater than a lesser one.
 * This implementation delegates to {@link HandStrengthComparator}.
 * @author w
 */
public class HandComparator implements Comparator<Hand> {
    private HandStrengthComparator hsc;

    public HandComparator() {
        hsc = new HandStrengthComparator();
    }

    @Override
    public int compare(Hand h1, Hand h2) {
        return hsc.compare(h1.getHandStrength(), h2.getHandStrength());
    }
}
