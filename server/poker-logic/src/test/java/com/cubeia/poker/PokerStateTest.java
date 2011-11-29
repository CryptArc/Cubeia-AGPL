package com.cubeia.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

public class PokerStateTest {

	PokerState state;
	PokerSettings settings;
	int anteLevel;

	@Before
	public void setup() {
		state = new PokerState();
		settings = mock(PokerSettings.class);
		anteLevel = 100;
		when(settings.getRakeSettings()).thenReturn(TestUtils.createOnePercentRakeSettings());
		when(settings.getAnteLevel()).thenReturn(anteLevel);
		when(settings.getVariant()).thenReturn(PokerVariant.TELESINA);
		when(settings.getTiming()).thenReturn(TimingFactory.getRegistry().getDefaultTimingProfile());
		state.serverAdapter = mock(ServerAdapter.class);

	}

	private PokerPlayer createMockPlayer(int playerId, int balance){
		PokerPlayer player = mock(PokerPlayer.class);
		when(player.getBalance()).thenReturn((long)balance);
		when(player.getId()).thenReturn(playerId);
		return player;
	}

	/*
	@Test
	public void testGetPlayersStillInPlay() {
        state.currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();
        PokerPlayer player0 = mock(PokerPlayer.class);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        state.currentHandPlayerMap.put(0, player0);
        state.currentHandPlayerMap.put(1, player1);
        state.currentHandPlayerMap.put(2, player2);
        
        Set<PokerPlayer> playersStillInPlay;
        
        when(player0.isAllIn()).thenReturn(true);
        when(player1.hasFolded()).thenReturn(true);
        playersStillInPlay = state.getPlayersStillInPlay();
	    assertThat(playersStillInPlay.size(), is(1));
	    assertThat(playersStillInPlay, JUnitMatchers.hasItem(player2));
	    
        when(player0.isAllIn()).thenReturn(false);
        when(player1.hasFolded()).thenReturn(false);
        playersStillInPlay = state.getPlayersStillInPlay();
        assertThat(playersStillInPlay.size(), is(3));
        assertThat(playersStillInPlay, JUnitMatchers.hasItems(player0, player1, player2));
	}
	*/
	
	@Test
	public void testNotifyHandFinished() {
		TimingProfile timingProfile = mock(TimingProfile.class);
		when(settings.getTiming()).thenReturn(timingProfile);
		when(settings.getMaxBuyIn()).thenReturn(10000);

		state.init(null, settings);
		state.setTournamentTable(false);

		Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();


		PokerPlayer player1 = createMockPlayer(1337, anteLevel-1);
		PokerPlayer player2 = createMockPlayer(666, anteLevel);
		PokerPlayer player3 = createMockPlayer(123, 0);
		when(player3.getPendingBalance()).thenReturn((long)anteLevel);


		Result result1 = mock(Result.class);
		Result result2 = mock(Result.class);
		Result result3 = mock(Result.class);
		results.put(player1, result1);
		results.put(player2, result2);
		results.put(player3, result3);

		HandResult result = new HandResult(results, new ArrayList<RatedPlayerHand>(), Collections.<PotTransition>emptyList(), null, new ArrayList<Integer>());
		state.playerMap = new HashMap<Integer, PokerPlayer>();
		state.playerMap.put(player1.getId(), player1);
		state.playerMap.put(player2.getId(), player2);
		state.playerMap.put(player3.getId(), player3);
		
		state.currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();
		state.currentHandPlayerMap.put(player1.getId(), player1);
		state.currentHandPlayerMap.put(player2.getId(), player2);
		state.currentHandPlayerMap.put(player3.getId(), player3);
		
		Long winningsIncludingOwnBets = 344L;
		when(result1.getWinningsIncludingOwnBets()).thenReturn(winningsIncludingOwnBets );

		state.notifyHandFinished(result, HandEndStatus.NORMAL);

		verify(player1).addChips(winningsIncludingOwnBets);
		verify(state.serverAdapter).notifyHandEnd(result, HandEndStatus.NORMAL);
		verify(player1).setSitOutStatus(SitOutStatus.SITTING_OUT);
		verify(state.serverAdapter).scheduleTimeout(Mockito.anyLong());
		assertThat(state.isFinished(), is(true));
		assertThat(state.getCurrentState(), is(PokerState.WAITING_TO_START));
		verify(player3, Mockito.never()).setSitOutStatus(SitOutStatus.SITTING_OUT);
		
		verify(state.serverAdapter).notifyPlayerBalance(player1);
		verify(state.serverAdapter).notifyPlayerBalance(player2);
		verify(state.serverAdapter).notifyPlayerBalance(player3);

		verify(state.serverAdapter).notifyBuyInInfo(player1.getId(),true);

		verify(player2, Mockito.never()).setSitOutStatus(SitOutStatus.SITTING_OUT);
	}

