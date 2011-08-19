package com.cubeia.poker.hand;

import java.util.List;

/**
 * <p>Inspect and calculate what poker hands are implemented in a Hand.</p>
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class HandCalculator {

	public HandStrength getHandStrength(Hand hand) {
//		if (isStraightFlush(hand)) {
//			return HandType.STRAIGHT_FLUSH;
//			
//		} else if (isManyOfAKind(hand, 4) != null) {
//			return HandType.FOUR_OF_A_KIND;
//			
//		} else if (isFullHouse(hand)) {
//			return HandType.FULL_HOUSE;
//			
//		} else if (isFlush(hand)) {
//			return HandType.FLUSH;
//			
//		} else if (isStraight(hand)) {
//			return HandType.STRAIGHT;
//			
//		} else if (isManyOfAKind(hand, 3) != null) {
//			return HandType.THREE_OF_A_KIND;
//			
//		} else if (isTwoPairs(hand)) {
//			return HandType.TWO_PAIRS;
//			
//		} else if (isOnePair(hand)) {
//			return HandType.ONE_PAIR;
//			
//		} else {
//			return HandType.HIGH_CARD;
//		}
		return null;
	}
	
	/**
	 * Checks if all cards are the same suit, regardless of the number of cards.
	 */
	public boolean isFlush(Hand hand) {
		boolean flush = true;
		Suit lastSuit = null;
		for (Card card : hand.getCards()) {
			if (lastSuit != null && !card.getSuit().equals(lastSuit)) {
				flush = false;
				break;
			}
			lastSuit = card.getSuit();
		}
		return flush;
	}

	/**
	 * Checks if all cards are a straight, regardless of the number of cards.
	 * Assumes that you have executed a sort (Hand.sortAscending) on the hand first!
	 */
	public boolean isStraight(Hand hand) {
		List<Card> cards = hand.sort().getCards();
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
		return straight;
	}

	public boolean isStraightFlush(Hand hand) {
		return isFlush(hand) && isStraight(hand);
	}
	
	/**
	 * Check for three and four of a kind. Will return with the 
	 * highest rank that matches the number of cards that is looked for.
	 * 
	 * @param hand
	 * @param number, number of same rank to look for, i.e. 3 = three of a kind
	 * @return the highest match found or null if not found
	 */
	public Rank isManyOfAKind(Hand hand, int number) {
		List<Card> cards = hand.sort().getCards();
		
		Rank foundRank = null;
		Rank lastRank = null;
		int count = 1;
		
		for (Card card : cards) {
			if (lastRank != null) {
				if (card.getRank().ordinal() == lastRank.ordinal()) {
					// We have found another card with the same rank.
					count++;
					if (count == number) {
						foundRank = card.getRank(); 
						break; // Break since we are starting with highest rank
					}
				} else {
					// Not a match so reset the counter
					count = 1;
				}
			}
			lastRank = card.getRank();
		}
		return foundRank;
	}

	public boolean isFullHouse(Hand hand) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTwoPairs(Hand hand) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOnePair(Hand hand) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
