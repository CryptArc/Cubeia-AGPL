package com.cubeia.poker.hand;

import static java.util.Collections.reverseOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TODO: we should consider making hand immutable
 * @author w
 *
 */
public class Hand implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static int MAX_CARDS = 7;
	
	private List<Card> cards = new ArrayList<Card>();
	
	//private HandStrength handStrength = new HandStrength(HandType.NOT_RANKED);
	
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
	
	public Hand(Collection<Card> cards) {
		this.cards.addAll(cards);
	}

	/**
	 * Copy constructor.
	 * @param pocketCards
	 */
	public Hand(Hand otherHand) {
	    this.cards = new ArrayList<Card>(otherHand.getCards());
	    //this.handStrength = new HandStrength(otherHand.handStrength);
    }

    @Override
	public String toString() {
		String s = new String();
		for (Card card : cards) {
			s += card.toString()+" ";
		}
		return s;
	}
	
    /**
     * Will return a defensive copy of the cards. 
     * Changes to the returned list are not reflected in the list 
     * contained in this hand object.
     * 
     * @return List of cards, never null.
     */
	public List<Card> getCards() {
		return new ArrayList<Card>(cards);
	}

	/**
	 * Add a card to the hand.
	 * @param card card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
	}
	
    public void addCards(Collection<Card> cardsToAdd) {
        cards.addAll(cardsToAdd);
    }
	
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
	/*
	public HandStrength getHandStrength() {
		return handStrength;
	}
	
	public void setHandStrength(HandStrength handStrength) {
		this.handStrength = handStrength;
	}*/

    public void clear() {
        cards.clear();
    }

	public int getNumberOfCards() {
		return cards.size();
	}

}