	@Test
	public void testNotifyHandFinishedPendingBalanceTooHigh() {
		TimingProfile timingProfile = mock(TimingProfile.class);
		when(settings.getTiming()).thenReturn(timingProfile);
		when(settings.getMaxBuyIn()).thenReturn(100);

		state.init(null, settings);

		DefaultPokerPlayer player1 = new DefaultPokerPlayer(1);
		player1.setBalance(40L);
		player1.addPendingAmount(90L);

		DefaultPokerPlayer player2 = new DefaultPokerPlayer(2);
		player2.setBalance(220L);
		player2.addPendingAmount(120L);

		state.playerMap.put(player1.getId(), player1);
		state.playerMap.put(player2.getId(), player2);

		state.commitPendingBalances();

		assertThat(player1.getBalance(), is(100L));
		assertThat(player1.getPendingBalance(), is(30L));

		assertThat(player2.getBalance(), is(220L));
		assertThat(player2.getPendingBalance(), is(120L));

	}

	@Test
	public void testCall() {
		PokerState state = new PokerState();
		state.potHolder = mock(PotHolder.class);
		state.call();
		verify(state.potHolder).call();
	}


	@Test
	public void testCommitPendingBalances() {
		PokerState state = new PokerState();
		PokerSettings settings = mock(PokerSettings.class);
		when(settings.getMaxBuyIn()).thenReturn(10000);
		when(settings.getVariant()).thenReturn(PokerVariant.TELESINA);

		state.init(null, settings);

		PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
		PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
		Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
		playerMap.put(0, player1);
		playerMap.put(1, player2);
		state.playerMap = playerMap;

		state.commitPendingBalances();

		// Verify interaction and max buyin level
		verify(player1).commitPendingBalance(10000);
		verify(player2).commitPendingBalance(10000);
	}

	@SuppressWarnings("unchecked")
	@Test 
	public void testNotifyPotUpdated() {
		PokerState state = new PokerState();
		
		state.currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();
		PokerPlayer player0 = mock(PokerPlayer.class);
		when(player0.getId()).thenReturn(1337);
		PokerPlayer player1 = mock(PokerPlayer.class);
		when(player1.getId()).thenReturn(1338);
		PokerPlayer player2 = mock(PokerPlayer.class);
		when(player2.getId()).thenReturn(1339);
	
		state.currentHandPlayerMap.put(player0.getId(), player0);
		state.currentHandPlayerMap.put(player1.getId(), player1);
		state.currentHandPlayerMap.put(player2.getId(), player2);
		
		state.potHolder = mock(PotHolder.class);
		state.serverAdapter = mock(ServerAdapter.class);

		Collection<Pot> pots = new ArrayList<Pot>();
		when(state.potHolder.getPots()).thenReturn(pots);
		long totalPot = 3434L;
		when(state.potHolder.getTotalPotSize()).thenReturn(totalPot);
		BigDecimal totalRake = new BigDecimal("4444");

		when(state.potHolder.calculateRake()).thenReturn(new RakeInfoContainer((int) totalPot, totalRake.intValue(), null));
		RakeInfoContainer rakeInfoContainer = mock(RakeInfoContainer.class);

		when(state.potHolder.calculateRakeIncludingBetStacks(Mockito.anyCollection() )).thenReturn(rakeInfoContainer);

		Collection<PotTransition> potTransitions = new ArrayList<PotTransition>();
		state.notifyPotAndRakeUpdates(potTransitions);

		verify(state.serverAdapter).notifyPotUpdates(pots, potTransitions);
		verify(state.serverAdapter).notifyPlayerBalance(player0);
		verify(state.serverAdapter).notifyPlayerBalance(player1);
		verify(state.serverAdapter).notifyPlayerBalance(player2);

		ArgumentCaptor<RakeInfoContainer> rakeInfoCaptor = ArgumentCaptor.forClass(RakeInfoContainer.class);
		verify(state.serverAdapter).notifyRakeInfo(rakeInfoCaptor.capture());
		RakeInfoContainer rakeInfoContainer1 = rakeInfoCaptor.getValue();
		assertThat(rakeInfoContainer1, is(rakeInfoContainer));

	}

