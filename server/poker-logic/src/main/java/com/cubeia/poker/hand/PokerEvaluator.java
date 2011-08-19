package com.cubeia.poker.hand;

import com.cubeia.poker.hand.calculator.HandCalculator;
import com.cubeia.poker.hand.calculator.TexasHoldemHandCalculator;


/**
 * <p>Compares poker hands.</p>
 * 
 * <p>This is a 'naive' implementation of a poker evaluator (and hand calculator),
 * it is not built for speed but with usability in mind. If you need a faster 
 * implementation I suggest looking at the University of Alberta implementation.</p>
 *  
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerEvaluator  {
	
	HandCalculator calc = new TexasHoldemHandCalculator();
	
	/**
	 * 
	 * @param hand1, unsorted is ok
	 * @param hand2, unsorted is ok
	 * 
	 * @return > 0 if hand1 is better, < 0 is hand2 is better, 0 if equal.
	 */
	public int compareHands(Hand h1, Hand h2) {
		Hand hand1 = h1.sort();
		Hand hand2 = h2.sort();
		
		HandStrength strength1 = calc.getHandStrength(hand1);
		HandStrength strength2 = calc.getHandStrength(hand2);
		
		HandType handType1 = strength1.getHandType();
		HandType handType2 = strength2.getHandType();
		
		if (handType1.ordinal() > handType2.ordinal()) {
			return 1;
		} else if (handType1.ordinal() < handType2.ordinal()) {
			return 1;
		} else {
			return 0;
		}
	}

	

	/**
	 * 
	 * @param hand1, must be sorted
	 * @param hand2, must be sorted
	 * 
	 * @return > 0 if hand1 is better, < 0 is hand2 is better, 0 if equal.
	 */
	protected int compareEmptyHands(Hand hand1, Hand hand2) {
		for (int i = 0; i < hand1.getCards().size(); i++) {
			int c1 = hand1.getCardAt(i).getRank().ordinal();
			int c2 = hand2.getCardAt(i).getRank().ordinal();
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return 0;
	}
	
}
