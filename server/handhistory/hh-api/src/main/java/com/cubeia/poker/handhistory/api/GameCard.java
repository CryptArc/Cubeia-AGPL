package com.cubeia.poker.handhistory.api;

public class GameCard {

	public enum Rank {
		TWO, 
		THREE, 
		FOUR, 
		FIVE, 
		SIX, 
		SEVEN, 
		EIGHT, 
		NINE, 
		TEN, 
		JACK, 
		QUEEN, 
		KING, 
		ACE
	}
	
	public enum Suit {
	    CLUBS,
	    DIAMONDS, 
	    HEARTS, 
	    SPADES;
	}
	
	private final Suit suit;
	private final Rank rank;
	
	public GameCard(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public Suit getSuit() {
		return suit;
	}
}
