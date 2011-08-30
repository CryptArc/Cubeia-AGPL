package com.cubeia.poker.hand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class CombinatorTest {

	@Test
	public void testCombinator_1() throws Exception {
		List<Card> set = new Hand("2s 3s 4s").getCards();
		Combinator<Card> combinator = new Combinator<Card>(set, 2);
		List<List<Card>> combinations = combinator.getAsList();

//		for (List<Card> cards : combinations) {
//			System.out.println(cards);
//		}

		assertEquals(3, combinations.size());

		assertTrue( findInCombination(combinations, new Hand("2s 3s")) );
		assertTrue( findInCombination(combinations, new Hand("2s 4s")) );
		assertTrue( findInCombination(combinations, new Hand("3s 4s")) );
		assertFalse( findInCombination(combinations, new Hand("2s 2s")) );
		assertFalse( findInCombination(combinations, new Hand("4s 3s")) );
	}

	@Test
	public void testCombinator_2() throws Exception {
		List<Card> set = new Hand("2s 3s 4s 5s 6s 7s 8s").getCards();
		Combinator<Card> combinator = new Combinator<Card>(set, 5);
		List<List<Card>> combinations = combinator.getAsList();

//		for (List<Card> cards : combinations) {
//			System.out.println(cards);
//		}

		assertEquals(21, combinations.size());
		assertTrue( findInCombination(combinations, new Hand("2s 3s 6s 7s 8s")) );
		assertTrue( findInCombination(combinations, new Hand("2s 3s 4s 7s 8s")) );
	}

	private boolean findInCombination(List<List<Card>> combinations, Hand hand) {
		boolean equals = false;
		for (List<Card> cards : combinations) {
			equals |= Arrays.equals(cards.toArray(), hand.getCards().toArray());

		}
		return equals;
	}
}
