package ca.ualberta.cs.poker;

import org.junit.Assert;
import org.junit.Test;


public class DeckTest {
	
	@Test
	public void testUnshuffledCardID() throws Exception {
		Deck deck = new Deck();
		Card card0 = deck.deal();
		Assert.assertEquals(0, card0.getIndex());
		Assert.assertEquals(0, card0.getDeckId());
		
		Card card1 = deck.deal();
		Assert.assertEquals(1, card1.getIndex());
		Assert.assertEquals(1, card1.getDeckId());
		
		Card card2 = deck.deal();
		Assert.assertEquals(2, card2.getIndex());
		Assert.assertEquals(2, card2.getDeckId());
	}
	
	/**
	 * NOTE: This test will fail if the shuffle generates the same
	 * order of the 3 first cards as in a non-shuffled deck =/
	 * 
	 * @throws Exception
	 */
	@Test
	public void testShuffledCardID() throws Exception {
		Deck deck = new Deck();
		deck.shuffle();
		
		Card card0 = deck.deal();
		Assert.assertEquals(0, card0.getDeckId());
		
		Card card1 = deck.deal();
		Assert.assertEquals(1, card1.getDeckId());
		
		Card card2 = deck.deal();
		Assert.assertEquals(2, card2.getDeckId());
		
		Assert.assertTrue("The deck has not been shuffled it seems...", !( card0.getIndex() == 0 && card1.getIndex() == 1 && card2.getIndex() == 2 ));
	}
}
