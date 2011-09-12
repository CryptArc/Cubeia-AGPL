package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cubeia.poker.hand.calculator.HandCalculator;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;

/**
 * Compares hands. The most valued hand is greater than a lesser one.
 * This implementation delegates to {@link HandStrengthComparator}.
 * 
 * NOTE this impl sorts hands in the "wrong" order according to
 * behavior specified by Comparator interface. In some parts of the
 * code base Collections.reverseOrder must be used 
 * @author w
 */
public class TexasHoldemHandComparator implements Comparator<Hand> {
	
    private HandStrengthComparator hsc;
	HandCalculator calc = new TexasHoldemHandCalculator();

	
    public TexasHoldemHandComparator() {
        hsc = new HandStrengthComparator();
    }

    @Override
    public int compare(Hand h1, Hand h2) {
    	
		HandStrength h1Strength = getBestCombinationHandStrength(h1);
    	HandStrength h2Strength = getBestCombinationHandStrength(h2);
        return hsc.compare(h1Strength, h2Strength);
    }
    

	/**
	 * Get all possible hand combinations and rank them.
	 *  
	 * @param hand with more than 5 cards
	 * @return the best HandStrength found.
	 */
	protected HandStrength getBestCombinationHandStrength(Hand hand) {
		List<HandStrength> allPossibleHands = new ArrayList<HandStrength>();
		Combinator<Card> combinator = new Combinator<Card>(hand.getCards(), 5);
		for (List<Card> cards : combinator) {
			HandStrength handStrength = calc.getHandStrength(new Hand(cards));
			allPossibleHands.add(handStrength);
		}
		
		if (allPossibleHands.isEmpty()) {
		    throw new IllegalStateException("calculated 0 possible hands from cards: " + hand.toString());
		}
		
		Collections.sort(allPossibleHands, new HandStrengthComparator());
		return allPossibleHands.get(0);
	}
}
