package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class PokerEvaluatorTest {

	PokerEvaluator eval = new PokerEvaluator();
	
	@Test
	public void testHands_1() throws Exception {
		Hand hand1 = new Hand("2C 2C 2C 2H AS");
		Hand hand2 = new Hand("AC AC AC AH 2S");
		
		List<Hand> hands = new ArrayList<Hand>();
		hands.add(hand1);
		hands.add(hand2);
		
		List<Hand> ranked = eval.rankHands(hands);
		Assert.assertEquals(hand2, ranked.get(0));
		Assert.assertEquals(hand1, ranked.get(1));
	}
	
	@Test
	public void testHands_2() throws Exception {
		Hand hand1 = new Hand("AC AC AC AH 2S");
		Hand hand2 = new Hand("2C 2C 2C 2H AS");
		
		List<Hand> hands = new ArrayList<Hand>();
		hands.add(hand1);
		hands.add(hand2);
		
		List<Hand> ranked = eval.rankHands(hands);
		Assert.assertEquals(hand1, ranked.get(0));
		Assert.assertEquals(hand2, ranked.get(1));
	}
	
	@Test
	public void testHands_3() throws Exception {
		Hand hand1 = new Hand("QC QC AC AH TS"); // TWO_PAIR
		Hand hand2 = new Hand("2C 8H 2C 2C AS"); // THREE_OF_A_KIND 2's + A kicker
		Hand hand3 = new Hand("7S 2C 2C 2C 8H"); // THREE_OF_A_KIND 2's
		Hand hand4 = new Hand("4D 4S 4H 5S 5C"); // FULL_HOUSE
		
		List<Hand> hands = new ArrayList<Hand>();
		hands.add(hand1);
		hands.add(hand2);
		hands.add(hand3);
		hands.add(hand4);
		
		List<Hand> ranked = eval.rankHands(hands);
		Assert.assertEquals(hand4, ranked.get(0));
		Assert.assertEquals(hand2, ranked.get(1));
		Assert.assertEquals(hand3, ranked.get(2));
		Assert.assertEquals(hand1, ranked.get(3));
	}
	
	@Test
	public void testHands_4() throws Exception {
		Hand hand1 = new Hand("JC 2C 3C 4H JS"); // PAIR Q's + 2 kicker
		Hand hand2 = new Hand("QC 2H 3C 4C QS"); // PAIR Q's + K kicker  
		
		List<Hand> hands = new ArrayList<Hand>();
		hands.add(hand1);
		hands.add(hand2);
		
		List<Hand> ranked = eval.rankHands(hands);
		
		Assert.assertEquals(hand2, ranked.get(0));
		Assert.assertEquals(hand1, ranked.get(1));
	}
	
	@Test
	public void testHands_5() throws Exception {
		Hand hand1 = new Hand("TS TC 2C 3C 4H"); // PAIR T's + 2 kicker
		Hand hand2 = new Hand("QC 2C 3C 4H QS"); // PAIR Q's + 2 kicker
		Hand hand3 = new Hand("QC 2H 3C KC QS"); // PAIR Q's + K kicker  
		Hand hand4 = new Hand("TD 2S TH KS 3C"); // PAIR T's + K kicker
		
		List<Hand> hands = new ArrayList<Hand>();
		hands.add(hand1);
		hands.add(hand2);
		hands.add(hand3);
		hands.add(hand4);
		
		List<Hand> ranked = eval.rankHands(hands);
		
		Assert.assertEquals(hand3, ranked.get(0));
		Assert.assertEquals(hand2, ranked.get(1));
		Assert.assertEquals(hand4, ranked.get(2));
		Assert.assertEquals(hand1, ranked.get(3));
	}
	
}
