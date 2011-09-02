package com.cubeia.poker.hand;

import static java.util.Collections.reverseOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Hand implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static int MAX_CARDS = 7;
	
	private List<Card> cards = new ArrayList<Card>();
	
//	/* cards that are public is also stored here (as well as in the cards list) */
//	private Set<Card> publicCards = new HashSet<Card>();
	
	private HandStrength handStrength = new HandStrength(HandType.NOT_RANKED);
	
	public Hand() {}
	
	public Hand(String cs) {
		StringTokenizer t = new StringTokenizer(cs," -");
		while(t.hasMoreTokens()) {
			String s = t.nextToken();
			if (s.length()==2) {
				Card c = new Card(s);
					addCard(c);
			}
		}     
	}
	
	public Hand(List<Card> cards) {
		this.cards.addAll(cards);
	}

	@Override
	public String toString() {
		String s = new String();
		for (Card card : cards) {
			s += card.toString()+" ";
		}
		return s;
	}
	
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * Add a card to the hand.
	 * @param card card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
	}
	
//	/**
//	 * Add a card to the hand with the option to 
//	 * indicated that it is public (shown to all).
//	 * @param card card to add
//	 * @param publicCard true if the card is open (public)
//	 */
//	public void addCard(Card card, boolean publicCard) {
//	    cards.add(card);
//	    publicCards.add(card);
//	}
	
    public void addCards(Collection<Card> cardsToAdd) {
        cards.addAll(cardsToAdd);
    }
    
//    public boolean isCardPublic(Card card) {
//        return publicCards.contains(card);
//    }
	
	/**
	 * Sort all cards in an descending order.
	 * 
	 * @return A new hand with cards sorted. Changes to this hand are not 
	 * reflected in the supplied hand.
	 */
	public Hand sort() {
		List<Card> sortedCards = new ArrayList<Card>(cards);
		Collections.sort(sortedCards, reverseOrder(new CardComparator()));
		return new Hand(sortedCards);
	}

	public Card getCardAt(int index) {
		return cards.get(index);
	}
	
	public HandStrength getHandStrength() {
		return handStrength;
	}
	
	public void setHandStrength(HandStrength handStrength) {
		this.handStrength = handStrength;
	}

//	/**
//	 * Delegate this to compare hand strengths.
//	 */
//	@Override
//	public int compareTo(Hand other) {
//		return handStrength.compareTo(other.getHandStrength());
//	}

    public void clear() {
        cards.clear();
    }

}
