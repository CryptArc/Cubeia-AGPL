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

import junit.framework.TestCase;

public class HandTest extends TestCase {

	private Hand hand;
	
	protected void setUp() throws Exception {
		hand = new Hand();
		hand.addCard(new Card(Card.KING, Card.SPADES));
		hand.addCard(new Card(Card.ACE, Card.SPADES));
		hand.addCard(new Card(Card.QUEEN, Card.SPADES));
		hand.addCard(new Card(Card.JACK, Card.SPADES));
		hand.addCard(new Card(Card.TEN, Card.SPADES));
	}
	
	public void testSize() {
		assertEquals(5, hand.size());
	}

	public void testRemoveCard() {
		hand.removeCard();
		assertEquals(4, hand.size());
		assertEquals("Ks As Qs Js", hand.toString().trim());
	}

	public void testAddCardCard() {
		hand.addCard(new Card(Card.ACE, Card.HEARTS));
		assertEquals(6, hand.size());
		assertEquals("Ks As Qs Js Ts Ah", hand.toString().trim());
	}

	public void testSort() {
		hand.sort();
		assertEquals("As Ks Qs Js Ts", hand.toString().trim());
		
		hand.addCard(new Card(Card.ACE, Card.HEARTS));
		assertEquals("As Ks Qs Js Ts Ah", hand.toString().trim());
	}

}
