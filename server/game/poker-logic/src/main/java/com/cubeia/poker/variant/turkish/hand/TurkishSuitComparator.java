package com.cubeia.poker.variant.turkish.hand;

import com.cubeia.poker.hand.Suit;

public class TurkishSuitComparator {

	static final int suitRank[] = {0, 1, 3, 2};
	
	static int compare(Suit suit1, Suit suit2) {
		return suitRank[suit1.ordinal()] - suitRank[suit2.ordinal()];
		
	}
}
