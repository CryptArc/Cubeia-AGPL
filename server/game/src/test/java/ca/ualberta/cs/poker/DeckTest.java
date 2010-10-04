package ca.ualberta.cs.poker;

import junit.framework.TestCase;

public class DeckTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDeal() {
		Deck deck = new Deck();
		
		for (int i = 0; i < 52; i++) {
			Card card = deck.deal();
			assertNotNull(card);
		}
	}

}
