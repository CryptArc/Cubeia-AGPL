package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.List;


public class HandStrength {
	
	private final HandType type;
	
	private Rank highestRank;
	
	private Rank secondRank;
	
	/** Ordered list of kicker cards, highest first */
	private List<Card> kickerCards = new ArrayList<Card>();
	
	public HandStrength(HandType type) {
		this.type = type;
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
}
