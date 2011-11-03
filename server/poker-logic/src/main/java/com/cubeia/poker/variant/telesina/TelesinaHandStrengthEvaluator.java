package com.cubeia.poker.variant.telesina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

@SuppressWarnings("unchecked")
public class TelesinaHandStrengthEvaluator implements HandTypeEvaluator, Serializable {

	private static final long serialVersionUID = 1L;

	private HandTypeCheckCalculator typeCalculator;
	
	/**
	 * Create a hand strength evaluator fo a given telesina deck. A 
	 * lowest Rank of Rank.TWO corresponds to a full deck.
	 * 
	 * @param deckLowestRank
	 */
	public TelesinaHandStrengthEvaluator(Rank deckLowestRank) {
		typeCalculator = new HandTypeCheckCalculator(deckLowestRank);
	}

	@Override
	public HandInfo getBestHandInfo(Hand hand) {
		return getBestHandStrength(hand);
	}
	
	private List<Card> findBestHand(Hand hand) {
		TelesinaHandComparator comp = new TelesinaHandComparator(this);
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
	 * Find the strength of the best hand that can be built using a list of cards.
	 * 
	 * @param cards
	 * @return
	 */
	public HandStrength getBestHandStrength(Hand hand) {
		List<Card> cards = hand.getCards();
		
		if (cards.size() > 5) {
			cards = findBestHand(hand);
			hand = new Hand(cards);
		}
		
		HandStrength strength = null;
		
		// STRAIGHT_FLUSH
		if (strength == null) {
			strength = checkStraightFlush(hand, 5);
		}
		
		// FOUR_OF_A_KIND
		if (strength == null) {
			strength = typeCalculator.checkManyOfAKind(hand, 4);
		}
		
		// FLUSH
		if (strength == null) {
			strength = checkFlush(hand, 5);
		}
		
		// FULL_HOUSE
		if (strength == null) {
			strength = typeCalculator.checkFullHouse(hand);
		}
		
		// STRAIGHT
		if (strength == null) {
			strength = checkStraight(hand, 5);
		}

		// THREE_OF_A_KIND
		if (strength == null) {
			strength = typeCalculator.checkManyOfAKind(hand, 3);
		}
		
		// TWO_PAIRS
		if (strength == null) {
			strength = typeCalculator.checkTwoPairs(hand);
		}
		
		// ONE_PAIR
		if (strength == null) {
			strength = typeCalculator.checkManyOfAKind(hand, 2);
		}
		
		// HIGH_CARD
		if (strength == null) {
			strength = checkHighCard(cards);
		}
		
		if (strength == null) {
			strength = new HandStrength(HandType.NOT_RANKED);
		}
		
		return strength;
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
		return new HandStrength(HandType.HIGH_CARD, cc, cc);
	}


	
	/**
	 * Checks to see if ALL cards (any number) supplied form a straight, aces low allowed.
	 * Deck may be stripped.
	 * 
	 * @param cards The cards to check
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
	 * <p>Check if all cards in a set are of the same suit</p>
	 * 
	 * @param Hand, the hand to check
	 * @param, minimumLength, minimum amount of cards for a flush
	 * 
	 * @return HandStrength, null if no valid flush
	 *  
	 * FIXME: See if we can break out the minimum length to the type calculator instead.
	 */
	public HandStrength checkFlush(Hand hand, int minimumLength) {
		if (hand.getNumberOfCards() < minimumLength) {
			return null;
		}
		
		HandStrength checkFlush = typeCalculator.checkFlush(hand);
		return checkFlush;
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
	public HandStrength checkStraightFlush(Hand hand, int minimumLength) {
		if (checkFlush(hand, minimumLength) == null) {
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
	
	
//	public HandStrength checkFlush(Hand hand) {
//		return checkFlush(hand, 1);
//	}
	
//	public HandStrength checkFlush(List<Card> cards, int minimumLength) {
//		if (cards.size() < minimumLength) {
//			return null;
//		}
//		
//		Suit lastSuit = null;
//
//		for (Card card : cards) {
//			if (lastSuit != null && !card.getSuit().equals(lastSuit)) {
//				return null;
//			}
//			lastSuit = card.getSuit();
//		}
//		
//		List<Card> sorted = new ArrayList<Card>(cards);
//		Collections.sort(sorted, ByRankCardComparator.ACES_HIGH_DESC);
//		return new HandStrength(HandType.FLUSH, sorted, 0, cards);
//	}

//	/**
//	 * Check if there is a set of count cards of a kind in the list of cards.
//	 * 
//	 * @param cards
//	 * @param count
//	 * @return A list containing the set of cards-of-a-kind with highest Rank exceeding count. NOTE a query to
//	 *  list a set of 2 cards (a pair) may return a list of 2 OR MORE cards if the highest set of at least
//	 *  two cards contains more than two cards. Returns null if no set is found.
//	 */
//	private List<Card> checkManyOfAKind(List<Card> cards, int count) {
//		Map<Rank, List<Card>> sets = new HashMap<Rank, List<Card>>();
//
//		for (Card c : cards) {
//			if (!sets.containsKey(c.getRank())) {
//				sets.put(c.getRank(), new LinkedList<Card>());
//			}
//			sets.get(c.getRank()).add(c);
//		}
//		
//		List<Card> result = null;
//		
//		for (Rank rank : Rank.values()) {
//			if (sets.containsKey(rank) && sets.get(rank).size() >= count) {
//				result = sets.get(rank);
//			}
//		}
//		
//		return result;
//	}

//	private HandStrength checkStraightAcesHigh(Hand hand) {
//		
//		// arbitrarily refuse to recognise one card straights
//		if (hand.getNumberOfCards() < 2) {
//			return null;
//		}
//		
//		return typeCalculator.checkStraight(hand);
		
//		List<Card> cc = new LinkedList<Card>(cards);
//		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_ASC);
//		
//		Iterator<Card> iter = cc.iterator();
//		Rank last = iter.next().getRank();
//		while (iter.hasNext()) {
//			Rank next = iter.next().getRank();
//			if (next.ordinal() != last.ordinal() + 1) {
//				return null;
//			}
//			last = next;
//		}
//		
//		List<Card> firstKicker = Arrays.asList(cc.get(cc.size() - 1));
//		List<Card> secondKicker = Arrays.asList(cc.get(cc.size() - 2));
//		
//		
//		return checkFlush;
//	}

//	private HandStrength checkStraightAcesLow(Hand hand) {
//		// arbitrarily refuse to recognise one card straights
//		if (hand.getNumberOfCards() < 2) {
//			return null;
//
//		}
//
//		return typeCalculator.checkStraight(hand, true);
//		
//		List<Card> cc = new LinkedList<Card>(cards);
//		Collections.sort(cc, ByRankCardComparator.ACES_LOW_ASC);
//		
//		Iterator<Card> iter = cc.iterator();
//		Rank last = iter.next().getRank();
//		while (iter.hasNext()) {
//			Rank next = iter.next().getRank();
//
//			
//			// if cards not in order 
//			if (next.ordinal() != last.ordinal() + 1) {
//				
//				// and not ACE followed by lowest card 
//				if (!(last == Rank.ACE && next == deckLowestRank)) {
//					return null;
//				}
//			}
//			last = next;
//			
//		}
//
//		List<Card> firstKicker = Arrays.asList(cc.get(cc.size() - 1));
//		List<Card> secondKicker = Arrays.asList(cc.get(cc.size() - 2));
//		return new HandStrength(HandType.STRAIGHT, cc, firstKicker, secondKicker);
//	}
	
//	/**
//	 * Check if there is at least one set of at least two cards.
//	 * @param cards
//	 * @return A HandStrength of type ONE_PAIR
//	 */
//	public HandStrength checkPair(Hand hand) {
//		
//		return typeCalculator.checkManyOfAKind(hand, 2);
//		
//		List<Card> cc = new LinkedList<Card>(cards);
//		List<Card> setCards = checkManyOfAKind(cc, 2);
//		
//		if (setCards == null) {
//			return null;
//		}
//		
//		cc.removeAll(setCards);
//		
//		// Make a list of the used cards in an ordered fashion
//		List<Card> usedCards = new LinkedList<Card>(setCards);
//		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
//		usedCards.addAll(cc);
//		
//
//		// pairs are compared by
//		//  1) pair rank (unsuited so hard code HEART)
//		//  2) kickers
//		//  3) suit of pair cards
//		return new HandStrength(HandType.PAIR, usedCards, 
//				Arrays.asList(new Card(setCards.get(0).getRank(), Suit.HEARTS)),
//				cc,
//				setCards);
//	}
	
//	/**
//	 * Check if there are at least two sets of at least two cards.
//	 * @param cards
//	 * @return
//	 */
//	public HandStrength checkTwoPair(List<Card> cards) {
//		
//		return typeCalculator.checkTwoPairs(new Hand(cards));
//		
//		List<Card> cc = new LinkedList<Card>(cards);
//		
//		List<Card> highSet = checkManyOfAKind(cc, 2);
//		if (highSet == null) {
//			return null;
//		}
//		
//		cc.removeAll(highSet);
//		
//		List<Card> lowSet = checkManyOfAKind(cc, 2);
//		if (lowSet == null) {
//			return null;
//		}
//		
//		cc.removeAll(lowSet);
//		
//		// pairs are compared by
//		//  1) high pair rank (unsuited so hard code HEART)
//		//  2) low pair rank (unsuited so hard code HEART)
//		//  3) kicker
//		//  4) suit of high pair 
//		return new HandStrength(HandType.TWO_PAIRS, cards,
//				Arrays.asList(new Card(highSet.get(0).getRank(), Suit.HEARTS)),
//				Arrays.asList(new Card(lowSet.get(0).getRank(), Suit.HEARTS)),
//				cc,
//				highSet);
//	}
	
//	/**
//	 * Check if there is at least one set of three cards and at least one other set of at least two cards.
//	 */
//	public HandStrength checkFullHouse(List<Card> cards) {
//		List<Card> cc = new LinkedList<Card>(cards);
//		
//		List<Card> threeSet = checkManyOfAKind(cc, 3);
//		if (threeSet == null) {
//			return null;
//		}
//		cc.removeAll(threeSet);
//		
//		List<Card> twoSet = checkManyOfAKind(cc, 2);
//		if (twoSet == null) {
//			return null;
//		}
//		cc.removeAll(twoSet);
//		
//		// Make a list of the used cards in an ordered fashion
//		List<Card> usedCards = new LinkedList<Card>(threeSet);
//		usedCards.addAll(twoSet);
//		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
//		usedCards.addAll(cc);
//
//		return new HandStrength(HandType.FULL_HOUSE, usedCards, threeSet);
//	}
	
//	/**
//	 * Check if there is at least one three of a kind in a list of cards.
//	 * @param cards
//	 * @return
//	 */
//	public HandStrength checkThreeOfAKind(Hand hand) {
//		List<Card> cc = new LinkedList<Card>(cards);
//		
//		List<Card> threeSet = checkManyOfAKind(cc, 3);
//		if (threeSet == null) {
//			return null;
//		}
//		
//		cc.removeAll(threeSet);
//		
//		// Make a list of the used cards in an ordered fashion
//		List<Card> usedCards = new LinkedList<Card>(threeSet);
//		Collections.sort(cc, ByRankCardComparator.ACES_HIGH_DESC);
//		usedCards.addAll(cc);
//		
//		return new HandStrength(HandType.THREE_OF_A_KIND, usedCards, threeSet, cc);
//	}
	
	
//	/**
//	 * Check if there is at least one four of a kind in a list of cards.
//	 * @param cards
//	 * @return
//	 */
//	public HandStrength checkFourOfAKind(List<Card> cards) {
//		List<Card> cc = new LinkedList<Card>(cards);
//		
//		List<Card> fourSet = checkManyOfAKind(cc, 4);
//		if (fourSet == null) {
//			return null;
//		}
//		
//		List<Card> kickers = new ArrayList<Card>(cards);
//		kickers.removeAll(fourSet);
//		
//		List<Card> usedCards = new ArrayList<Card>(fourSet);
//		usedCards.addAll(kickers);
//		
//		return new HandStrength(HandType.FOUR_OF_A_KIND, usedCards, fourSet, kickers);
//	}
	
}
