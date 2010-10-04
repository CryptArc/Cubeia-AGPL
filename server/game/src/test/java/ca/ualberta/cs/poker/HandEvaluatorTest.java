package ca.ualberta.cs.poker;

import junit.framework.TestCase;

/**
 * Small testcase for a couple of hands.
 * We should probably add more hands as an ongoing project.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class HandEvaluatorTest extends TestCase {


	public void testRankHandCardCardHand() {
		Hand hand1 = getRoyalFlush();
		Hand hand2 = getPairOfJacks();
		
		HandEvaluator eval = new HandEvaluator();
		int i = eval.compareHands(hand1, hand2);
		assertEquals(1, i);
		
		i = eval.compareHands(hand2, hand1);
		assertEquals(2, i);
		
		hand2 = getRoyalFlush();
		i = eval.compareHands(hand2, hand1);
		assertEquals(0, i);
		
	}
	
	
	
	
	private Hand getRoyalFlush() {
		Hand hand = new Hand();
		hand.addCard(new Card(Card.KING, Card.SPADES));
		hand.addCard(new Card(Card.ACE, Card.SPADES));
		hand.addCard(new Card(Card.QUEEN, Card.SPADES));
		hand.addCard(new Card(Card.JACK, Card.SPADES));
		hand.addCard(new Card(Card.TEN, Card.SPADES));
		
		return hand;
	}
	
	private Hand getPairOfJacks() {
		Hand hand = new Hand();
		hand.addCard(new Card(Card.FIVE, Card.CLUBS));
		hand.addCard(new Card(Card.ACE, Card.SPADES));
		hand.addCard(new Card(Card.QUEEN, Card.SPADES));
		hand.addCard(new Card(Card.JACK, Card.SPADES));
		hand.addCard(new Card(Card.JACK, Card.HEARTS));
		
		return hand;
	}
}
