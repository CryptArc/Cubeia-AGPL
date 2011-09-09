package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * Compares hands. The most valued hand is greater than a lesser one.
 * This implementation delegates to {@link HandStrengthComparator}.
 * @author w
 */
public class HandComparator implements Comparator<Hand> {
    private HandStrengthComparator hsc;
	private PokerEvaluator pokerEvaluator;

    public HandComparator() {
        hsc = new HandStrengthComparator();
        pokerEvaluator = new PokerEvaluator();
    }

    @Override
    public int compare(Hand h1, Hand h2) {
    	
		HandStrength h1Strength = pokerEvaluator.getBestCombinationHandStrength(h1);
    	HandStrength h2Strength = pokerEvaluator.getBestCombinationHandStrength(h2);
        return hsc.compare(h1Strength, h2Strength);
    }
}