	@Test
	public void testGetTotalPotSize() {
		PokerState state = new PokerState();
		state.potHolder = mock(PotHolder.class);
		
		PokerPlayer player0 = mock(PokerPlayer.class);
		Integer player0id = 13371;
		when(player0.getId()).thenReturn(player0id );
		when(player0.getBetStack()).thenReturn(10L); // Bet
		
		PokerPlayer player1 = mock(PokerPlayer.class);
		Integer player1id = 13372;
		when(player1.getId()).thenReturn(player1id );
		when(player1.getBetStack()).thenReturn(10L); // Raise
		
		PokerPlayer player2 = mock(PokerPlayer.class);
		Integer player2id = 13373;
		when(player2.getId()).thenReturn(player2id );
		when(player2.getBetStack()).thenReturn(0L); // Nothing yet

		Collection<Pot> pots = new ArrayList<Pot>();
		when(state.potHolder.getPots()).thenReturn(pots);
		long totalPot = 500L; // already betted in earlier betting rounds
		when(state.potHolder.getTotalPotSize()).thenReturn(totalPot);
		
		Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
		playerMap.put(player0.getId(), player0);
		playerMap.put(player1.getId(), player1);
		playerMap.put(player2.getId(), player2);
		
		state.currentHandPlayerMap = playerMap;
		
		assertThat(state.getTotalPotsize(), is(520L));

	}

	@Test
	public void testPotsClearedAtStartOfHand() {
		PokerState state = new PokerState();
		state.gameType = mock(GameType.class);
		state.serverAdapter = mock(ServerAdapter.class);
		state.playerMap = new HashMap<Integer, PokerPlayer>();
		RakeSettings rakeSettings = TestUtils.createOnePercentRakeSettings();
		PokerSettings settings = new PokerSettings(0, 0, 0, null, null, 4, null, rakeSettings, "1");
		state.settings = settings;
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		state.playerMap.put(1, player1);
		state.playerMap.put(2, player2);
		when(player1.isSittingOut()).thenReturn(false);
		when(player2.isSittingOut()).thenReturn(false);

		assertThat(state.potHolder, nullValue());
		state.startHand();
		assertThat(state.potHolder, notNullValue());
	}

	@Test
	public void testResetValuesAtStartOfHand() {
		PokerState state = new PokerState();
		PotHolder oldPotHolder = new PotHolder(null);
		state.potHolder = oldPotHolder;
		state.gameType = mock(GameType.class);
		RakeSettings rakeSettings = TestUtils.createOnePercentRakeSettings();
		PokerSettings settings = new PokerSettings(0, 0, 0, null, null, 4, null, rakeSettings, "1");
		state.settings = settings;

		state.playerMap = new HashMap<Integer, PokerPlayer>();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);
		state.playerMap.put(1, player1);
		state.playerMap.put(2, player2);

		state.resetValuesAtStartOfHand();

