package com.cubeia.poker.hand;

import org.junit.Assert;
import org.junit.Test;


public class HandCalculatorTest {

	HandCalculator calc = new HandCalculator();
	
	@Test
	public void testIsFlush() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		Assert.assertFalse(calc.isFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C AC");
		Assert.assertTrue(calc.isFlush(hand));
		
		hand = new Hand("2C KC");
		Assert.assertTrue(calc.isFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C AC TC");
		Assert.assertTrue(calc.isFlush(hand));
		
		hand = new Hand("2C 3C 4H");
		Assert.assertFalse(calc.isFlush(hand));
	}
	
	@Test
	public void testIsStraight() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		Assert.assertFalse(calc.isStraight(hand));
		
		hand = new Hand("2C 3C 5C 4C 6H");
		Assert.assertTrue(calc.isStraight(hand));
	}
	
	@Test
	public void testIsStraightFlush() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		Assert.assertFalse(calc.isStraightFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C 6C");
		Assert.assertTrue(calc.isStraightFlush(hand));
	}
	
	@Test
	public void testHasManyOfAKind() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		Assert.assertNull(calc.isManyOfAKind(hand, 3));
		
		hand = new Hand("2C 3C 2H 5C 2S");
		Assert.assertEquals(Rank.TWO, calc.isManyOfAKind(hand, 3));
		
		hand = new Hand("2C 2C 2H 5C 2S");
		Assert.assertEquals(Rank.TWO, calc.isManyOfAKind(hand, 3));
		
		hand = new Hand("2C 2C 2H 5C 2S");
		Assert.assertEquals(Rank.TWO, calc.isManyOfAKind(hand, 3));
		
		hand = new Hand("2C 2C 2H AC AS AH");
		Assert.assertEquals(Rank.ACE, calc.isManyOfAKind(hand, 3));
		
	}
}
