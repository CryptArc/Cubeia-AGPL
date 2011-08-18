package ca.ualberta.cs.poker;

import org.junit.Assert;
import org.junit.Test;


public class HandTest {
	
	@Test
	public void testGetCard() throws Exception {
		Deck deck = new Deck();
		Hand hand = new Hand();
		
		Card card0 = deck.deal(); // ID = 0
		Assert.assertEquals(0, card0.getDeckId());
		
		Card card1 = deck.deal(); // ID = 1
		Assert.assertEquals(1, card1.getDeckId());
		
		hand.addCard(card1);
		
		Card card = hand.getCard(1);
		// FIXME: Fix test with proper deck ID:s
		// Assert.assertEquals(1, card.getDeckId());
	}
	
}