		verify(player1).resetBeforeNewHand();
		verify(player2).resetBeforeNewHand();
		assertThat(state.potHolder, not(sameInstance(oldPotHolder)));
		verify(state.gameType).prepareNewHand();
	}

	@Test
	public void testNotifyBalancesAsStartOfHand() {
		PokerState state = new PokerState();
		PotHolder oldPotHolder = new PotHolder(null);
		state.potHolder = oldPotHolder;
		state.gameType = mock(GameType.class);
		RakeSettings rakeSettings = TestUtils.createOnePercentRakeSettings();
		PokerSettings settings = new PokerSettings(0, 0, 0, null, null, 4, null, rakeSettings, "1");
		state.settings = settings;

		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		state.serverAdapter = serverAdapter;

		state.playerMap = new HashMap<Integer, PokerPlayer>();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);

		int player1Id = 1337;
		int player2Id = 666;

		when(player1.getPendingBalance()).thenReturn(100L);
		when(player2.getPendingBalance()).thenReturn(100L);

		when(player1.getBalance()).thenReturn(10L);
		when(player2.getBalance()).thenReturn(10L);

		when(player1.getId()).thenReturn(player1Id);
		when(player2.getId()).thenReturn(player2Id);

		when(player1.isSittingOut()).thenReturn(false);
		when(player2.isSittingOut()).thenReturn(false);

		state.playerMap.put(player1Id, player1);
		state.playerMap.put(player2Id, player2);

		state.seatingMap.put(0, player1);
		state.seatingMap.put(1, player2);

		state.startHand();

		verify(state.serverAdapter).notifyPlayerBalance(player1);
		verify(state.serverAdapter).notifyPlayerBalance(player2);


	}

	@Test
	public void testNotifyStatusesAsStartOfHand() {
		PokerState state = new PokerState();
		PotHolder oldPotHolder = new PotHolder(null);
		state.potHolder = oldPotHolder;
		state.gameType = mock(GameType.class);
		RakeSettings rakeSettings = TestUtils.createOnePercentRakeSettings();
		PokerSettings settings = new PokerSettings(0, 0, 0, null, null, 4, null, rakeSettings, "1");
		state.settings = settings;

		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		state.serverAdapter = serverAdapter;

		state.playerMap = new HashMap<Integer, PokerPlayer>();
		PokerPlayer player1 = mock(PokerPlayer.class);
		PokerPlayer player2 = mock(PokerPlayer.class);

		int player1Id = 1337;
		int player2Id = 666;

		when(player1.getPendingBalance()).thenReturn(100L);
		when(player2.getPendingBalance()).thenReturn(100L);

		when(player1.getBalance()).thenReturn(10L);
		when(player2.getBalance()).thenReturn(10L);

		when(player1.getId()).thenReturn(player1Id);
		when(player2.getId()).thenReturn(player2Id);

		when(player1.isSittingOut()).thenReturn(false);
		when(player2.isSittingOut()).thenReturn(false);

		state.playerMap.put(player1Id, player1);
		state.playerMap.put(player2Id, player2);

		state.seatingMap.put(0, player1);
		state.seatingMap.put(1, player2);

		state.startHand();

		//verify(state.serverAdapter).notifyPlayerStatusChanged(player1Id, PokerPlayerStatus.SITIN);
		//verify(state.serverAdapter).notifyPlayerStatusChanged(player2Id, PokerPlayerStatus.SITIN);

	}

	@Test
	public void requestAction() {
		PokerState state = new PokerState();
		state.init(mock(RNGProvider.class), settings);


		state.serverAdapter = mock(ServerAdapter.class);
		
		state.potHolder = mock(PotHolder.class);
		Collection<Pot> pots = new ArrayList<Pot>();
		when(state.potHolder.getPots()).thenReturn(pots);
		long totalPot = 50L;
		when(state.potHolder.getTotalPotSize()).thenReturn(totalPot);

		ActionRequest actionRequest = mock(ActionRequest.class);
		state.requestAction(actionRequest);

		verify(actionRequest).setTotalPotSize(50L);
		verify(actionRequest).setTimeToAct(state.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
		verify(state.serverAdapter).requestAction(actionRequest);
	}

	@Test
	public void requestMultipleActions() {
		PokerState state = new PokerState();
		state.init(mock(RNGProvider.class), settings);
		state.serverAdapter = mock(ServerAdapter.class);
		
		state.potHolder = mock(PotHolder.class);
		Collection<Pot> pots = new ArrayList<Pot>();
		when(state.potHolder.getPots()).thenReturn(pots);
		long totalPot = 50L;
		when(state.potHolder.getTotalPotSize()).thenReturn(totalPot);

		ActionRequest actionRequest1 = mock(ActionRequest.class);
		ActionRequest actionRequest2 = mock(ActionRequest.class);

		Collection<ActionRequest> requests = Arrays.asList(actionRequest1, actionRequest2);
		state.requestMultipleActions(requests);

		verify(actionRequest1).setTimeToAct(state.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
		verify(actionRequest2).setTimeToAct(state.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
		verify(state.serverAdapter).requestMultipleActions(requests);
	}

	@Test
	public void shutdown() {
		PokerState state = new PokerState();
		state.shutdown();
		assertThat(state.getCurrentState(), is(PokerState.SHUTDOWN));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void illegalToMoveFromShutdownState() {
		PokerState state = new PokerState();
		state.setCurrentState(PokerState.SHUTDOWN);
		state.setCurrentState(PokerState.PLAYING);
	}
}
