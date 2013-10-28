package com.cubeia.poker.variant.turkish.hand;

import static com.cubeia.poker.hand.Suit.SPADES;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Combinator;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.HandTypeEvaluator;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.calculator.ByRankCardComparator;
import com.cubeia.poker.hand.eval.HandTypeCheckCalculator;

public class TurkishHandStrengthEvaluator implements HandTypeEvaluator, Serializable { 

	private static final long serialVersionUID = -9170331338794918678L;

	private HandTypeCheckCalculator typeCalculator;

    private final Rank deckLowestRank;

    /**
     * Create a hand strength evaluator fo a given turkish deck. 
     *
     * @param deckLowestRank
     */
    public TurkishHandStrengthEvaluator(Rank deckLowestRank) {
        this.deckLowestRank = deckLowestRank;
        typeCalculator = new HandTypeCheckCalculator(deckLowestRank);
    }

    @Override
    public HandInfo getBestHandInfo(Hand hand) {
        return getBestHandStrength(hand);
    }

    @Override
    public Comparator<Hand> createHandComparator(int playersInPot) {
        return new TurkishHandComparator(this, playersInPot);
    }

    private List<Card> findBestHand(Hand hand) {
    	TurkishHandComparator comp = new TurkishHandComparator(this, 1);
        Combinator<Card> comb = new Combinator<Card>(hand.getCards(), 5);
        List<Card> best = comb.next();

        while (comb.hasNext()) {
            List<Card> candidate = comb.next();
            if (comp.compare(new Hand(candidate), new Hand(best)) > 0) {
                best = candidate;
            }
        }

        return best;
    }

    /**
     * Returns a list of cards (without id:s) that forms the
     * lowest possible straight flush for the given deck size.
     * The cards are all spades. For a deck with lowest rank = 3 this is:
     * AS 3S 4S 5S 6S
     *
     * @return lowest possible straight flush
     */
    public List<Card> getLowestStraightFlushCards() {
        int lowestOrdinal = deckLowestRank.ordinal();

        Rank[] ranks = Rank.values();
        Card c0 = new Card(Rank.ACE, SPADES);
        Card c1 = new Card(ranks[lowestOrdinal + 0], SPADES);
        Card c2 = new Card(ranks[lowestOrdinal + 1], SPADES);
        Card c3 = new Card(ranks[lowestOrdinal + 2], SPADES);
        Card c4 = new Card(ranks[lowestOrdinal + 3], SPADES);

        return Arrays.asList(c0, c1, c2, c3, c4);
    }

    /**
     * Find the strength of the best hand that can be built using a list of cards.
     *
     * @param hand
     * @return
     */
    public HandStrength getBestHandStrength(Hand hand) {
        List<Card> cards = hand.getCards();

        if (cards.size() > 5) {
            cards = findBestHand(hand);
            hand = new Hand(cards);
        }

        HandStrength strength = null;

        // ROYAL_STRAIGHT_FLUSH
        strength = checkRoyalStraightFlush(hand, 5);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.ROYAL_STRAIGHT_FLUSH.ordinal();
        	return strength;
        }

        // STRAIGHT_FLUSH
        strength = checkStraightFlush(hand, 5);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.STRAIGHT_FLUSH.ordinal();
        	return strength;
        }

        // FOUR_OF_A_KIND
        strength = typeCalculator.checkManyOfAKind(hand, 4);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.FOUR_OF_A_KIND.ordinal();
        	return strength;
        }

        // FLUSH
        strength = typeCalculator.checkFlush(hand, 5);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.FLUSH.ordinal();
        	return strength;
        }

        // FULL_HOUSE
        strength = typeCalculator.checkFullHouse(hand);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.FULL_HOUSE.ordinal();
        	return strength;
        }

        // THREE_OF_A_KIND
        strength = typeCalculator.checkManyOfAKind(hand, 3);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.THREE_OF_A_KIND.ordinal();
        	return strength;
        }
        
        // STRAIGHT
        strength = checkStraight(hand, 5);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.STRAIGHT.ordinal();
        	return strength;
        }
       
        // TWO_PAIRS
        strength = typeCalculator.checkTwoPairs(hand);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.TWO_PAIRS.ordinal();
        	return strength;
        }

        // ONE_PAIR
        strength = typeCalculator.checkManyOfAKind(hand, 2);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.PAIR.ordinal();
        	return strength;
        }

        // HIGH_CARD
        strength = typeCalculator.checkHighCard(hand);
        if (strength != null) {
        	strength.getHandType().specialHandTypeValue = TurkishHandType.HIGH_CARD.ordinal();
        	return strength;
        }

        strength = new HandStrength(HandType.NOT_RANKED);
        strength.getHandType().specialHandTypeValue = TurkishHandType.NOT_RANKED.ordinal();
        
        return strength;
    }

    /**
     * Checks to see if ALL cards (any number) supplied form a straight, aces low allowed.
     * Deck may be stripped.
     *
     * @param hand         The cards to check
     * @param minimumLength The minimum length required for a set of card to count as a straight (NOTE a one card straight is never recognized)
     * @return
     */
    public HandStrength checkStraight(Hand hand, int minimumLength) {
        if (hand.getNumberOfCards() < minimumLength) {
            return null;
        }

        HandStrength checkStraightAcesHigh = typeCalculator.checkStraight(hand);
        if (checkStraightAcesHigh != null) {
            return checkStraightAcesHigh;
        }
        
        return typeCalculator.checkStraight(hand, true);
    }

    /**
     * Checks to see if ALL cards (any number) supplied form a straight flush.
     * Deck may be stripped.
     *
     * @param hand         The cards to check
     * @param minimumLength The minimum length required for a set of card to count as a straight
     * @return
     */
    public HandStrength checkStraightFlush(Hand hand, int minimumLength) {
        if (typeCalculator.checkFlush(hand, minimumLength) == null) {
            return null;
        }

        HandStrength checkStraight = checkStraight(hand, minimumLength);

        if (checkStraight == null) {
            return null;
        }

        List<Card> sorted = new ArrayList<Card>(hand.getCards());
        Collections.sort(sorted, ByRankCardComparator.ACES_LOW_ASC);
        return new HandStrength(HandType.STRAIGHT_FLUSH, sorted, hand.getCards());
    }

    /**
     * Checks for a Royal Straight Flush.
     *
     * @param hand         The cards to check
     * @param minimumLength The minimum length required for a set of card to count as a straight
     * @return hand strength, null if not a royal straight flush
     * @see #checkStraightFlush(Hand, int)
     */
    public HandStrength checkRoyalStraightFlush(Hand hand, int minimumLength) {
        HandStrength strength = checkStraightFlush(hand, minimumLength);

        if (strength == null) {
            return null;
        }

        List<Card> cards = strength.getCards();
        if (containsRank(cards, Rank.KING) && containsRank(cards, Rank.ACE)) {
            List<Card> sorted = new ArrayList<Card>(hand.getCards());
            Collections.sort(sorted, ByRankCardComparator.ACES_HIGH_ASC);
            return new HandStrength(HandType.ROYAL_STRAIGHT_FLUSH, sorted, hand.getCards());
        } else {
            return null;
        }
    }

    private boolean containsRank(List<Card> cards, Rank rank) {
        for (Card c : cards) {
            if (c.getRank() == rank) {
                return true;
            }
        }
        return false;
    }
}