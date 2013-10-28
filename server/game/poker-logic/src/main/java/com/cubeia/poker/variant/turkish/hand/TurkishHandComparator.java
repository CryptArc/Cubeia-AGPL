package com.cubeia.poker.variant.turkish.hand;

import static com.cubeia.poker.hand.HandType.ROYAL_STRAIGHT_FLUSH;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.variant.telesina.TelesinaCardComparator;

public class TurkishHandComparator implements Comparator<Hand>, Serializable {

	private static final long serialVersionUID = -6327424558382436021L;
	private int playersInPot;
	private TurkishHandStrengthEvaluator evaluator;

	private final static Hand HIGHEST_ROYAL_STRAIGHT_FLUSH = new Hand("TH JH QH KH AH");
	
	 /**
     * Needed by JBoss serialization.
     */
    @SuppressWarnings("unused")
    private TurkishHandComparator() {
        playersInPot = 0;
    }

	
	public TurkishHandComparator(TurkishHandStrengthEvaluator turkishHandStrengthEvaluator, int playersInPot) {
		 this.evaluator = turkishHandStrengthEvaluator;
	     this.playersInPot = playersInPot;
	}

	 public int compare(Hand h1, Hand h2) {
	        HandStrength c1Strength = evaluator.getBestHandStrength(h1);
	        HandStrength c2Strength = evaluator.getBestHandStrength(h2);

	        if (playersInPot == 2 && checkForRoyals(c1Strength, c2Strength)) {
	            List<Card> highestRoyal = HIGHEST_ROYAL_STRAIGHT_FLUSH.getCards();
	            List<Card> lowestRoyal = evaluator.getLowestStraightFlushCards();

	            if (h1.containsAllCardsRegardlessOfId(highestRoyal) && h2.containsAllCardsRegardlessOfId(lowestRoyal)) {
	                return -1;
	            } else if (h2.containsAllCardsRegardlessOfId(highestRoyal) && h1.containsAllCardsRegardlessOfId(lowestRoyal)) {
	                return 1;
	            }
	        }

	        if (c1Strength.getHandType() != c2Strength.getHandType()) {
	            return c1Strength.getHandType().specialHandTypeValue - c2Strength.getHandType().specialHandTypeValue;
	        }

	        if (c1Strength.getHandType() == HandType.FLUSH) {
	            Suit c1Suit = c1Strength.getGroup(0).get(0).getSuit();
	            Suit c2Suit = c2Strength.getGroup(0).get(0).getSuit();

	            if (c1Suit != c2Suit) {
	                return TurkishSuitComparator.compare(c1Suit, c2Suit);
	            }
	        }

	        if (c1Strength.getGroupSize() != c2Strength.getGroupSize()) {
	            throw new IllegalStateException("Comparison groups in strength not of same size for two hands of type " + c1Strength.getHandType());
	        }

	        for (int i = 0; i < c1Strength.getGroupSize(); i++) {
	            int compare = compareKickers(c1Strength.getGroup(i), c2Strength.getGroup(i));
	            if (compare != 0) {
	                return compare;
	            }
	        }

	        return 0;
	    }

	    /**
	     * Returns true if any of the given hand strengths contains a royal straight flush.
	     */
	    private boolean checkForRoyals(HandStrength c1Strength, HandStrength c2Strength) {
	        return c1Strength.getHandType() == ROYAL_STRAIGHT_FLUSH || c2Strength.getHandType() == ROYAL_STRAIGHT_FLUSH;
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
