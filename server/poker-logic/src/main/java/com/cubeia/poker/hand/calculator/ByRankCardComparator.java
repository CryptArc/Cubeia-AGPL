package com.cubeia.poker.hand.calculator;

import java.util.Comparator;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;

public class ByRankCardComparator implements Comparator<Card> {

	public static final Comparator<Card> ACES_HIGH =  new ByRankCardComparator(true);
	public static final Comparator<Card> ACES_LOW =  new ByRankCardComparator(false);
	
	private boolean acesHigh;

	private ByRankCardComparator(boolean acesHigh) {
		this.acesHigh = acesHigh;
	}

	@Override
	public int compare(Card c1, Card c2) {
		if (acesHigh) {
			return compareAcesHigh(c1, c2);
		} else {
			return compareAcesLow(c1, c2);
		}
	}

	private int compareAcesLow(Card c1, Card c2) {
		int c1Rank = c1.getRank().ordinal();
		if (c1Rank == Rank.ACE.ordinal()) {
			c1Rank = -1;
		}

		int c2Rank = c2.getRank().ordinal();
		if (c2Rank == Rank.ACE.ordinal()) {
			c2Rank = -1;
		}
		
		return c1Rank - c2Rank;
	}

	public int compareAcesHigh(Card c1, Card c2) {
		return c1.getRank().ordinal() - c2.getRank().ordinal();
	}
}
