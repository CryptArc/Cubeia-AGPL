package com.cubeia.poker.hand.calculator;

import org.junit.Test;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;


public class HandCalculatorSpeedTest {

	HandCalculator calc = new TexasHoldemHandCalculator();
	
	@Test
	public void testRankHand_1() throws Exception {
		Hand hand = new Hand("2S 3H TD JD TH");
		int iterations = 1000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			calc.getHandStrength(hand);
		}
		long elapsed = System.currentTimeMillis() - start;
		System.out.println(iterations+" iterations for getHandStrength (static hand ) took: "+elapsed+"ms.");
		// 2011-08-19 : ca 8ms   - No checkHighCard
		// 2011-08-19 : ca 15ms  - Added checkHighCard 
	}

	
	
}
