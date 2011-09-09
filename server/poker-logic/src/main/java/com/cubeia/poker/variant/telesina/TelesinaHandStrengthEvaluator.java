package com.cubeia.poker.variant.telesina;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.HandTypeEvaluator;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.hand.calculator.ByRankCardComparator;

public class TelesinaHandStrengthEvaluator implements HandTypeEvaluator, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Rank deckLowestRank;

	/**
	 * Create a hand strength evaluator fo a given telesina deck. A 
	 * lowest Rank of Rank.TWO corresponds to a full deck.
	 * 
	 * @param deckLowestRank
	 */
	public TelesinaHandStrengthEvaluator(Rank deckLowestRank) {
		this.deckLowestRank = deckLowestRank;
	}

	@Override
	public HandType getHandType(Hand hand) {
		return getStrength(hand.getCards()).handType;
	}
	
	public TelesinaHandStrength getStrength(List<Card> cards) {
		TelesinaHandStrength strength = checkStraightFlush(cards, 5, true);
		if (strength != null) {
			return strength;
		}
		
		strength = checkFourOfAKind(cards);
		if (strength != null) {
			return strength;
		}
		
		strength = checkFlush(cards, 5);
		if (strength != null) {
			return strength;
		}
		
		strength = checkFullHouse(cards);
		if (strength != null) {
			return strength;
		}
		
		strength = checkStraight(cards, 5, true);
		if (strength != null) {
			return strength;
		}
		
		strength = checkThreeOfAKind(cards);
		if (strength != null) {
			return strength;
		}
		
		strength = checkTwoPair(cards);
		if (strength != null) {
			return strength;
		}
		
		strength = checkPair(cards);
		if (strength != null) {
			return strength;
		}
		
		strength = new TelesinaHandStrength(HandType.HIGH_CARD, Collections.EMPTY_LIST, Collections.EMPTY_LIST, cards);
		return strength;
	}
	
	/**
	 * Check if there is at least one set of at least two cards.
	 * @param cards
	 * @return A HandStrength of type ONE_PAIR
	 */
	public TelesinaHandStrength checkPair(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		List<Card> setCards = checkManyOfAKind(cc, 2);
		
		if (setCards == null) {
			return null;
		}
		
		cc.removeAll(setCards);
		return new TelesinaHandStrength(HandType.ONE_PAIR, setCards, Collections.EMPTY_LIST, cc);
	}
	
	/**
	 * Check if there are at least two sets of at least two cards.
	 * @param cards
	 * @return
	 */
	public TelesinaHandStrength checkTwoPair(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> highSet = checkManyOfAKind(cc, 2);
		if (highSet == null) {
			return null;
		}
		
		cc.removeAll(highSet);
		
		List<Card> lowSet = checkManyOfAKind(cc, 2);
		if (lowSet == null) {
			return null;
		}
		
		cc.removeAll(lowSet);
		
		return new TelesinaHandStrength(HandType.TWO_PAIRS, highSet, lowSet, cc);
	}
	
	/**
	 * Check if there is at least one set of three cards and at least one other set of at least two cards.
	 */
	public TelesinaHandStrength checkFullHouse(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> threeSet = checkManyOfAKind(cc, 3);
		if (threeSet == null) {
			return null;
		}
		
		cc.removeAll(threeSet);
		
		List<Card> twoSet = checkManyOfAKind(cc, 2);
		if (twoSet == null) {
			return null;
		}

		return new TelesinaHandStrength(HandType.FULL_HOUSE, threeSet, twoSet, Collections.EMPTY_LIST);
	}
	
	/**
	 * Check if there is at least one three of a kind in a list of cards.
	 * @param cards
	 * @return
	 */
	public TelesinaHandStrength checkThreeOfAKind(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> threeSet = checkManyOfAKind(cc, 3);
		if (threeSet == null) {
			return null;
		}
		
		cc.removeAll(threeSet);
		
		return new TelesinaHandStrength(HandType.THREE_OF_A_KIND, threeSet, Collections.EMPTY_LIST, cc);
	}
	
	
	/**
	 * Check if there is at least one four of a kind in a list of cards.
	 * @param cards
	 * @return
	 */
	public TelesinaHandStrength checkFourOfAKind(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> fourSet = checkManyOfAKind(cc, 4);
		if (fourSet == null) {
			return null;
		}
		
		cc.removeAll(fourSet);
		
		return new TelesinaHandStrength(HandType.FOUR_OF_A_KIND, fourSet, Collections.EMPTY_LIST, cc);
	}
	
	/**
	 * Duplicate default behavior of checkStraight in texas holdem impl.
	 * No cards stripped from deck, aces may NOT be low, straight length not bound (ie one card is a straight).
	 */
	public TelesinaHandStrength checkStraight(List<Card> cards) {
		return checkStraight(cards, 1, false);
	}
	
	/**
	 * Checks to see if ALL cards (any number) supplied form a straight.
	 * Deck may be stripped and aces may or may not be used as low card.
	 * 
	 * @param cards The cards to check
	 * @param minimumLength The minimum length required for a set of card to count as a straight (NOTE a one card straight is never recognized)
	 * @param allowLowAces 
	 * @return
	 */
	public TelesinaHandStrength checkStraight(List<Card> cards, int minimumLength, boolean allowLowAces) {
		
		if (cards.size() < minimumLength) {
			return null;
		}
		
		TelesinaHandStrength checkStraightAcesHigh = checkStraightAcesHigh(cards);
		if (checkStraightAcesHigh != null) {
			return checkStraightAcesHigh;
		}
		if (!allowLowAces) {
			return null;
		}
		
		return checkStraightAcesLow(cards);
	}
	
	/**
	 * Check that ALL the cards in a set are of the same suit with no required
	 * minimum number of cards, ie one card is a flush.
	 * 
	 * @param cards
	 * @return
	 */
	public TelesinaHandStrength checkFlush(List<Card> cards) {
		return checkFlush(cards, 1);
	}
	
	public TelesinaHandStrength checkFlush(List<Card> cards, int minimumLength) {
		if (cards.size() < minimumLength) {
			return null;
		}
		
		Suit lastSuit = null;

		for (Card card : cards) {
			if (lastSuit != null && !card.getSuit().equals(lastSuit)) {
				return null;
			}
			lastSuit = card.getSuit();
		}

		return new TelesinaHandStrength(HandType.FLUSH, cards, Collections.EMPTY_LIST, cards);
	}
	
	/**
	 * Check that ALL cards in a set for a straight flush.
	 * No cards stripped from deck, aces may NOT be low, straight length
	 * not bound (ie one card is a straight). 
	 */
	public TelesinaHandStrength checkStraightFlush(List<Card> cards) {
		return checkStraightFlush(cards, 1, false);
	}
	
	/**
	 * Checks to see if ALL cards (any number) supplied form a straight flush.
	 * Deck may be stripped and aces may or may not be used as low card.
	 * 
	 * @param cards The cards to check
	 * @param minimumLength The minimum length required for a set of card to count as a straight
	 * @param allowLowAces 
	 * @return
	 */
	public TelesinaHandStrength checkStraightFlush(List<Card> cards, int minimumLength, boolean allowLowAces) {
		if (checkFlush(cards) == null) {
			return null;
		}
		
		TelesinaHandStrength checkStraight = checkStraight(cards, minimumLength, allowLowAces);
		
		if (checkStraight == null) {
			return null;
		}
		
		return new TelesinaHandStrength(HandType.STRAIGHT_FLUSH, cards, Collections.EMPTY_LIST, cards);
	}

	/**
	 * Check if there is a set of count cards of a kind in the list of cards.
	 * 
	 * @param cards
	 * @param count
	 * @return A list containing the set of cards-of-a-kind with highest Rank exceeding count. NOTE a query to
	 *  list a set of 2 cards (a pair) may return a list of 2 OR MORE cards if the highest set of at least
	 *  two cards contains more than two cards. Returns null if no set is found.
	 */
	private List<Card> checkManyOfAKind(List<Card> cards, int count) {
		Map<Rank, List<Card>> sets = new HashMap<Rank, List<Card>>();

		for (Card c : cards) {
			if (!sets.containsKey(c.getRank())) {
				sets.put(c.getRank(), new LinkedList<Card>());
			}
			sets.get(c.getRank()).add(c);
		}
		
		List<Card> result = null;
		
		for (Rank rank : Rank.values()) {
			if (sets.containsKey(rank) && sets.get(rank).size() >= count) {
				result = sets.get(rank);
			}
		}
		
		return result;
	}

	private TelesinaHandStrength checkStraightAcesHigh(List<Card> cards) {
		
		// arbitrarily refuse to recognise one card straights
		if (cards.size() < 2) {
			return null;
		}
		
		List<Card> cc = new LinkedList<Card>(cards);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH);
		
		Iterator<Card> iter = cc.iterator();
		Rank last = iter.next().getRank();
		while (iter.hasNext()) {
			Rank next = iter.next().getRank();
			if (next.ordinal() != last.ordinal() + 1) {
				return null;
			}
			last = next;
		}
		
		List<Card> firstKicker = Arrays.asList(cc.get(cc.size() - 1));
		List<Card> secondKicker = Arrays.asList(cc.get(cc.size() - 2));
		return new TelesinaHandStrength(HandType.STRAIGHT, firstKicker, secondKicker, Collections.EMPTY_LIST);
	}

	private TelesinaHandStrength checkStraightAcesLow(List<Card> cards) {
		// arbitrarily refuse to recognise one card straights
		if (cards.size() < 2) {
			return null;
		}
		
		List<Card> cc = new LinkedList<Card>(cards);
		Collections.sort(cc, ByRankCardComparator.ACES_LOW);
		
		Iterator<Card> iter = cc.iterator();
		Rank last = iter.next().getRank();
		while (iter.hasNext()) {
			Rank next = iter.next().getRank();

			// if cards not in order 
			if (next.ordinal() != last.ordinal() + 1) {
				
				// and not ACE followed by lowest card 
				if (!(last == Rank.ACE && next == deckLowestRank)) {
					return null;
				}
			}
			last = next;
		}

		List<Card> firstKicker = Arrays.asList(cc.get(cc.size() - 1));
		List<Card> secondKicker = Arrays.asList(cc.get(cc.size() - 2));
		return new TelesinaHandStrength(HandType.STRAIGHT, firstKicker, secondKicker, Collections.EMPTY_LIST);
	}
	
}
