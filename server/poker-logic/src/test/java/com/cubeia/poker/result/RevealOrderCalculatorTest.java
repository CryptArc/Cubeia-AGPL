package com.cubeia.poker.result;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.cubeia.poker.player.PokerPlayer;

public class RevealOrderCalculatorTest {

	@Test
	public void testRevealOrderWithLastCaller() {
		RevealOrderCalculator roc = new RevealOrderCalculator();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		PokerPlayer player3 = mock(PokerPlayer.class);
		when(player1.getId()).thenReturn(1001);
		when(player2.getId()).thenReturn(1002);
		when(player3.getId()).thenReturn(1003);
		
		SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
		seatingMap.put(0, player1);
		seatingMap.put(1, player2);
		seatingMap.put(2, player3);
		
		List<Integer> revealOrder = roc.calculateRevealOrder(seatingMap, player2, player1);
		
		Iterator<Integer> revealIter = revealOrder.iterator();
		assertThat(revealIter.next(), is(1002));
		assertThat(revealIter.next(), is(1003));
		assertThat(revealIter.next(), is(1001));
	}

	@Test
	public void testRevealOrderWithNoLastCaller() {
		RevealOrderCalculator roc = new RevealOrderCalculator();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		PokerPlayer player3 = mock(PokerPlayer.class);
		when(player1.getId()).thenReturn(1001);
		when(player2.getId()).thenReturn(1002);
		when(player3.getId()).thenReturn(1003);
		
		SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
		seatingMap.put(0, player1);
		seatingMap.put(1, player2);
		seatingMap.put(2, player3);
		
		List<Integer> revealOrder = roc.calculateRevealOrder(seatingMap, null, player2);
		
		Iterator<Integer> revealIter = revealOrder.iterator();
		assertThat(revealIter.next(), is(1003));
		assertThat(revealIter.next(), is(1001));
		assertThat(revealIter.next(), is(1002));
	}

	@Test
	public void testRevealOrderWithNoLastCallerDealerButtonAtLastSeat() {
		RevealOrderCalculator roc = new RevealOrderCalculator();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		PokerPlayer player3 = mock(PokerPlayer.class);
		when(player1.getId()).thenReturn(1001);
		when(player2.getId()).thenReturn(1002);
		when(player3.getId()).thenReturn(1003);
		
		SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
		seatingMap.put(0, player1);
		seatingMap.put(1, player2);
		seatingMap.put(2, player3);
		
		List<Integer> revealOrder = roc.calculateRevealOrder(seatingMap, null, player3);
		
		Iterator<Integer> revealIter = revealOrder.iterator();
		assertThat(revealIter.next(), is(1001));
		assertThat(revealIter.next(), is(1002));
		assertThat(revealIter.next(), is(1003));
	}
	
}
