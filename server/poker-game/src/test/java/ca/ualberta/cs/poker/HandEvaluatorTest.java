/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
