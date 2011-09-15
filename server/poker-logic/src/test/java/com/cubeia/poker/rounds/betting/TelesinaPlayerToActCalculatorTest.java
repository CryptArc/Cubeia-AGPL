package com.cubeia.poker.rounds.betting;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;

@SuppressWarnings("unchecked")
public class TelesinaPlayerToActCalculatorTest {

    @Test
	public void testFirstPlayerToActTwoCards1() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);
		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "7C", "7D");
		PokerPlayer p2 = playerWithHand(2, "AS", "KS");

		seating.put(1, p1);
		seating.put(2, p2);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(2, first.getId());
	}
	
    @Test
	public void testFirstPlayerToActTwoCards2() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "7C", "7D");
		PokerPlayer p2 = playerWithHand(2, "8C", "KS");
		PokerPlayer p3 = playerWithHand(3, "9C", "KH");
		PokerPlayer p4 = playerWithHand(4, "TC", "QD");
		PokerPlayer p5 = playerWithHand(5, "JC", "9S");
		PokerPlayer p6 = playerWithHand(6, "QC", "QS");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);
		seating.put(4, p4);
		seating.put(5, p5);
		seating.put(6, p6);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(3, first.getId());
	}
	
	@Test
	public void testFirstPlayerToActThreeCards1() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "7C", "9C 9D");
		PokerPlayer p2 = playerWithHand(2, "AS", "AC KD");
		PokerPlayer p3 = playerWithHand(3, "8H", "8C 8S");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(1, first.getId());
	}

	@Test
	public void testFirstPlayerToActThreeCards2() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "7C", "8C 9C");
		PokerPlayer p2 = playerWithHand(2, "AS", "AC KD");
		PokerPlayer p3 = playerWithHand(3, "8H", "8C 8S");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(3, first.getId());
	}
	
	@Test
	public void testFirstPlayerToActThreeCards3() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "AH", "7D 9C");
		PokerPlayer p2 = playerWithHand(2, "KH", "7H 9D");
		PokerPlayer p3 = playerWithHand(3, "QH", "7C 9S");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(2, first.getId());
	}
	
	
	@Test
	public void testFirstPlayerToActThreeCards5() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "AH", "KH QH JH TH");
		PokerPlayer p2 = playerWithHand(2, "7D", "8C 9H TD JS");
		PokerPlayer p3 = playerWithHand(3, "QH", "7S 9C TD 9S");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Collections.EMPTY_LIST);
		assertEquals(3, first.getId());
	}
	
	@Test
	public void testFirstPlayerToActVelaRound1() {
		TelesinaPlayerToActCalculator ptac = new TelesinaPlayerToActCalculator(Rank.SEVEN);

		SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

		PokerPlayer p1 = playerWithHand(1, "AH", "KH QH JH TH");
		PokerPlayer p2 = playerWithHand(2, "7D", "8C TD JS QD");
		PokerPlayer p3 = playerWithHand(3, "QH", "7S TD 9S 7C");

		seating.put(1, p1);
		seating.put(2, p2);
		seating.put(3, p3);

		PokerPlayer first = ptac.getFirstPlayerToAct(0, seating, Arrays.asList(new Card("9H")));
		assertEquals(1, first.getId());
	}
	
	private PokerPlayer playerWithHand(int id, String pocketCard, String publicPocketCards) {
		PokerPlayer p = mock(PokerPlayer.class);
		
		when(p.getId()).thenReturn(id);
		
		Hand pocketCardsHand = new Hand(pocketCard + " " + publicPocketCards);
		when(p.getPocketCards()).thenReturn(pocketCardsHand);
		
		Set<Card> publicPocketCardsSet = new HashSet<Card>(new Hand(publicPocketCards).getCards());
		when(p.getPublicPocketCards()).thenReturn(publicPocketCardsSet);

		return p;
	}

	@Test
	public void testNextPlayerToAct() {
		DefaultPlayerToActCalculator pac = new DefaultPlayerToActCalculator();
		SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		PokerPlayer player3 = mock(PokerPlayer.class);
		seatingMap.put(0, player1);
		seatingMap.put(1, player2);
		seatingMap.put(2, player3);

		PokerPlayer playerToAct = pac.getFirstPlayerToAct(1, seatingMap, Collections.EMPTY_LIST);
		assertThat(playerToAct, is(player3));

		playerToAct = pac.getFirstPlayerToAct(2, seatingMap, Collections.EMPTY_LIST);
		assertThat(playerToAct, is(player1));
	}

}
