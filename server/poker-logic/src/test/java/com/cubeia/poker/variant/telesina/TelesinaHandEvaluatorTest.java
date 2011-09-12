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
	public void testEvaluatorIncludesHighCard() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("KS QD TD 9H 7D 8C"));
		
		assertEquals(HandType.HIGH_CARD, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("KS QD TD 9H 8C")));
	}
	
	@Test
	public void testEvaluatorIncludesHandPair() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("AS QD TD 9H 7D 9C"));
		
		assertEquals(HandType.ONE_PAIR, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("AS QD TD 9H 9C")));
	}
	
	@Test
	public void testEvaluatorIncludesHandTwoPair() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("AS QD QS 9H 7D 9C"));
		
		assertEquals(HandType.TWO_PAIRS, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("AS QD QS 9H 9C")));
	}
	
	@Test
	public void testEvaluatorIncludesHandThreeOfAKind() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("AS QD QS QC 7D 9C"));
		
		assertEquals(HandType.THREE_OF_A_KIND, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("AS QD QS QC 9C")));
	}
	
	@Test
	public void testEvaluatorIncludesHandStraight1() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("TS AS KC JD 9H QS"));
		
		assertEquals(HandType.STRAIGHT, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("AS KC QS JD TS")));
	}
	
	@Test
	public void testEvaluatorIncludesHandStraight2() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("7C KC QS JD TS 9H"));
		
		assertEquals(HandType.STRAIGHT, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("KC QS JD TS 9H")));
	}
	
	@Test
	public void testEvaluatorIncludesHandFullHouse() {
		TelesinaHandStrengthEvaluator eval = new TelesinaHandStrengthEvaluator(Rank.SEVEN);
		
		HandInfo best = eval.getBestHandInfo(new Hand("QH QD QC TS JH JD"));
		
		assertEquals(HandType.FULL_HOUSE, best.getType());
		assertEquals(5, best.getCards().size());
		assertTrue(best.getCards().containsAll(Card.list("QH QD QC JD JH")));
	}
	
	@Test
	public void testEvaluatorIncludesHandFlush() {
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
