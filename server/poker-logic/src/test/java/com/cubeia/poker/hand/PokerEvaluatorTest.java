package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.HandType.FLUSH;
import static com.cubeia.poker.hand.HandType.FULL_HOUSE;
import static com.cubeia.poker.hand.Rank.ACE;
import static com.cubeia.poker.hand.Rank.NINE;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.security.SecureRandom;
import java.util.List;

import org.junit.Test;

import com.cubeia.poker.model.PlayerHand;


public class PokerEvaluatorTest {

	PokerEvaluator eval = new PokerEvaluator();
	
	@Test
	public void testHands_1() throws Exception {
        PlayerHand hand1 = new PlayerHand(1, new Hand("2C 2C 2C 2H AS"));
        PlayerHand hand2 = new PlayerHand(2, new Hand("AC AC AC AH 2S"));
	    
		List<PlayerHand> ranked = eval.rankHands(asList(hand1, hand2));
        assertThat(ranked.get(0), is(hand2));
        assertThat(ranked.get(1), is(hand1));
	}
	
	@Test
	public void testHands_2() throws Exception {
		PlayerHand hand1 = new PlayerHand(1, new Hand("AC AC AC AH 2S"));
		PlayerHand hand2 = new PlayerHand(2, new Hand("2C 2C 2C 2H AS"));
		
		List<PlayerHand> ranked = eval.rankHands(asList(hand1, hand2));
        assertThat(ranked.get(0), is(hand1));
        assertThat(ranked.get(0).getPlayerId(), is(1));
        assertThat(ranked.get(1), is(hand2));
        assertThat(ranked.get(1).getPlayerId(), is(2));
	}
	
	@Test
	public void testHands_3() throws Exception {
		PlayerHand hand1 = new PlayerHand(1, new Hand("QC QC AC AH TS")); // TWO_PAIR
		PlayerHand hand2 = new PlayerHand(2, new Hand("2C 8H 2C 2C AS")); // THREE_OF_A_KIND 2's + A kicker
		PlayerHand hand3 = new PlayerHand(3, new Hand("7S 2C 2C 2C 8H")); // THREE_OF_A_KIND 2's
		PlayerHand hand4 = new PlayerHand(4, new Hand("4D 4S 4H 5S 5C")); // FULL_HOUSE
		
		
		List<PlayerHand> ranked = eval.rankHands(asList(hand1, hand2, hand3, hand4));
        assertThat(ranked.get(0), is(hand4));
        assertThat(ranked.get(1), is(hand2));
        assertThat(ranked.get(2), is(hand3));
        assertThat(ranked.get(3), is(hand1));
	}
	
	@Test
	public void testHands_4() throws Exception {
		PlayerHand hand1 = new PlayerHand(1, new Hand("JC 2C 3C 4H JS")); // PAIR Q's + 2 kicker
		PlayerHand hand2 = new PlayerHand(2, new Hand("QC 2H 3C 4C QS")); // PAIR Q's + K kicker  
		
		List<PlayerHand> ranked = eval.rankHands(asList(hand1, hand2));
        assertThat(ranked.get(0), is(hand2));
        assertThat(ranked.get(1), is(hand1));
	}
	
	@Test
	public void testHands_5() throws Exception {
		PlayerHand hand1 = new PlayerHand(1, new Hand("TS TC 2C 3C 4H")); // PAIR T's + 2 kicker
		PlayerHand hand2 = new PlayerHand(2, new Hand("QC 2C 3C 4H QS")); // PAIR Q's + 2 kicker
		PlayerHand hand3 = new PlayerHand(3, new Hand("QC 2H 3C KC QS")); // PAIR Q's + K kicker  
		PlayerHand hand4 = new PlayerHand(4, new Hand("TD 2S TH KS 3C")); // PAIR T's + K kicker
		
		List<PlayerHand> ranked = eval.rankHands(asList(hand1, hand2, hand3, hand4));
        assertThat(ranked.get(0), is(hand3));
        assertThat(ranked.get(1), is(hand2));
        assertThat(ranked.get(2), is(hand4));
        assertThat(ranked.get(3), is(hand1));
	}
	
	
	@Test
	public void test7CardHands_1() throws Exception {
		Hand hand = new Hand("2s 3h 4s 5s 5h 6s As"); 
		HandStrength strength = eval.getBestCombinationHandStrength(hand);
		assertEquals(FLUSH, strength.getHandType());
		assertEquals(ACE, strength.getHighestRank());
	}
	
	@Test
	public void test7CardHands_2() throws Exception {
		Hand hand = new Hand("9s Ah Jd 9h Kh As 9s"); 
		HandStrength strength = eval.getBestCombinationHandStrength(hand);
		assertEquals(FULL_HOUSE, strength.getHandType());
		assertEquals(NINE, strength.getHighestRank());
		assertEquals(ACE, strength.getSecondRank());
	}
	
	/*
	@Test
	public void testBetCombinationHandStrength() {
	    Shuffler<Card> shuffler = new Shuffler<Card>(new SecureRandom());
	    CardIdGenerator idGenerator = new IndexCardIdGenerator();
	    
	    int max = 10000000;
        for (int i = 0; i < max; i++) {
	        
	        if (i % 10000 == 0) {
	            System.err.println(i + "(" + max + ")");
	        }
	        
            StandardDeck deck = new StandardDeck(shuffler , idGenerator);
    	    
            Hand hand = new Hand();
    	    
            hand.addCard(deck.deal());
            hand.addCard(deck.deal());
            hand.addCard(deck.deal());
            hand.addCard(deck.deal());
            hand.addCard(deck.deal());
            hand.addCard(deck.deal());
    	    eval.getBestCombinationHandStrength(hand);
	    }	    
	}
	*/
}
