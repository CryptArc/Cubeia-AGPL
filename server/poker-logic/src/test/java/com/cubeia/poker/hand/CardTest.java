package com.cubeia.poker.hand;

import org.junit.Assert;
import org.junit.Test;


public class CardTest {
	
	@Test
	public void testToString() throws Exception {
		Card card = new Card(Rank.ACE, Suit.SPADES);
		Assert.assertEquals("AS", card.toString());
		
		card = new Card(Rank.FIVE, Suit.CLUBS);
		Assert.assertEquals("5C", card.toString());
	}
	
	@Test
	public void testFromString() throws Exception {
		Card card = new Card("AS");
		Assert.assertEquals(Rank.ACE, card.getRank());
		Assert.assertEquals(Suit.SPADES, card.getSuit());
		
		card = new Card("5c");
		Assert.assertEquals(Rank.FIVE, card.getRank());
		Assert.assertEquals(Suit.CLUBS, card.getSuit());
	}
	
	@Test
	public void testEquals() throws Exception {
		Card card1 = new Card(Rank.ACE, Suit.SPADES);
		Card card2 = new Card(Rank.ACE, Suit.SPADES);
		Assert.assertEquals(card1, card2);
	}
}
