/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.ualberta.cs.poker;

/***************************************************************************
Copyright (c) 2000:
      University of Alberta,
      Deptartment of Computing Science
      Computer Poker Research Group

    See "Liscence.txt"
 ***************************************************************************/

import java.io.Serializable;

import com.cubeia.poker.rng.MersenneTwisterFast;

/**
 *  A Deck of 52 Cards which can be dealt and shuffled
 *  
 *  Part of replicated game state
 *  
 *  @author  Aaron Davidson
 */

public class Deck implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NUM_CARDS = 52;
	private Card[] gCards = new Card[NUM_CARDS];
	private char position; // top of deck
	private MersenneTwisterFast r = new MersenneTwisterFast();

	/**
	 * Constructor.
	 */
	public Deck() {
		position = 0;
		for (int i=0;i<NUM_CARDS;i++) {
			gCards[i] = new Card(i);
		}
	}

	/**
	 * Constructor w/ shuffle seed.
	 * @param seed the seed to use in randomly shuffling the deck.
	 */
	public Deck(long seed) {
		this();
		if (seed == 0) { 
			seed = System.currentTimeMillis();
		}
		r.setSeed(seed);
	}

	/**
	 * Places all cards back into the deck.
	 * Note: Does not sort the deck.
	 */
	public synchronized void reset() { position = 0; }

	/**
	 * Shuffles the cards in the deck.
	 */
	public synchronized void shuffle() {
		Card  tempCard;
		int   i,j;
		for (i=0; i<NUM_CARDS; i++) {
			j = i + randInt(NUM_CARDS-i);
			tempCard = gCards[j];
			gCards[j] = gCards[i];
			gCards[i] = tempCard;
		}
		position = 0;
	}

	/**
	 * Obtain the next card in the deck.
	 * If no cards remain, a null card is returned
	 * @return the card dealt
	 */
	public synchronized Card deal() {
		return (position < NUM_CARDS ? gCards[position++] : null);
	}

	/**
	 * Obtain the next card in the deck.
	 * If no cards remain, a null card is returned
	 * @return the card dealt
	 */
	public synchronized Card dealCard() {
		return extractRandomCard();
	}

	/**
	 * Find position of Card in Deck.
	 */
	public synchronized int findCard(Card c) {
		int i = position;
		int n = c.getIndex();
		while (i < NUM_CARDS && n != gCards[i].getIndex())
			i++;
		return (i < NUM_CARDS ? i : -1);
	}

	private synchronized int findDiscard(Card c) {
		int i = 0;
		int n = c.getIndex();
		while (i < position && n != gCards[i].getIndex())
			i++;  
		return (n == gCards[i].getIndex() ? i : -1);
	}

	/**
	 * Remove all cards in the given hand from the Deck.
	 */
	public synchronized void extractHand(Hand h) {
		for (int i=1;i<=h.size();i++)
			this.extractCard(h.getCard(i));
	}

	/**
	 * Remove a card from within the deck.
	 * @param c the card to remove.
	 */
	public synchronized void extractCard(Card c) {
		int i = findCard(c);
		if (i != -1) {
			Card t = gCards[i];
			gCards[i] = gCards[position];
			gCards[position] = t;
			position++;
		} else {
			System.err.println("*** ERROR: could not find card " + c);
			Thread.dumpStack();
		}
	}

	/**
	 * Remove and return a randomly selected card from within the deck.
	 */
	public synchronized Card extractRandomCard() {
		int pos = position+randInt(NUM_CARDS-position);
		Card c = gCards[pos];
		gCards[pos] = gCards[position];
		gCards[position] = c;
		position++;
		return c;
	}

	/**
	 * Return a randomly selected card from within the deck without removing it.  
	 */
	public synchronized Card pickRandomCard() {
		return gCards[position+randInt(NUM_CARDS-position)];
	}

	/**
	 * Place a card back into the deck.
	 * @param c the card to insert.
	 */
	public synchronized void replaceCard(Card c) {
		int i = findDiscard(c);
		if (i != -1) {
			position--;
			Card t = gCards[i];
			gCards[i] = gCards[position];
			gCards[position] = t;
		}
	}

	/**
	 * Obtain the position of the top card. 
	 * (the number of cards dealt from the deck)
	 * @return the top card index
	 */
	public synchronized int getTopCardIndex() {
		return position;
	}


	/**
	 * Obtain the number of cards left in the deck
	 */
	public synchronized int cardsLeft() {
		return NUM_CARDS-position;
	}

	/**
	 * Obtain the card at a specific index in the deck.
	 * Does not matter if card has been dealt or not.
	 * If i < topCardIndex it has been dealt.
	 * @param i the index into the deck (0..51)
	 * @return the card at position i
	 */
	public synchronized Card getCard(int i) {    
		return gCards[i];
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("* ");
		for (int i=0;i<position;i++)
			s.append(gCards[i].toString()+" ");
		s.append("\n* ");
		for (int i=position;i<NUM_CARDS;i++)
			s.append(gCards[i].toString()+" ");
		return s.toString();
	}

	private int randInt(int range) {
		return r.nextInt(range);
	}


}
