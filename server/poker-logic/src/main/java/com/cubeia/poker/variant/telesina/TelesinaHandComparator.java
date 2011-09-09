package com.cubeia.poker.variant.telesina;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Combinator;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

public class TelesinaHandComparator implements Comparator<Hand> {

	private TelesinaHandStrengthEvaluator evaluator;
	
	/**
	 * 
	 * @param deckLowestRank the rank of the lowest card not stripped from the deck
	 */
	public TelesinaHandComparator(Rank deckLowestRank) {
		this.evaluator = new TelesinaHandStrengthEvaluator(deckLowestRank);
	}

	@Override
	public int compare(Hand h1, Hand h2) {
		List<Card> c1 = h1.getCards();
		List<Card> c2 = h2.getCards();
		
		List<Card> bestH1 = (c1.size() > 5) ? findBestHand(c1) : c1;
		List<Card> bestH2 = (c2.size() > 5) ? findBestHand(c2) : c2;
		
		return compare0(bestH1, bestH2);
	}
	
	private int compare0(List<Card> c1, List<Card> c2) {
		TelesinaHandStrength c1Strength = evaluator.getStrength(c1);
		TelesinaHandStrength c2Strength = evaluator.getStrength(c2);
		
		if (c1Strength.handType != c2Strength.handType) {
			return c1Strength.handType.telesinaHandTypeValue - c2Strength.handType.telesinaHandTypeValue;
		}
		
		if (c1Strength.handType == HandType.FLUSH) {
			Suit c1Suit = c1Strength.highGroupCards.get(0).getSuit();
			Suit c2Suit = c2Strength.highGroupCards.get(0).getSuit();
			
			if (c1Suit != c2Suit) {
				return c1Suit.telesinaSuitValue - c2Suit.telesinaSuitValue;
			}
		}
		
		int highGroupCompare = compareKickers(c1Strength.highGroupCards, c2Strength.highGroupCards);
		if (highGroupCompare != 0) {
			return highGroupCompare;
		}
		
		int lowGroupCompare = compareKickers(c1Strength.lowGroupCards, c2Strength.lowGroupCards);
		if (lowGroupCompare != 0) {
			return lowGroupCompare;
		}
		
		return compareKickers(c1Strength.kickers, c2Strength.kickers);
	}
	
	private List<Card> findBestHand(List<Card> cards) {
		Combinator<Card> comb = new Combinator<Card>(cards, 5);
		List<Card> best = comb.next();

		while (comb.hasNext()) {
			List<Card> candidate = comb.next();
			if (compare0(candidate, best) > 0) {
				best = candidate;
			}
		}
		
		return best;
	}

	private int compareKickers(List<Card> c1, List<Card> c2) {
		if (c1.size() != c2.size()) {
			throw new IllegalArgumentException("Only kicker lists of equal length may be compared");
		}
		
		List<Card> c1copy = new LinkedList<Card>(c1); 
		List<Card> c2copy = new LinkedList<Card>(c2);
		
		Collections.sort(c1copy, TelesinaCardComparator.DESC);
		Collections.sort(c2copy, TelesinaCardComparator.DESC);
		
		Iterator<Card> c1iter = c1copy.iterator();
		Iterator<Card> c2iter = c2copy.iterator();

		while (c1iter.hasNext() && c2iter.hasNext()) {
			int cmp = TelesinaCardComparator.ASC.compare(c1iter.next(), c2iter.next());
			if (cmp != 0) {
				return cmp;
			}
		}

		return 0;
	}
}
