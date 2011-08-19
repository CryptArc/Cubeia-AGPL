package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.HandType.*;
import static com.cubeia.poker.hand.Rank.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class HandStrengthTest {
	
	@Test
	public void testTrivial() throws Exception {
		List<HandStrength> list = new ArrayList<HandStrength>();
		
		HandStrength strength1 = new HandStrength(THREE_OF_A_KIND);
		HandStrength strength2 = new HandStrength(STRAIGHT);
		HandStrength strength3 = new HandStrength(FULL_HOUSE);
		HandStrength strength4 = new HandStrength(STRAIGHT_FLUSH);
		
		list.add(strength1);
		list.add(strength2);
		list.add(strength3);
		list.add(strength4);
		
		Collections.sort(list);
		
		Assert.assertEquals(STRAIGHT_FLUSH, list.get(0).getHandType());
		Assert.assertEquals(FULL_HOUSE, list.get(1).getHandType());
		Assert.assertEquals(STRAIGHT, list.get(2).getHandType());
		Assert.assertEquals(THREE_OF_A_KIND, list.get(3).getHandType());
	}
	
	@Test
	public void testDifferentPairs() throws Exception {
		List<HandStrength> list = new ArrayList<HandStrength>();
		
		HandStrength strength1 = new HandStrength(ONE_PAIR);
		strength1.setHighestRank(TEN);
		
		HandStrength strength2 = new HandStrength(ONE_PAIR);
		strength2.setHighestRank(KING);
		
		HandStrength strength3 = new HandStrength(ONE_PAIR);
		strength3.setHighestRank(ACE);
		
		list.add(strength1);
		list.add(strength2);
		list.add(strength3);
		
		Collections.sort(list);
		
		Assert.assertEquals(ACE, list.get(0).getHighestRank());
		Assert.assertEquals(KING, list.get(1).getHighestRank());
		Assert.assertEquals(TEN, list.get(2).getHighestRank());
	}
	
	@Test
	public void testDifferentTwoPairs() throws Exception {
		List<HandStrength> list = new ArrayList<HandStrength>();
		
		HandStrength strength1 = new HandStrength(TWO_PAIRS);
		strength1.setHighestRank(ACE);
		strength1.setSecondRank(TWO);
		
		HandStrength strength2 = new HandStrength(TWO_PAIRS);
		strength2.setHighestRank(ACE);
		strength2.setSecondRank(JACK);
		
		HandStrength strength3 = new HandStrength(TWO_PAIRS);
		strength3.setHighestRank(ACE);
		strength3.setSecondRank(KING);
		
		list.add(strength1);
		list.add(strength2);
		list.add(strength3);
		
		Collections.sort(list);
		
		Assert.assertEquals(KING, list.get(0).getSecondRank());
		Assert.assertEquals(JACK, list.get(1).getSecondRank());
		Assert.assertEquals(TWO, list.get(2).getSecondRank());
	}
	
	@Test
	public void testTwoPairKicker() throws Exception {
		List<HandStrength> list = new ArrayList<HandStrength>();
		
		HandStrength strength1 = new HandStrength(TWO_PAIRS);
		strength1.setHighestRank(ACE);
		strength1.setSecondRank(JACK);
		List<Card> kickers = new ArrayList<Card>();
		kickers.add(new Card("4D"));
		strength1.setKickerCards(kickers);
		
		HandStrength strength2 = new HandStrength(TWO_PAIRS);
		strength2.setHighestRank(ACE);
		strength2.setSecondRank(JACK);
		kickers = new ArrayList<Card>();
		kickers.add(new Card("JH"));
		strength2.setKickerCards(kickers);
		
		HandStrength strength3 = new HandStrength(TWO_PAIRS);
		strength3.setHighestRank(ACE);
		strength3.setSecondRank(JACK);
		kickers = new ArrayList<Card>();
		kickers.add(new Card("TS"));
		strength3.setKickerCards(kickers);
		
		list.add(strength1);
		list.add(strength2);
		list.add(strength3);
		
		Collections.sort(list);
		
		Assert.assertEquals(JACK, list.get(0).getKickerCards().get(0).getRank());
		Assert.assertEquals(TEN, list.get(1).getKickerCards().get(0).getRank());
		Assert.assertEquals(FOUR, list.get(2).getKickerCards().get(0).getRank());
	}
	
	@Test
	public void testSinglePairKicker() throws Exception {
		List<HandStrength> list = new ArrayList<HandStrength>();
		
		HandStrength strength1 = new HandStrength(ONE_PAIR);
		strength1.setHighestRank(ACE);
		List<Card> kickers = new ArrayList<Card>();
		kickers.add(new Card("AD"));
		kickers.add(new Card("QD"));
		kickers.add(new Card("2D"));
		strength1.setKickerCards(kickers);
		
		HandStrength strength2 = new HandStrength(ONE_PAIR);
		strength2.setHighestRank(ACE);
		kickers = new ArrayList<Card>();
		kickers.add(new Card("AH"));
		kickers.add(new Card("KH"));
		kickers.add(new Card("8H"));
		strength2.setKickerCards(kickers);
		
		HandStrength strength3 = new HandStrength(ONE_PAIR);
		strength3.setHighestRank(ACE);
		kickers = new ArrayList<Card>();
		kickers.add(new Card("AS"));
		kickers.add(new Card("KS"));
		kickers.add(new Card("TS"));
		strength3.setKickerCards(kickers);
		
		list.add(strength1);
		list.add(strength2);
		list.add(strength3);
		
		Collections.sort(list);
		
		Assert.assertEquals(TEN, list.get(0).getKickerCards().get(2).getRank());
		Assert.assertEquals(EIGHT, list.get(1).getKickerCards().get(2).getRank());
		Assert.assertEquals(TWO, list.get(2).getKickerCards().get(2).getRank());
		
		Assert.assertEquals(strength3, list.get(0));
		Assert.assertEquals(strength2, list.get(1));
		Assert.assertEquals(strength1, list.get(2));
	}
	
}
