package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Holds the type of hand, highest & second ranking card and kickers.</p>
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class HandStrength implements HandInfo {
	
	private final HandType type;
	
	private Rank highestRank;
	
	private Rank secondRank;
	
	/** 
	 * Ordered list of kicker cards, highest first.
	 * If the hand type is HIGH_CARD then all cards
	 * from the hand will be held here as well as 
	 * highest rank and second rank.
	 */
	private List<Card> kickerCards = new ArrayList<Card>();
	
	public HandStrength(HandType type) {
		this.type = type;
	}

	public HandStrength(HandStrength other) {
	    this.type = other.type;
	    this.highestRank = other.highestRank;
	    this.secondRank = other.secondRank;
	}
	
	public HandType getHandType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "type["+type+"]";
	}

	public Rank getHighestRank() {
		return highestRank;
	}
	
	public void setHighestRank(Rank highestRank) {
		this.highestRank = highestRank;
	}

	public Rank getSecondRank() {
		return secondRank;
	}
	
	public void setSecondRank(Rank secondRank) {
		this.secondRank = secondRank;
	}

	public List<Card> getKickerCards() {
		return kickerCards;
	}
	
	public void setKickerCards(List<Card> kickerCards) {
		this.kickerCards = kickerCards;
	}
	
	@Override
	public HandType getType() {
		return type;
	}

	public List<Card> getCards() {
		// Not used in texas hold'em implementation as of 2011-09-12 so
		// I just left it for now. / dreas
		return Collections.EMPTY_LIST;
	}
	
//	/**
//	 * <p>Compare to another hand strength with hand ranking in mind,
//	 * i.e. the strongest hand should come first.</p>
//	 * 
//	 */
//	@Override
//	public int compareTo(HandStrength other) {
//		if (!other.getHandType().equals(type)) {
//			// Different hand types so only compare type
//			return other.getHandType().ordinal() - type.ordinal();
//			
//		} else {
//			// Check highest card etc.
//			if (other.getHighestRank() != highestRank) {
//				return other.getHighestRank().ordinal() - highestRank.ordinal();
//				
//			} else if (other.getSecondRank() != secondRank) {
//				return other.getSecondRank().ordinal() - secondRank.ordinal();
//				
//			} else {
//				// Check kickers in descending order
//				for (int i = 0; i < kickerCards.size(); i++) {
//					if (other.getKickerCards().get(i).getRank() != kickerCards.get(i).getRank()) {
//						return other.getKickerCards().get(i).getRank().ordinal() - kickerCards.get(i).getRank().ordinal();
//					}
//				}
//			}
//		}
//		
//		// Same strength
//		return 0;
//	}
}
