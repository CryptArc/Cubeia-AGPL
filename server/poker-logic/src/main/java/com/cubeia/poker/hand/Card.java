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
public class Card {
	
	private final Suit suit;
	
	private final Rank rank;
	
	private final Integer id;
	
	/**
	 * Creates an anonymous card.
	 * @param rank the rank
	 * @param suit the suid
	 */
	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
		this.id = null;
	}
	
	/**
	 * Creates an identifiable card.
	 * @param id id of the card
	 * @param rank the rank
	 * @param suit the suit
	 */
    public Card(Integer id, Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.id = id;
    }
	
	/**
	 * Shorthand value, 
	 * e.g. AS = Ace of Spades, 5c = Five of Clubs etc.
	 * 
	 * @param s
	 */
	public Card(String s) {
	    this(null, s);
	}
	
	public Card(Integer id, String s) {
        this.rank = Rank.fromShortString(s.charAt(0));
        this.suit = Suit.fromShortString(s.charAt(1));
        this.id = id;
    }

    public Card makeCopyWithId(int id) {
        return new Card(id, rank, suit);
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
	
	public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result + ((suit == null) ? 0 : suit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (rank != other.rank)
            return false;
        if (suit != other.suit)
            return false;
        return true;
    }

	
}
