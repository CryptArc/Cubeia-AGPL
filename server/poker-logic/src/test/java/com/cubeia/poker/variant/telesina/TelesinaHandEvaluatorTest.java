package com.cubeia.poker.variant.telesina;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;

public class TelesinaHandEvaluatorTest {

	@Test
	public void testEvaluatorIncludesHand() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("8S 9S 9H 9C JS KS AS"));
		
		assertEquals(HandType.FLUSH, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("8S 9S JS KS AS")));
	}
	
	@Test
	public void testEvaluatorIncludesHandShortHand() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("JS KS AS"));
		
		assertEquals(HandType.HIGH_CARD, best.getType());
		assertEquals(3, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("JS KS AS")));
	}
}
