package com.cubeia.poker.hand.calculator;

import static com.cubeia.poker.hand.HandType.*;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.HandTypeEvaluator;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

/**
 * <p>Texas Holdem implementation of a Hand Calculator. This is probably
 * the common calculations for most poker games, but variations do exists.</p>
 * 
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class TexasHoldemHandCalculator implements HandCalculator, HandTypeEvaluator {

	@Override
	public HandType getHandType(Hand hand) {
		return getHandStrength(hand).getHandType();
	}
	
	/* ----------------------------------------------------
	 * 	
	 * 	PUBLIC METHODS
	 *  
	 *  ---------------------------------------------------- */
	
	/* (non-Javadoc)
	 * @see com.cubeia.poker.hand.calculator.HandCalculator#getHandStrength(com.cubeia.poker.hand.Hand)
	 */
	@Override
	public HandStrength getHandStrength(Hand hand) {
		HandStrength strength = null;
		
		// STRAIGHT_FLUSH
		if (strength == null) {
			strength = checkStraightFlush(hand);
		}
		
		// FOUR_OF_A_KIND
		if (strength == null) {
			strength = checkManyOfAKind(hand, 4);
		}
		
		// FULL_HOUSE
		if (strength == null) {
			strength = checkFullHouse(hand);
		}
		
		// FLUSH
		if (strength == null) {
			strength = checkFlush(hand);
		}
		
		// STRAIGHT
		if (strength == null) {
			strength = checkStraight(hand);
		}

		// THREE_OF_A_KIND
		if (strength == null) {
			strength = checkManyOfAKind(hand, 3);
		}
		
		// TWO_PAIRS
		if (strength == null) {
			strength = checkTwoPairs(hand);
		}
		
		// ONE_PAIR
		if (strength == null) {
			strength = checkManyOfAKind(hand, 2);
		}
		
		// HIGH_CARD
		if (strength == null) {
			strength = checkHighCard(hand);
		}
		
		if (strength == null) {
			strength = new HandStrength(HandType.NOT_RANKED);
		}
		
		return strength;
	}
	
	
	/* ----------------------------------------------------
	 * 	
	 * 	INSPECT HAND METHODS
	 * 
	 * 	Inspect hand for specific hand types and get the 
	 *  corresponding hand strength. 
	 *  
	 *  ---------------------------------------------------- */
	

	protected HandStrength checkStraightFlush(Hand hand) {
		HandStrength strength = null;
		if (checkFlush(hand) != null && checkStraight(hand) != null) {
			strength = new HandStrength(HandType.STRAIGHT_FLUSH);
			strength.setHighestRank(hand.sort().getCards().get(0).getRank());
		}
		return strength;
	}
	
	/**
	 * Checks if all cards are the same suit, regardless of the number of cards.
	 */
	protected HandStrength checkFlush(Hand hand) {
		if (hand.getCards().isEmpty()) {
			return null;
		}
		
		boolean flush = true;
		Suit lastSuit = null;
		HandStrength strength = null;
		for (Card card : hand.getCards()) {
			if (lastSuit != null && !card.getSuit().equals(lastSuit)) {
				flush = false;
				break;
			}
			lastSuit = card.getSuit();
		}
		if (flush) {
			strength = new HandStrength(FLUSH);
			strength.setHighestRank(hand.sort().getCards().get(0).getRank());
		}
		
		return strength;
	}

	/**
	 * Checks if all cards are a straight, regardless of the number of cards.
	 * Assumes that you have executed a sort (Hand.sortAscending) on the hand first!
	 */
	protected HandStrength checkStraight(Hand hand) {
		
		if (hand.getCards().isEmpty()) {
			return null;
		}
		
		List<Card> cards = hand.sort().getCards();
		HandStrength strength = null;
		boolean straight = true;
		Rank lastRank = null;
		for (Card card : cards) {
			if (lastRank != null) {
				if (card.getRank().ordinal() != lastRank.ordinal() - 1 ) {
					straight = false;
					break;
				}
			}
			lastRank = card.getRank();
		}
		if (straight) {
			strength = new HandStrength(STRAIGHT);
			strength.setHighestRank(cards.get(0).getRank());
		}
		return strength;
	}

	
	
	/**
	 * Check for three and four of a kind. Will return with the 
	 * highest rank that matches the number of cards that is looked for.
	 * 
	 * @param hand
	 * @param number, number of same rank to look for, i.e. 3 = three of a kind
	 * @return the highest match found or null if not found
	 */
	protected HandStrength checkManyOfAKind(Hand hand, int number) {
		List<Card> cards = hand.sort().getCards();
		
		HandStrength strength = null;
		Rank lastRank = null;
		int count = 1;
		
		for (Card card : cards) {
			if (lastRank != null) {
				if (card.getRank().ordinal() == lastRank.ordinal()) {
					// We have found another card with the same rank.
					count++;
					if (count == number) {
						strength = new HandStrength(getType(number));
						strength.setHighestRank(card.getRank());
						// Get kicker cards
						List<Card> kickers = removeAllRanks(card.getRank(), cards);
						strength.setKickerCards(kickers);
						
						break; // Break since we are starting with highest rank
					}
				} else {
					// Not a match so reset the counter
					count = 1;
				}
			}
			lastRank = card.getRank();
		}
		return strength;
	}

	protected HandStrength checkFullHouse(Hand hand) {
		return checkDoubleManyCards(hand, 3);
	}

	protected HandStrength checkTwoPairs(Hand hand) {
		return checkDoubleManyCards(hand, 2);
	}

	/**
	 * 
	 * @param hand
	 * @param number, the number to check highest multiple. I.e. 2 = two pair, 3 = full house
	 * @return
	 */
	private HandStrength checkDoubleManyCards(Hand hand, int number) {
		HandStrength strength = null;
		HandStrength firstPair = checkManyOfAKind(hand, number);
		if (firstPair != null) {
				
			List<Card> cards = removeAllRanks(firstPair.getHighestRank(), hand.getCards());
			
			Hand secondPairHand = new Hand(cards);
			HandStrength secondPair = checkManyOfAKind(secondPairHand, 2);
			
			if (secondPair != null) {
				if (number == 2) {
					strength = new HandStrength(TWO_PAIRS);
				} else if (number == 3) {
					strength = new HandStrength(FULL_HOUSE);
				}
				strength.setHighestRank(firstPair.getHighestRank());
				strength.setSecondRank(secondPair.getHighestRank());
				List<Card> kickers = removeAllRanks(secondPair.getHighestRank(), secondPairHand.getCards());
				strength.setKickerCards(kickers);
			}
		}
		return strength;
	}
	
	protected HandStrength checkHighCard(Hand hand) {
		
		if (hand.getCards().isEmpty()) {
			return null;
		}
		
		HandStrength strength = new HandStrength(HIGH_CARD);
		Hand sorted = hand.sort();
		strength.setHighestRank(sorted.getCardAt(0).getRank());
		strength.setSecondRank(sorted.getCardAt(1).getRank());
		strength.setKickerCards(sorted.getCards());
		return strength;
	}
	
	
	/* ----------------------------------------------------
	 * 	
	 * 	PRIVATE METHODS
	 *  
	 *  ---------------------------------------------------- */
	
	private List<Card> removeAllRanks(Rank rank, List<Card> cards) {
		List<Card> result = new ArrayList<Card>();
		for (Card card : cards) {
			if (!card.getRank().equals(rank)) {
				result.add(card);
			}
		}
		return result;
	}


	private HandType getType(int number) {
		switch (number) {
			case 2: return ONE_PAIR;
			case 3: return THREE_OF_A_KIND;
			case 4: return HandType.FOUR_OF_A_KIND;
			default: throw new IllegalArgumentException("Invalid number of cards for hand type");
		}
	}

	
}
