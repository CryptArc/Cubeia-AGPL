package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.HandType.FLUSH;
import static com.cubeia.poker.hand.HandType.FOUR_OF_A_KIND;
import static com.cubeia.poker.hand.HandType.FULL_HOUSE;
import static com.cubeia.poker.hand.HandType.HIGH_CARD;
import static com.cubeia.poker.hand.HandType.ONE_PAIR;
import static com.cubeia.poker.hand.HandType.STRAIGHT;
import static com.cubeia.poker.hand.HandType.STRAIGHT_FLUSH;
import static com.cubeia.poker.hand.HandType.THREE_OF_A_KIND;
import static com.cubeia.poker.hand.HandType.TWO_PAIRS;
import static com.cubeia.poker.hand.Rank.ACE;
import static com.cubeia.poker.hand.Rank.JACK;
import static com.cubeia.poker.hand.Rank.TWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class HandCalculatorTest {

	HandCalculator calc = new HandCalculator();
	
	@Test
	public void testIsFlush() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		assertNull(calc.checkFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C AC");
		assertNotNull(calc.checkFlush(hand));
		
		hand = new Hand("2C KC");
		assertNotNull(calc.checkFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C AC TC");
		HandStrength strength = calc.checkFlush(hand);
		assertEquals(FLUSH, strength.getHandType());
		assertEquals(Rank.ACE, strength.getHighestRank());
		assertNull(strength.getSecondRank());
		
		hand = new Hand("2C 3C 4H");
		assertNull(calc.checkFlush(hand));
	}
	
	@Test
	public void testIsStraight() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		assertNull(calc.checkStraight(hand));
		
		hand = new Hand("2C 3C 5C 4C 6H");
		HandStrength strength = calc.checkStraight(hand);
		assertEquals(STRAIGHT, strength.getHandType());
		assertEquals(Rank.SIX, strength.getHighestRank());
		assertNull(strength.getSecondRank());
	}
	
	@Test
	public void testIsStraightFlush() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		assertNull(calc.checkStraightFlush(hand));
		
		hand = new Hand("2C 3C 4C 5C 6C");
		HandStrength strength = calc.checkStraightFlush(hand);
		assertEquals(STRAIGHT_FLUSH, strength.getHandType());
		assertEquals(Rank.SIX, strength.getHighestRank());
		assertNull(strength.getSecondRank());
	}
	
	@Test
	public void testHasManyOfAKind() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C AS");
		assertNull(calc.checkManyOfAKind(hand, 3));
		
		hand = new Hand("2C 3C 2H 5C 2S");
		assertEquals(THREE_OF_A_KIND, calc.checkManyOfAKind(hand, 3).getHandType());
		assertEquals(Rank.TWO, calc.checkManyOfAKind(hand, 3).getHighestRank());
		
		hand = new Hand("2C 2C 2H 5C 2S");
		assertEquals(THREE_OF_A_KIND, calc.checkManyOfAKind(hand, 3).getHandType());
		assertEquals(Rank.TWO, calc.checkManyOfAKind(hand, 3).getHighestRank());
		
		hand = new Hand("2C 2C 2H 5C 2S");
		assertEquals(THREE_OF_A_KIND, calc.checkManyOfAKind(hand, 3).getHandType());
		assertEquals(Rank.TWO, calc.checkManyOfAKind(hand, 3).getHighestRank());
		
		hand = new Hand("2C 2C 2H AC AS AH");
		assertEquals(THREE_OF_A_KIND, calc.checkManyOfAKind(hand, 3).getHandType());
		assertEquals(Rank.ACE, calc.checkManyOfAKind(hand, 3).getHighestRank());
		
	}
	
	@Test
	public void testQuads() throws Exception {
		Hand hand = new Hand("2C 2H 5D 2H 2C");
		HandStrength strength = calc.checkManyOfAKind(hand, 4);
		assertEquals(FOUR_OF_A_KIND, strength.getHandType());
		assertEquals(TWO, strength.getHighestRank());
	}
	
	@Test
	public void testOnePair() throws Exception {
		Hand hand = new Hand("2C 2H 5D JH 4C");
		HandStrength strength = calc.checkManyOfAKind(hand, 2);
		assertEquals(ONE_PAIR, strength.getHandType());
		assertEquals(TWO, strength.getHighestRank());
		assertNull(strength.getSecondRank());
	}
	
	@Test
	public void testTwoPairs() throws Exception {
		Hand hand = new Hand("JC JH 5D TH TC");
		HandStrength strength = calc.checkTwoPairs(hand);
		assertEquals(TWO_PAIRS, strength.getHandType());
		assertEquals(JACK, strength.getHighestRank());
		assertEquals(Rank.TEN, strength.getSecondRank());
	}
	
	@Test
	public void testFullHouse() throws Exception {
		Hand hand = new Hand("JC JH JD TH TC");
		HandStrength strength = calc.checkFullHouse(hand);
		assertEquals(FULL_HOUSE, strength.getHandType());
		assertEquals(JACK, strength.getHighestRank());
		assertEquals(Rank.TEN, strength.getSecondRank());
	}
	
	@Test
	public void testHighCard() throws Exception {
		Hand hand = new Hand("2C 4H JD TH AC");
		HandStrength strength = calc.checkHighCard(hand);
		assertEquals(HIGH_CARD, strength.getHandType());
		assertEquals(ACE, strength.getHighestRank());
		assertEquals(JACK, strength.getSecondRank());
	}
	
}
