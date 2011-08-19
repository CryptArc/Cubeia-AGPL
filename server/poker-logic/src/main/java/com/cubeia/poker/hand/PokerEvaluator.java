package com.cubeia.poker.hand;


/**
 * <p>Compares poker hands.</p>
 * 
 *  
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerEvaluator  {
	
	HandCalculator calc = new HandCalculator();
	
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
		
		HandType type1 = getHandStrength(hand1);
		HandType type2 = getHandStrength(hand2);
		
		if (type1.ordinal() > type2.ordinal()) {
			return 1;
		} else if (type1.ordinal() < type2.ordinal()) {
			return 1;
		} else {
			return 0;
		}
	}

	
	
	public HandType getHandStrength(Hand hand) {
		if (calc.isStraightFlush(hand)) {
			return HandType.STRAIGHT_FLUSH;
			
		} else if (calc.isManyOfAKind(hand, 4) != null) {
			return HandType.FOUR_OF_A_KIND;
			
		} else if (calc.isFullHouse(hand)) {
			return HandType.FULL_HOUSE;
			
		} else if (calc.isFlush(hand)) {
			return HandType.FLUSH;
			
		} else if (calc.isStraight(hand)) {
			return HandType.STRAIGHT;
			
		} else if (calc.isManyOfAKind(hand, 3) != null) {
			return HandType.THREE_OF_A_KIND;
			
		} else if (calc.isTwoPairs(hand)) {
			return HandType.TWO_PAIRS;
			
		} else if (calc.isOnePair(hand)) {
			return HandType.ONE_PAIR;
			
		} else {
			return HandType.HIGH_CARD;
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
