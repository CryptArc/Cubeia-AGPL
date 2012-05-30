package com.cubeia.poker.variant.texasholdem;

import com.cubeia.poker.hand.*;
import com.cubeia.poker.hand.calculator.HandCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Compares hands. The most valued hand is greater than a lesser one.
 * This implementation delegates to {@link HandStrengthComparator}.
 * <p/>
 * NOTE this impl sorts hands in the "wrong" order according to
 * behavior specified by Comparator interface. In some parts of the
 * code base Collections.reverseOrder must be used
 *
 * @author w
 */
public class TexasHoldemHandComparator implements Comparator<Hand> {

    private HandStrengthComparator hsc;
    private TexasHoldemHandCalculator calc = new TexasHoldemHandCalculator();


    public TexasHoldemHandComparator() {
        hsc = new HandStrengthComparator();
    }

    @Override
    public int compare(Hand h1, Hand h2) {
        HandStrength h1Strength = calc.getBestCombinationHandStrength(h1, 5);
        HandStrength h2Strength = calc.getBestCombinationHandStrength(h2, 5);
        return hsc.compare(h1Strength, h2Strength);
    }


}
