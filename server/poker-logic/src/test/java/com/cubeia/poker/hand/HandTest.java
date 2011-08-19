package com.cubeia.poker.hand;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class HandTest {

	@Test
	public void testHandFromString() {
		Hand hand = new Hand("AS 5c kh");
		List<Card> cards = hand.getCards();
		Assert.assertEquals(3, cards.size());
		Assert.assertEquals("AS 5C KH ", hand.toString());
	}
	
	@Test
	public void testSortCards() throws Exception {
		Hand hand = new Hand("5C 3C 6C 2C 4C");
		hand = hand.sort();
		Assert.assertEquals("6C 5C 4C 3C 2C ", hand.toString());
		
		hand = new Hand("2C 2H 2D 2S 3C 3H");
		hand = hand.sort();
		Assert.assertEquals("3H 3C 2S 2H 2D 2C ", hand.toString());
		
		hand = new Hand("KC KH");
		hand = hand.sort();
		Assert.assertEquals("KH KC ", hand.toString());
	}
	
}
