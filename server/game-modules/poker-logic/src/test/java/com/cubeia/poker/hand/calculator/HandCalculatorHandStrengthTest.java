package com.cubeia.poker.hand.calculator;

import static com.cubeia.poker.hand.HandType.FLUSH;
import static com.cubeia.poker.hand.HandType.FULL_HOUSE;
import static com.cubeia.poker.hand.HandType.HIGH_CARD;
import static com.cubeia.poker.hand.HandType.STRAIGHT_FLUSH;
import static com.cubeia.poker.hand.HandType.THREE_OF_A_KIND;
import static com.cubeia.poker.hand.HandType.TWO_PAIRS;
import static com.cubeia.poker.hand.Rank.FIVE;
import static com.cubeia.poker.hand.Rank.FOUR;
import static com.cubeia.poker.hand.Rank.JACK;
import static com.cubeia.poker.hand.Rank.KING;
import static com.cubeia.poker.hand.Rank.SIX;
import static com.cubeia.poker.hand.Rank.TWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;


public class HandCalculatorHandStrengthTest {

	HandCalculator calc = new TexasHoldemHandCalculator();
	
	@Test
	public void testHandStrength_1() throws Exception {
		Hand hand = new Hand("2C 3C 4C 5C 6C");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(STRAIGHT_FLUSH, strength.getHandType());
		assertEquals(SIX, strength.getHighestRank());
	}
	
	@Test
	public void testHandStrength_2() throws Exception {
		Hand hand = new Hand("2C 2H 4C 5C 2S");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(THREE_OF_A_KIND, strength.getHandType());
		assertEquals(TWO, strength.getHighestRank());
		assertNull(strength.getSecondRank());
		assertEquals(2, strength.getKickerCards().size());
		assertEquals(FIVE, strength.getKickerCards().get(0).getRank());
		assertEquals(FOUR, strength.getKickerCards().get(1).getRank());
	}
	
	@Test
	public void testHandStrength_3() throws Exception {
		Hand hand = new Hand("2C KC 4C 5C JC");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(FLUSH, strength.getHandType());
		assertEquals(KING, strength.getHighestRank());
	}
	
	@Test
	public void testHandStrength_4() throws Exception {
		Hand hand = new Hand("2C 2H JC 2D JC");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(FULL_HOUSE, strength.getHandType());
		assertEquals(TWO, strength.getHighestRank());
		assertEquals(JACK, strength.getSecondRank());
	}
	
	@Test
	public void testHandStrength_5() throws Exception {
		Hand hand = new Hand("8C QH 8D QC KS");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(TWO_PAIRS, strength.getHandType());
		assertEquals(Rank.QUEEN, strength.getHighestRank());
		assertEquals(Rank.EIGHT, strength.getSecondRank());
		assertEquals(1, strength.getKickerCards().size());
		assertEquals(KING, strength.getKickerCards().get(0).getRank());
	}
	
	@Test
	public void testHandStrength_6() throws Exception {
		Hand hand = new Hand("2C 4H 6D 8C KS");
		HandStrength strength = calc.getHandStrength(hand);
		assertEquals(HIGH_CARD, strength.getHandType());
		assertEquals(Rank.KING, strength.getHighestRank());
		assertEquals(Rank.EIGHT, strength.getSecondRank());
		assertEquals(5, strength.getKickerCards().size());
		assertEquals(KING, strength.getKickerCards().get(0).getRank());
	}
}
