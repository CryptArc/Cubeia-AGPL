package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	 * @param hands
	 * @return
	 */
	public List<Hand> rankHands(List<Hand> hands) {
		List<Hand> result = new ArrayList<Hand>(hands);
		for (Hand hand : result) {
			HandStrength handStrength = calc.getHandStrength(hand);
			hand.setHandStrength(handStrength);
		}
		
		Collections.sort(result);
		return result;
	}
}
