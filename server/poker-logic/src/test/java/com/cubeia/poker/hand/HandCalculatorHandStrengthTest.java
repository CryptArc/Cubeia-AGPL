package com.cubeia.poker.hand;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;


public class HandCalculatorHandStrengthTest {

	HandCalculator calc = new HandCalculator();
	
	@Test
	public void testHandStrength_1() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C 6C");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(HandType.STRAIGHT_FLUSH, strength.getHandType());
		assertEquals(Rank.SIX, strength.getHighestRank());
	}
	
	@Test
	public void testHandStrength_2() throws Exception {
		Hand hand = new Hand("2C 2H 4C 5C 2S");
		HandStrength strength = calc.getHandStrength(hand);
		Assert.assertEquals(HandType.THREE_OF_A_KIND, strength.getHandType());
		assertEquals(Rank.TWO, strength.getHighestRank());
	}
	
	@Test
	public void testHandStrength_3() throws Exception {
		Hand hand = new Hand("2C KC 4C 5C JC");
		HandStrength strength = calc.getHandStrength(hand);
		Assert.assertEquals(HandType.FLUSH, strength.getHandType());
		assertEquals(Rank.KING, strength.getHighestRank());
	}
	
}
