package com.cubeia.poker.hand;

import org.junit.Assert;
import org.junit.Test;


public class PokerEvaluatorCompareTest {

	PokerEvaluator eval = new PokerEvaluator();
	
	@Test
	public void testCompareHands_1() throws Exception {
		Hand hand1 = new Hand("AS AH 2C 3C 4C").sort();
		Hand hand2 = new Hand("AD AC 2D 3D 4D").sort();
		
		int comp = eval.compareEmptyHands(hand1, hand2);
		Assert.assertTrue(comp == 0);
	}
	
	@Test
	public void testCompareHands_2() throws Exception {
		Hand hand1 = new Hand("AS AH AC 3C 4C").sort();
		Hand hand2 = new Hand("AD AC 2D 3D 4D").sort();
		
		int comp = eval.compareEmptyHands(hand1, hand2);
		Assert.assertTrue(comp > 0);
	}
	
}
