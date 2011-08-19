package com.cubeia.poker.hand;

/**
 * <p>Simple playing card.</p>
 * 
 * <p>This class implements Comparable which will sort the cards according
 * to rank first and suit secondly. The suits are sorted according to the
 * ordinals of the suits as defined in the Suit enum class.</p> 
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class Card implements Comparable<Card> {
	
	private final Suit suit;
	
	private final Rank rank;
	
	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	/**
	 * Shorthand value, 
	 * e.g. AS = Ace of Spades, 5c = Five of Clubs etc.
	 * 
	 * @param s
	 */
	public Card(String s) {
		 rank = Rank.fromShortString(s.charAt(0));
		 suit = Suit.fromShortString(s.charAt(1));
	}
	
	public String toString() {
		return rank.toShortString()+suit.toShortString();
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public Suit getSuit() {
		return suit;
	}

	@Override
	public int compareTo(Card other) {
		int comp = other.getRank().ordinal()*1000- rank.ordinal()*1000;
		comp += other.getSuit().ordinal() - suit.ordinal();
		return comp;
	}
	
}
