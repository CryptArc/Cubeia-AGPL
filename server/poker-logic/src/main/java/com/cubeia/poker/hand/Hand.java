package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Hand implements Comparable<Hand> {

	private static final long serialVersionUID = 1L;

	public final static int MAX_CARDS = 7;
	
	private List<Card> cards = new ArrayList<Card>();
	
	private HandStrength handStrength = new HandStrength(HandType.NOT_RANKED);
	
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

	public void addCard(Card card) {
		cards.add(card);
	}
	
	/**
	 * Sort all cards in an descending order.
	 * 
	 * @return A new hand with cards sorted. Changes to this hand are not 
	 * reflected in the supplied hand.
	 */
	public Hand sort() {
		List<Card> sortedCards = new ArrayList<Card>(cards);
		Collections.sort(sortedCards);
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

	/**
	 * Delegate this to compare hand strengths.
	 */
	@Override
	public int compareTo(Hand other) {
		return handStrength.compareTo(other.getHandStrength());
	}
}
