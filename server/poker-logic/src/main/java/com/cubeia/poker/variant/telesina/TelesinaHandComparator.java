package com.cubeia.poker.variant.telesina;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

public class TelesinaHandComparator implements Comparator<Hand> {

	private TelesinaHandStrengthEvaluator evaluator;
	
	/**
	 * Needed by JBoss serialization.
	 */
	@SuppressWarnings("unused")
    private TelesinaHandComparator() {}
	
	/**
	 * 
	 * @param deckLowestRank the rank of the lowest card not stripped from the deck
	 */
	public TelesinaHandComparator(Rank deckLowestRank) {
		this.evaluator = new TelesinaHandStrengthEvaluator(deckLowestRank);
	}

	public TelesinaHandComparator(TelesinaHandStrengthEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	@Override
	public int compare(Hand h1, Hand h2) {
		List<Card> c1 = h1.getCards();
		List<Card> c2 = h2.getCards();

		return compare(c1, c2);
	}
	
	public int compare(List<Card> c1, List<Card> c2) {
		TelesinaHandStrength c1Strength = evaluator.getBestHandStrength(c1);
		TelesinaHandStrength c2Strength = evaluator.getBestHandStrength(c2);
		
		if (c1Strength.handType != c2Strength.handType) {
			return c1Strength.handType.telesinaHandTypeValue - c2Strength.handType.telesinaHandTypeValue;
		}
		
		if (c1Strength.handType == HandType.FLUSH) {
			Suit c1Suit = c1Strength.groups[0].get(0).getSuit();
			Suit c2Suit = c2Strength.groups[0].get(0).getSuit();
			
			if (c1Suit != c2Suit) {
				return c1Suit.telesinaSuitValue - c2Suit.telesinaSuitValue;
			}
		}
		
		if (c1Strength.groups.length != c2Strength.groups.length) {
			throw new IllegalStateException("Comparison groups in strength not of same size for two hands of type " + c1Strength.handType);
		}
		
		for (int i = 0; i < c1Strength.groups.length; i++) {
			int compare = compareKickers(c1Strength.groups[i], c2Strength.groups[i]);
			if (compare != 0) {
				return compare;
			}
		}
		
		return 0;
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
