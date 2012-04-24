package poker.gs.server.blinds;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static poker.gs.server.blinds.Fixtures.blindsInfo;
import static poker.gs.server.blinds.Fixtures.player;
import static poker.gs.server.blinds.Fixtures.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import junit.framework.TestCase;

import org.mockito.Mockito;

import poker.gs.server.blinds.utils.MockPlayer;

public class BlindsCalculatorTest extends TestCase implements LogCallback {

	private BlindsCalculator calc;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		calc = new BlindsCalculator();
		calc.setLogCallback(this);
	}

	/**
	 * This corresponds to use case 1.1.
	 */
	public void testFirstHeadsUpHand() {
		// Given
		List<Integer> seatIdList = Arrays.asList(0, 1);
		BlindsInfo lastHandsBlinds = new BlindsInfo();
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players.add(player(1, false));
		players.add(player(0, false));
		RandomSeatProvider randomizer = mock(RandomSeatProvider.class);
		when(randomizer.getRandomSeatId(seatIdList)).thenReturn(1);		
		calc = new BlindsCalculator(randomizer);
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		verify(randomizer).getRandomSeatId(seatIdList);
		assertBlindsInfo(result, 1, 1, 0);
	}
	
	/**
	 * This corresponds to test case 1.2.
	 */
	public void testNonFirstHeadsUpHand() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 1, 2);
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players(1, 2));
		
		// Then
		assertBlindsInfo(result, 2, 2, 1);
	}	
	
	/**
	 * In this test case, a new player is entering the game in seat 5.
	 */
	public void testNormalEntryBet() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(1, 2, 3, 4);
		players.add(player(5, false));		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		Queue<EntryBetter> entryBetters = calc.getEntryBetters();
		EntryBetter entryBetter = entryBetters.peek();
		assertEquals(5, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.BIG_BLIND, entryBetter.getEntryBetType());
	}
	
	public void testPlayerBetweenDealerButtonAndSmallBlindDoesNotPostEntryBet() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 3, 4);
		List<BlindsPlayer> players = players(1, 2, 3);
		players.add(player(4, false));		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertEquals(0, calc.getEntryBetters().size());
	}
	
	public void testPlayerOnDealerButtonDoesNotPostEntryBet() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 3, 4);
		List<BlindsPlayer> players = players(1, 2, 4);
		players.add(player(3, false));		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertEquals(0, calc.getEntryBetters().size());
	}
	
	public void testCalculatingEntryBetsWhenDealerIsOnEmptySeatDoesNotHang() {
		BlindsInfo blinds = blindsInfo(1, 4, 5);
		List<BlindsPlayer> players = players(1, 2, 3, 5, 6, 7);
		
		calc.initializeBlinds(blinds, players);
		assertEquals(0, calc.getEntryBetters().size());
	}	
	
	public void testPlayerShouldPayDeadSmallBlind() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(1, 2, 3, 4);
		MockPlayer playerWhoMissedSmallBlind = player(5, false);
		playerWhoMissedSmallBlind.setMissedBlindsStatus(MissedBlindsStatus.MISSED_SMALL_BLIND);
		players.add(playerWhoMissedSmallBlind);		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		Queue<EntryBetter> entryBetters = calc.getEntryBetters();
		EntryBetter entryBetter = entryBetters.peek();
		assertEquals(5, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.DEAD_SMALL_BLIND, entryBetter.getEntryBetType());		
	}
	
	public void testPlayerShouldPayBigBlindPlusDeadSmallBlind() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(1, 2, 3, 4);
		MockPlayer playerWhoMissedSmallBlind = player(5, false);
		playerWhoMissedSmallBlind.setMissedBlindsStatus(MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);
		players.add(playerWhoMissedSmallBlind);		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		Queue<EntryBetter> entryBetters = calc.getEntryBetters();
		EntryBetter entryBetter = entryBetters.peek();
		assertEquals(5, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND, entryBetter.getEntryBetType());		
	}
	
	public void testPlayerWhoHasNotMissedAnyBlindsDoesNotHaveToPayEntryBet() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(1, 2, 3, 4);
		MockPlayer playerWhoMissedNoBlinds = player(5, true);
		playerWhoMissedNoBlinds.setMissedBlindsStatus(MissedBlindsStatus.NO_MISSED_BLINDS);
		players.add(playerWhoMissedNoBlinds);		
		
		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		Queue<EntryBetter> entryBetters = calc.getEntryBetters();
		assertEquals(0, entryBetters.size());
	}
	
	public void testOrderOfEntryBetters() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(1, 2, 3, 4);
		
		MockPlayer playerWhoMissedSmallBlind = player(5, false);
		playerWhoMissedSmallBlind.setMissedBlindsStatus(MissedBlindsStatus.MISSED_SMALL_BLIND);
		players.add(playerWhoMissedSmallBlind);
		
		MockPlayer newPlayer = player(6, false);
		newPlayer.setMissedBlindsStatus(MissedBlindsStatus.NOT_ENTERED_YET);
		players.add(newPlayer);		

		MockPlayer playerWhoMissedBigBlind = player(7, false);
		playerWhoMissedBigBlind.setMissedBlindsStatus(MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);
		players.add(playerWhoMissedBigBlind);		

		// When
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		Queue<EntryBetter> entryBetters = calc.getEntryBetters();
		
		EntryBetter entryBetter = entryBetters.poll();
		assertEquals(5, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.DEAD_SMALL_BLIND, entryBetter.getEntryBetType());
		
		entryBetter = entryBetters.poll();
		assertEquals(6, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.BIG_BLIND, entryBetter.getEntryBetType());
		
		entryBetter = entryBetters.poll();
		assertEquals(7, entryBetter.getPlayer().getSeatId());
		assertEquals(EntryBetType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND, entryBetter.getEntryBetType());				
	}	
	
	public void testNonHeadsUpToHeadsUp() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 3);
		List<BlindsPlayer> players = players(4, 5);
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 5, 5, 4);
	}

	/**
	 * Tests moving from heads up to non heads up. 
	 * 
	 * Note, the player in seat 3 cannot enter the game until the button has passed.
	 */
	public void testHeadsUpToNonHeadsUp() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(5, 5, 4);
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players.add(player(3, false)); 
		players.add(player(4, true));
		players.add(player(5, true));		
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 5, 4, 5);
		assertEquals(0, calc.getEntryBetters().size());
	}
	
	public void testUndefinedLastHandCountsAsFirstHandOnTable() {
		// Given
		List<Integer> seatIdList = Arrays.asList(1, 2, 3, 4, 5);
		BlindsInfo lastHandsBlinds = new BlindsInfo();
		assertEquals(false, lastHandsBlinds.isDefined());
		
		RandomSeatProvider randomizer = mock(RandomSeatProvider.class);
		Mockito.when(randomizer.getRandomSeatId(seatIdList)).thenReturn(5);		
		calc = new BlindsCalculator(randomizer);

		// When
		BlindsInfo blinds = calc.initializeBlinds(lastHandsBlinds, players(false, 1, 2, 3, 4, 5));
		
		// Then
		assertEquals(5, blinds.getDealerSeatId());		
		verify(randomizer).getRandomSeatId(seatIdList);
	}
	
	public void testOnlyOneEnteredPlayerDoesNotGetTheBigBlind() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 2, 0); // This hand was cancelled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players.add(player(2, true)); 
		MockPlayer player = player(3, false);
		player.setMissedBlindsStatus(MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);
		players.add(player);		
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 2, 2, 3);
		assertEquals(0, calc.getEntryBetters().size());		
	}
	
	public void testOnlyOneEnteredPlayerDoesNotGetTheBigBlindNonHeadsUp() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 2, 0); // This hand was cancelled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players.add(player(2, true)); 
		MockPlayer player3 = player(3, false);
		player3.setMissedBlindsStatus(MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);
		MockPlayer player4 = player(4, false);
		player4.setMissedBlindsStatus(MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);		
		players.addAll(Arrays.asList(player3, player4));		
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 2, 3, 4);
		assertEquals(0, calc.getEntryBetters().size());		
	}	
	
	public void testInitWhenLastHandWasCancelled() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 0, 0); // This hand was canceled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(2, 3); 
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 2, 2, 3);
		assertEquals(0, calc.getEntryBetters().size());				
	}
	
	public void testInitWhenLastHandWasCancelledAndDealerNotSeated() {
		// Given
		RandomSeatProvider randomizer = mock(RandomSeatProvider.class);
		when(randomizer.getRandomSeatId(Mockito.anyListOf(Integer.class))).thenReturn(3);
		BlindsInfo lastHandsBlinds = blindsInfo(2, 0, 0);
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(3, 4, 5);
		players.add(player(2, false, false));
		calc = new BlindsCalculator(randomizer);
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 3, 4, 5);
		assertEquals(0, calc.getEntryBetters().size());				
	}	
	
	public void testReturnNullIfNotEnoughPlayers() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(2, 0, 0); // This hand was canceled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(2); 
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertNull(result);
	}
	
	public void testOnlyOneEnteredAndLastHandCancelled() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(5, 2, 0); // This hand was cancelled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players.add(player(3, true)); 
		MockPlayer player = player(2, false);
		player.setMissedBlindsStatus(MissedBlindsStatus.NOT_ENTERED_YET);
		players.add(player);		
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 3, 3, 2);
		assertEquals(0, calc.getEntryBetters().size());		
	}
	
	public void testNonFirstHeadsUpButDealerIsGone() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(5, 5, 4); // This hand was cancelled
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(5, 6); 
		
		// When
		BlindsInfo result = calc.initializeBlinds(lastHandsBlinds, players);
		
		// Then
		assertBlindsInfo(result, 5, 5, 6);
		assertEquals(0, calc.getEntryBetters().size());				
	}
	
	public void testGetPlayersBetweenDealerAndBig() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 2, 5);
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(1, 2, 3, 4, 5, 6); 
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// When
		List<BlindsPlayer> playersBetweenDealerAndBig = calc.getPlayersBetweenDealerAndBig();
		
		// Then
		assertEquals(Arrays.asList(players.get(2), players.get(3)), playersBetweenDealerAndBig);
	}
	
	public void testGetPlayersBetweenDealerAndBigHeadsUp() {
		// Given
		BlindsInfo lastHandsBlinds = blindsInfo(1, 1, 5);
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(1, 2, 3, 4, 5, 6); 
		calc.initializeBlinds(lastHandsBlinds, players);
		
		// When
		List<BlindsPlayer> playersBetweenDealerAndBig = calc.getPlayersBetweenDealerAndBig();
		
		// Then
		assertEquals(Arrays.asList(players.get(1), players.get(2), players.get(3)), playersBetweenDealerAndBig);
	}	
	
	public void testGetNextBigBlindPlayer() {
		BlindsInfo lastHandsBlinds = blindsInfo(1, 1, 5);
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		players = players(1, 2, 3, 4, 5, 6); 
		calc.initializeBlinds(lastHandsBlinds, players);
		
		assertEquals(6, calc.getNextBigBlindPlayer(-1).getSeatId());
		assertEquals(6, calc.getNextBigBlindPlayer(-1).getSeatId());
		assertEquals(1, calc.getNextBigBlindPlayer(6).getSeatId());
		assertNull(calc.getNextBigBlindPlayer(1));
		
	}
	
	private void assertBlindsInfo(BlindsInfo result, int dealer, int small, int big) {
		assertEquals(dealer, result.getDealerSeatId());
		assertEquals(small, result.getSmallBlindSeatId());
		assertEquals(big, result.getBigBlindSeatId());		
	}

	public void log(String message) {
		System.out.println(message);
	}	
	
}
