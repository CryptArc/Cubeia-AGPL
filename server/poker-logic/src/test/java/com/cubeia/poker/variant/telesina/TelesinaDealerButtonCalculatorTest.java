package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.player.PokerPlayer;

public class TelesinaDealerButtonCalculatorTest {

	private TelesinaDealerButtonCalculator calculator;
	@Mock private PokerPlayer player0;
	@Mock private PokerPlayer player1;
	@Mock private PokerPlayer player2;
	@Mock private PokerPlayer player3;
	@Mock private PokerPlayer player4;
	@Mock private PokerPlayer player5;
	@Mock private PokerPlayer player6;
	
	private SortedMap<Integer, PokerPlayer> seatingMap;
	
	@Before
	public void setupBefore() {
		MockitoAnnotations.initMocks(this);	
		calculator = new TelesinaDealerButtonCalculator();
		seatingMap = new TreeMap<Integer, PokerPlayer>();
		seatingMap.put(0, player0);
		// empty seat
		seatingMap.put(2, player2);
		seatingMap.put(3, player3);
		seatingMap.put(4, player1);
	}
	
	
	@Test
	public void testVanillaNewRound() {
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false);
		assertThat(newSeat, is(2));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 2, false);
		assertThat(newSeat, is(3));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 4, false);
		assertThat(newSeat, is(0));
	}
	

	@Test
	public void testOnlyOnePlayer() {
		int newSeat;
		seatingMap.clear();
		seatingMap.put(3, player0);
		newSeat = calculator.getNextDealerSeat(seatingMap, 3, false);
		assertThat(newSeat, is(3));
		
		seatingMap.clear();
		seatingMap.put(3, player0);
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false);
		assertThat(newSeat, is(3));
	}
	
	@Test
	public void testHandCancelled() {
		int newSeat;
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, true);
		assertThat(newSeat, is(0));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 4, true);
		assertThat(newSeat, is(4));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 1, true);
		assertThat(newSeat, is(1));
	}
	
	@Test
	public void testDealerDeclinesAnte() {
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 1, false); // one is not here
		assertThat(newSeat, is(2));
		
	}
	
	@Test
	public void testDealerDeclinesNextAlsoAnte() {
		
		seatingMap.clear();
		
		seatingMap.put(0, player0);
		// empty seat
		// empty seat
		seatingMap.put(3, player3);
		seatingMap.put(4, player1);
		seatingMap.put(5, player2);
		
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 1, false); // one is not here
		assertThat(newSeat, is(3));
		
	}
	
	@Test
	public void testDealerDeclinesLotOfHoles() {
		
		seatingMap.clear();
		
		// empty seat
		// empty seat
		// empty seat
		seatingMap.put(2, player0);
		seatingMap.put(3, player3);
		seatingMap.put(4, player1);
		seatingMap.put(5, player2);
		//empty seat
		seatingMap.put(7, player4);
		//empty seat
		//empty seat
		seatingMap.put(10, player5);
		// empty seat
		seatingMap.put(12, player6);
		
		int newSeat;
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 1, false);
		assertThat(newSeat, is(2));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 5, false);
		assertThat(newSeat, is(7));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 7, false);
		assertThat(newSeat, is(10));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 12, false);
		assertThat(newSeat, is(2));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false);
		assertThat(newSeat, is(2));
		
		
		
	}
	
	@Test
	public void testNextDealerDeclinesAnte() {
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false); // one is not here
		assertThat(newSeat, is(2));
	}
	
	@Test
	public void testLastSeatIsCurrentDealer() {
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 4, false);
		assertThat(newSeat, is(0));
	}
	
	@Test
	public void testNextSeatIsEmpty() {
		int newSeat;
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false);
		assertThat(newSeat, is(2));
	}
	
	@Test
	public void testAllSeatsAreEmpty() {
		int newSeat;
		
		seatingMap.clear();
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 0, false);
		assertThat(newSeat, is(0));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 4, false);
		assertThat(newSeat, is(4));
		
		newSeat = calculator.getNextDealerSeat(seatingMap, 1, false);
		assertThat(newSeat, is(1));
		
	}

}
