package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * Compares hands. The most valued hand is greater than a lesser one.
 * This implementation delegates to {@link HandStrengthComparator}.
 * @author w
 */
public class HandComparator implements Comparator<Hand> {
    
    private Comparator<HandStrength> handStrengthComparator;

    public HandComparator(Comparator<HandStrength> handStrengthComparator) {
    	this.handStrengthComparator = handStrengthComparator;
    }

    @Override
    public int compare(Hand h1, Hand h2) {
        return handStrengthComparator.compare(h1.getHandStrength(), h2.getHandStrength());
    }
    
}
