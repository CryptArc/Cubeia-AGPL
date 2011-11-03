package com.cubeia.poker.variant.telesina;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.eval.HandTypeCheckCalculator;


public class TelesinaHandStrengthEvaluatorTest {
	
	TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
	
	HandTypeCheckCalculator typeCalculator = new HandTypeCheckCalculator(Rank.SEVEN);
	
	@Test
	public void testCheckStraight() {
		Hand hand = new Hand("TH 7S 9D JS 8C");
		HandStrength straight = eval.checkStraight(hand, 5);
		assertThat(straight.getHandType(), is(HandType.STRAIGHT));
		assertThat(straight.getCards().size(), is(5));
		
		assertThat(straight.getCards().get(0).getRank(), is(Rank.SEVEN));
		assertThat(straight.getCards().get(1).getRank(), is(Rank.EIGHT));
		assertThat(straight.getCards().get(2).getRank(), is(Rank.NINE));
		assertThat(straight.getCards().get(3).getRank(), is(Rank.TEN));
		assertThat(straight.getCards().get(4).getRank(), is(Rank.JACK));
	}
	
	@Test
	public void testCheckStraightAceLow() {
		Hand hand = new Hand("TH 7S 9D AC 8C");
		HandStrength straight = eval.checkStraight(hand, 5);
		assertThat(straight.getHandType(), is(HandType.STRAIGHT));
		assertThat(straight.getCards().size(), is(5));
		
		assertThat(straight.getCards().get(0).getRank(), is(Rank.ACE));
		assertThat(straight.getCards().get(1).getRank(), is(Rank.SEVEN));
		assertThat(straight.getCards().get(2).getRank(), is(Rank.EIGHT));
		assertThat(straight.getCards().get(3).getRank(), is(Rank.NINE));
		assertThat(straight.getCards().get(4).getRank(), is(Rank.TEN));
	}
	
	@Test
	public void testCheckStraightAceHigh() {
		Hand hand = new Hand("QH JS TD AC KC");
		HandStrength straight = eval.checkStraight(hand, 5);
		assertThat(straight.getHandType(), is(HandType.STRAIGHT));
		assertThat(straight.getCards().size(), is(5));
		
		assertThat(straight.getCards().get(0).getRank(), is(Rank.TEN));
		assertThat(straight.getCards().get(1).getRank(), is(Rank.JACK));
		assertThat(straight.getCards().get(2).getRank(), is(Rank.QUEEN));
		assertThat(straight.getCards().get(3).getRank(), is(Rank.KING));
		assertThat(straight.getCards().get(4).getRank(), is(Rank.ACE));
	}
	
	@Test
	public void testCheckStraightFlush() {
		Hand hand = new Hand("TS 7S 9S JS 8S");
		HandStrength straight = eval.checkStraightFlush(hand, 5);
		assertThat(straight.getHandType(), is(HandType.STRAIGHT_FLUSH));
		assertThat(straight.getCards().size(), is(5));
		
		assertThat(straight.getCards().get(0).getRank(), is(Rank.SEVEN));
		assertThat(straight.getCards().get(1).getRank(), is(Rank.EIGHT));
		assertThat(straight.getCards().get(2).getRank(), is(Rank.NINE));
		assertThat(straight.getCards().get(3).getRank(), is(Rank.TEN));
		assertThat(straight.getCards().get(4).getRank(), is(Rank.JACK));
	}
	
	@Test
	public void testCheckFullHouse() {
		Hand hand = new Hand("7S 8S 8C 7D 8H");
		HandStrength strength = typeCalculator.checkFullHouse(hand);
		assertThat(strength.getHandType(), is(HandType.FULL_HOUSE));
		assertThat(strength.getCards().size(), is(5));
		
		assertThat(strength.getCards().get(0).getRank(), is(Rank.EIGHT));
		assertThat(strength.getCards().get(1).getRank(), is(Rank.EIGHT));
		assertThat(strength.getCards().get(2).getRank(), is(Rank.EIGHT));
		assertThat(strength.getCards().get(3).getRank(), is(Rank.SEVEN));
		assertThat(strength.getCards().get(4).getRank(), is(Rank.SEVEN));
	}
	
	
	@Test
	public void testFlush() {
		Hand hand = new Hand("7S KS 9S AS JS");
		HandStrength strength = eval.checkFlush(hand, 5);
		assertThat(strength.getHandType(), is(HandType.FLUSH));
		
		assertThat(strength.getCards().get(0).getRank(), is(Rank.ACE));
		assertThat(strength.getCards().get(1).getRank(), is(Rank.KING));
		assertThat(strength.getCards().get(2).getRank(), is(Rank.JACK));
		assertThat(strength.getCards().get(3).getRank(), is(Rank.NINE));
		assertThat(strength.getCards().get(4).getRank(), is(Rank.SEVEN));
	}
}
