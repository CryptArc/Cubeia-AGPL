package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Holds the type of hand, highest & second ranking card and kickers.</p>
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class HandStrength implements HandInfo {
	
	private final HandType type;
	
	/**
	 * The highest rank of the cards that forms this hand type
	 */
	private Rank highestRank;
	
	/**
	 * The second highest rank of the cards that forms this hand type
	 */
	private Rank secondRank;
	
	/** 
	 * Ordered list of kicker cards, highest first.
	 * If the hand type is HIGH_CARD then all cards
	 * from the hand will be held here as well as 
	 * highest rank and second rank.
	 */
	private List<Card> kickerCards = new ArrayList<Card>();
	
	/**
	 * Groups of cards used to form the hand.
	 * 
	 * E.g. for full house:
	 * groups[0] = KS KD KH
	 * groups[1] = 8H 8D 
	 */
	private List<Card>[] groups;
	
	/**
	 * All cards used in this hand
	 */
	private List<Card> cardsUsedInHand;
	
	
	
	/* ----------------------------------------------------
	 * 
	 * 	CONSTRUCTORS 
	 * 
	 * ---------------------------------------------------- */
	
	public HandStrength(HandType type) {
		this.type = type;
	}
	
	public HandStrength(HandType handType, List<Card> cardsUsedInHand, List<Card>... groups) {
		this.type = handType;
		this.cardsUsedInHand = cardsUsedInHand;
		this.groups = groups;
	}
	
	
	/* ----------------------------------------------------
	 * 
	 * 	PUBLIC METHODS 
	 * 
	 * ---------------------------------------------------- */
	
	@Override
	public String toString() {
		String groupString = "";
		if (groups != null) {
			for (List<Card> group : groups) {
				groupString += "Group:"+group;
			}
		}
		return "HandStrength type["+type+"] highestRank["+highestRank+"] secondRank["+secondRank+"] kickers["+kickerCards+"] cardsUsed["+cardsUsedInHand+"] groups["+groupString+"]";
		
	}
	
	@Override
	public HandType getHandType() {
		return type;
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

    public List<Card> getCards() {
		return cardsUsedInHand;
	}
    
    public void setCardsUsedInHand(List<Card> cardsUsedInHand) {
		this.cardsUsedInHand = cardsUsedInHand;
	}
    
    /**
     * Get a copy of the list of cards contained in the groups with given index
     * Changes to the returned list will not be reflected in the list contained
     * in the groups.
     * 
     * @param index
     * @return List of card 
     */
    public List<Card> getGroup(int index) {
    	return new ArrayList<Card>(groups[index]);
    }
    
    public void setGroups(List<Card>... groups) {
		this.groups = groups;
	}

	public int getGroupSize() {
		return groups.length;
	}
	
}
