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
