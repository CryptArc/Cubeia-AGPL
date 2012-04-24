package com.cubeia.poker.hand;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
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
		
		hand = new Hand("KH KC");
		hand = hand.sort();
		Assert.assertEquals("KH KC ", hand.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testHandFailFromString1() {
		new Hand("BS 5c kh");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testHandFailFromString2() {
		new Hand("AX 5c kh");
	}
	
	@Test
	public void testContainsAllCardsRegardlessOfId() {
        Hand hand = new Hand(Arrays.asList(
            new Card(1, "5C"), 
            new Card(1, "3C"), 
            new Card(1, "6C"), 
            new Card(1, "2C"), 
            new Card(1, "4C")));
        
        assertThat(hand.containsAllCardsRegardlessOfId(new Hand("6C").getCards()), is(true));
        assertThat(hand.containsAllCardsRegardlessOfId(new Hand("2C 5C").getCards()), is(true));
        assertThat(hand.containsAllCardsRegardlessOfId(new Hand("6C 2C 5C").getCards()), is(true));
        assertThat(hand.containsAllCardsRegardlessOfId(asList(new Card(34, "6C"), new Card(10, "2C"))), is(true));
        
        assertThat(hand.containsAllCardsRegardlessOfId(new Hand("6D 2C 5C").getCards()), is(false));
	}
	
	
}
