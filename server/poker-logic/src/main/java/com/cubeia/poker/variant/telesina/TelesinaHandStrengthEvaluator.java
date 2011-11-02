package com.cubeia.poker.variant.telesina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Combinator;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.HandTypeEvaluator;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.hand.calculator.ByRankCardComparator;

@SuppressWarnings("unchecked")
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
	public HandInfo getBestHandInfo(Hand hand) {
		return getBestHandStrength(hand.getCards());
	}
	
	private List<Card> findBestHand(List<Card> cards) {
		TelesinaHandComparator comp = new TelesinaHandComparator(this);
		Combinator<Card> comb = new Combinator<Card>(cards, 5);
		List<Card> best = comb.next();

		while (comb.hasNext()) {
			List<Card> candidate = comb.next();
			if (comp.compare(candidate, best) > 0) {
				best = candidate;
			}
		}
		
		return best;
	}
	
	/**
	 * Find the strength of the best hand that can be built using a list of cards.
	 * 
	 * @param cards
	 * @return
	 */
	public HandStrength getBestHandStrength(List<Card> cards) {
		
		if (cards.size() > 5) {
			cards = findBestHand(cards);
		}
		
		HandStrength strength = checkStraightFlush(cards, 5);
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
		
		strength = checkStraight(cards, 5);
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
		
		return checkHighCard(cards);
	}
	
	/**
	 * Check if a given set of cards constitutes a high card hand.
	 * @param cards
	 * @return
	 */
    public HandStrength checkHighCard(List<Card> cards) {
		if (cards.isEmpty()) {
			return null;
		}
		
		List<Card> cc = new LinkedList<Card>(cards);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
		return new HandStrength(HandType.HIGH_CARD, cc, 0, cc);
	}

	/**
	 * Check if there is at least one set of at least two cards.
	 * @param cards
	 * @return A HandStrength of type ONE_PAIR
	 */
	public HandStrength checkPair(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		List<Card> setCards = checkManyOfAKind(cc, 2);
		
		if (setCards == null) {
			return null;
		}
		
		cc.removeAll(setCards);
		
		// Make a list of the used cards in an ordered fashion
		List<Card> usedCards = new LinkedList<Card>(setCards);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
		usedCards.addAll(cc);
		

		// pairs are compared by
		//  1) pair rank (unsuited so hard code HEART)
		//  2) kickers
		//  3) suit of pair cards
		return new HandStrength(HandType.PAIR, usedCards, 0,
				Arrays.asList(new Card(setCards.get(0).getRank(), Suit.HEARTS)),
				cc,
				setCards);
	}
	
	/**
	 * Check if there are at least two sets of at least two cards.
	 * @param cards
	 * @return
	 */
	public HandStrength checkTwoPair(List<Card> cards) {
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
		
		// pairs are compared by
		//  1) high pair rank (unsuited so hard code HEART)
		//  2) low pair rank (unsuited so hard code HEART)
		//  3) kicker
		//  4) suit of high pair 
		return new HandStrength(HandType.TWO_PAIRS, cards, 0,
				Arrays.asList(new Card(highSet.get(0).getRank(), Suit.HEARTS)),
				Arrays.asList(new Card(lowSet.get(0).getRank(), Suit.HEARTS)),
				cc,
				highSet);
	}
	
	/**
	 * Check if there is at least one set of three cards and at least one other set of at least two cards.
	 */
	public HandStrength checkFullHouse(List<Card> cards) {
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
		cc.removeAll(twoSet);
		
		// Make a list of the used cards in an ordered fashion
		List<Card> usedCards = new LinkedList<Card>(threeSet);
		usedCards.addAll(twoSet);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
		usedCards.addAll(cc);

		return new HandStrength(HandType.FULL_HOUSE, usedCards, 0, threeSet);
	}
	
	/**
	 * Check if there is at least one three of a kind in a list of cards.
	 * @param cards
	 * @return
	 */
	public HandStrength checkThreeOfAKind(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> threeSet = checkManyOfAKind(cc, 3);
		if (threeSet == null) {
			return null;
		}
		
		cc.removeAll(threeSet);
		
		// Make a list of the used cards in an ordered fashion
		List<Card> usedCards = new LinkedList<Card>(threeSet);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
		usedCards.addAll(cc);
		
		return new HandStrength(HandType.THREE_OF_A_KIND, usedCards, 0, threeSet, cc);
	}
	
	
	/**
	 * Check if there is at least one four of a kind in a list of cards.
	 * @param cards
	 * @return
	 */
	public HandStrength checkFourOfAKind(List<Card> cards) {
		List<Card> cc = new LinkedList<Card>(cards);
		
		List<Card> fourSet = checkManyOfAKind(cc, 4);
		if (fourSet == null) {
			return null;
		}
		
		List<Card> kickers = new ArrayList<Card>(cards);
		kickers.removeAll(fourSet);
		
		List<Card> usedCards = new ArrayList<Card>(fourSet);
		usedCards.addAll(kickers);
		
		return new HandStrength(HandType.FOUR_OF_A_KIND, usedCards, 0, fourSet, kickers);
	}
	
	/**
	 * Checks to see if ALL cards (any number) supplied form a straight, aces low allowed.
	 * Deck may be stripped.
	 * 
	 * @param cards The cards to check
	 * @param minimumLength The minimum length required for a set of card to count as a straight (NOTE a one card straight is never recognized) 
	 * @return
	 */
	public HandStrength checkStraight(List<Card> cards, int minimumLength) {
		
		if (cards.size() < minimumLength) {
			return null;
		}
		
		HandStrength checkStraightAcesHigh = checkStraightAcesHigh(cards);
		if (checkStraightAcesHigh != null) {
			return checkStraightAcesHigh;
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
	public HandStrength checkFlush(List<Card> cards) {
		return checkFlush(cards, 1);
	}
	
	public HandStrength checkFlush(List<Card> cards, int minimumLength) {
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
		
		List<Card> sorted = new ArrayList<Card>(cards);
		Collections.sort(sorted, ByRankCardComparator.ACES_HIGH_DESC);
		return new HandStrength(HandType.FLUSH, sorted, 0, cards);
	}
	
	/**
	 * Checks to see if ALL cards (any number) supplied form a straight flush.
	 * Deck may be stripped.
	 * 
	 * @param cards The cards to check
	 * @param minimumLength The minimum length required for a set of card to count as a straight
	 * @param allowLowAces 
	 * @return
	 */
	public HandStrength checkStraightFlush(List<Card> cards, int minimumLength) {
		if (checkFlush(cards) == null) {
			return null;
		}
		
		HandStrength checkStraight = checkStraight(cards, minimumLength);
		
		if (checkStraight == null) {
			return null;
		}
		
		List<Card> sorted = new ArrayList<Card>(cards);
		Collections.sort(sorted, ByRankCardComparator.ACES_LOW_ASC);
		return new HandStrength(HandType.STRAIGHT_FLUSH, sorted, 0, cards);
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

	private HandStrength checkStraightAcesHigh(List<Card> cards) {
		
		// arbitrarily refuse to recognise one card straights
		if (cards.size() < 2) {
			return null;
		}
		
		List<Card> cc = new LinkedList<Card>(cards);
		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_ASC);
		
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
		return new HandStrength(HandType.STRAIGHT, cc, 0, firstKicker, secondKicker);
	}

	private HandStrength checkStraightAcesLow(List<Card> cards) {
		// arbitrarily refuse to recognise one card straights
		if (cards.size() < 2) {
			return null;

		}

		
		List<Card> cc = new LinkedList<Card>(cards);
		Collections.sort(cc, ByRankCardComparator.ACES_LOW_ASC);
		
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
		return new HandStrength(HandType.STRAIGHT, cc, 0, firstKicker, secondKicker);
	}
	
}
