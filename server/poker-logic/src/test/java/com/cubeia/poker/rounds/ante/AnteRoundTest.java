package com.cubeia.poker.rounds.ante;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.blinds.BlindsInfo;

public class AnteRoundTest {

	@Mock private GameType game;
	@Mock private IPokerState state;
	@Mock private PokerPlayer player1;
	@Mock private PokerPlayer player2;
	@Mock private AnteRoundHelper anteRoundHelper;
	private ActionRequest actionRequest1;
	private ActionRequest actionRequest2;
	private int dealerButtonSeatId = 1;
	private BlindsInfo blindsInfo;
	private SortedMap<Integer, PokerPlayer> playerMap;

	@Before
	public void setUp() {
		initMocks(this);

		when(player1.getId()).thenReturn(111);
		when(player2.getId()).thenReturn(222);

		when(game.getState()).thenReturn(state);

		actionRequest1 = new ActionRequest();
		actionRequest1.enable(new PossibleAction(PokerActionType.ANTE, 10));

		actionRequest2 = new ActionRequest();
		actionRequest2.enable(new PossibleAction(PokerActionType.ANTE, 10));

		when(player1.getActionRequest()).thenReturn(actionRequest1);
		when(player2.getActionRequest()).thenReturn(actionRequest2);


		playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(0, player1);
		playerMap.put(1, player2);
		when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);

		blindsInfo = mock(BlindsInfo.class);
		when(blindsInfo.getDealerButtonSeatId()).thenReturn(dealerButtonSeatId);
		when(game.getBlindsInfo()).thenReturn(blindsInfo);
		
		when(state.getPlayerInCurrentHand(player1.getId())).thenReturn(player1);
		when(state.getPlayerInCurrentHand(player2.getId())).thenReturn(player2);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreationAndAnteRequestBroadcast() {
		int anteLevel = 1000;
		when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);

		AnteRound anteRound = new AnteRound(game, anteRoundHelper);

		verify(player1).clearActionRequest();
		verify(player2).clearActionRequest();
		verify(state).notifyDealerButton(dealerButtonSeatId);
		
		ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
		verify(anteRoundHelper).requestAntes(captor.capture(), Mockito.eq(anteLevel), Mockito.eq(game));
		Collection<PokerPlayer> captured = captor.getValue();
		
		Iterator<PokerPlayer> iter = captured.iterator();
		Iterator<PokerPlayer> original = playerMap.values().iterator();
		while (iter.hasNext()) {
			assertThat(iter.next(), is(original.next()));
		}
		
		assertThat(anteRound.isFinished(), is(false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActOnAnte() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);
		int player1Id = 1337;
		when(state.getPlayerInCurrentHand(player1Id)).thenReturn(player1);
		int anteLevel = 1000;
		when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);

		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(false);
		//        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		long resultingBalance = 23434L;
		when(player1.getBalance()).thenReturn(resultingBalance);

		PokerAction action = new PokerAction(player1Id, PokerActionType.ANTE);
		anteRound.act(action);

		verify(player1).addBet(anteLevel);
		verify(player1).setHasActed(true);
		verify(player1).setHasPostedEntryBet(true);
		verify(serverAdapter).notifyActionPerformed(action, resultingBalance);

		verify(state).notifyBetStacksUpdated();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActOnAnteImpossibleToStartHandWillAutoDecline() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);
		int anteLevel = 1000;
		when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);

		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(false);
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		long resultingBalance1 = 23434L;
		when(player1.getBalance()).thenReturn(resultingBalance1);
		long resultingBalance2 = 349834L;
		when(player2.getBalance()).thenReturn(resultingBalance2);
		when(anteRoundHelper.isImpossibleToStartRound(Mockito.anyCollection())).thenReturn(true);

		when(anteRoundHelper.setAllPendingPlayersToDeclineEntryBet(Mockito.anyCollection())).thenReturn(Arrays.asList(player2));

		PokerAction action1 = new PokerAction(player1.getId(), PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action1);

		verify(serverAdapter).notifyActionPerformed(Mockito.eq(action1), Mockito.eq(resultingBalance1));

		ArgumentCaptor<PokerAction> captor = ArgumentCaptor.forClass(PokerAction.class);
		verify(serverAdapter, Mockito.times(2)).notifyActionPerformed(captor.capture(), Mockito.anyLong());

		PokerAction declineAction = captor.getAllValues().get(0);
		assertThat(declineAction, is(action1));

		PokerAction declineAction2 = captor.getAllValues().get(1);
		assertThat(declineAction2.getActionType(), is(PokerActionType.DECLINE_ENTRY_BET));
		assertThat(declineAction2.getPlayerId(), is(player2.getId()));
	}


	@SuppressWarnings("unchecked")
	@Test
	public void testActOnDeclineAnte() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);
		int player1Id = 1337;
		when(state.getPlayerInCurrentHand(player1Id)).thenReturn(player1);
		int anteLevel = 1000;
		when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);

		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(false);
		//        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);
		long resultingBalance = 343L;
		when(player1.getBalance()).thenReturn(resultingBalance);

		PokerAction action = new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action);

		verify(player1, never()).addBet(anteLevel);
		verify(player1).setHasActed(true);
		verify(player1).setHasPostedEntryBet(false);
		verify(serverAdapter).notifyActionPerformed(action, resultingBalance );
	}    

	@Test
	public void testCancelHandWhenAllButOneRejectedAnte(){   	
		AnteRound anteRound = new AnteRound(game, new AnteRoundHelper());

		int player1Id = 1;
		DefaultPokerPlayer player1 = createPlayer(player1Id, 1000L);
		int player2Id = 2;
		DefaultPokerPlayer player2 = createPlayer(player2Id, 1000L);

		SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(player1Id, player1);
		playerMap.put(player2Id, player2);

		addAnteActionRequestToPlayer(player1);

		when(game.getState().getPlayerInCurrentHand(player1Id)).thenReturn(player1);
		when(game.getState().getPlayerInCurrentHand(player2Id)).thenReturn(player2);
		when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);
		PokerAction action = new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET);

		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);
		anteRound.act(action);

		assertThat(anteRound.isCanceled(), is(true));
	}

	@Test
	public void testCancelHandWhenAllButOneRejectedAnte3Players(){   	
		AnteRound anteRound = new AnteRound(game, new AnteRoundHelper());

		int player1Id = 1;
		DefaultPokerPlayer player1 = createPlayer(player1Id, 1000L);
		int player2Id = 2;
		DefaultPokerPlayer player2 = createPlayer(player2Id, 1000L);
		int player3Id = 3;
		DefaultPokerPlayer player3 = createPlayer(player3Id, 1000L);

		SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(player1Id, player1);
		playerMap.put(player2Id, player2);
		playerMap.put(player3Id, player3);

		addAnteActionRequestToPlayer(player1);
		addAnteActionRequestToPlayer(player2);

		when(game.getState().getPlayerInCurrentHand(player1Id)).thenReturn(player1);
		when(game.getState().getPlayerInCurrentHand(player2Id)).thenReturn(player2);
		when(game.getState().getPlayerInCurrentHand(player3Id)).thenReturn(player3);
		when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);


		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		PokerAction action1 = new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action1);
		assertThat(anteRound.isCanceled(), is(false));

		PokerAction action2 = new PokerAction(player2Id, PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action2);
		assertThat(anteRound.isCanceled(), is(true));
	}

	private void addAnteActionRequestToPlayer(DefaultPokerPlayer player) {
		ActionRequest playerActionRequest = new ActionRequest();
		playerActionRequest.enable(new PossibleAction(PokerActionType.ANTE));
		player.setActionRequest(playerActionRequest);
	}

	private DefaultPokerPlayer createPlayer(int playerId, long balance) {
		DefaultPokerPlayer player = new DefaultPokerPlayer(playerId);
		player.setBalance(1000L);
		when(state.getPlayerInCurrentHand(playerId)).thenReturn(player);
		return player;
	}

	@Test
	public void testIsFinished() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);

		when(player1.hasActed()).thenReturn(true);
		when(player1.hasPostedEntryBet()).thenReturn(true);
		when(player2.hasActed()).thenReturn(false);
		assertThat(anteRound.isFinished(), is(false));

		when(player1.hasActed()).thenReturn(true);
		when(player1.hasPostedEntryBet()).thenReturn(true);
		when(player2.hasActed()).thenReturn(true);
		when(player2.hasPostedEntryBet()).thenReturn(true);
		assertThat(anteRound.isFinished(), is(true));
	}

	@Test
	public void testIsFinishedFalseOnTooFewAntes() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);

		when(player1.hasActed()).thenReturn(true);
		when(player1.hasPostedEntryBet()).thenReturn(false);
		when(player2.hasActed()).thenReturn(true);
		when(player2.hasPostedEntryBet()).thenReturn(false);
		assertThat(anteRound.isFinished(), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsCanceled() {
		AnteRound anteRound = new AnteRound(game, anteRoundHelper);

		// both declined: canceled
		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(true);
		when(anteRoundHelper.numberOfPlayersPayedAnte(Mockito.anyCollection())).thenReturn(0);
		assertThat(anteRound.isCanceled(), is(true));

		// one declined: canceled
		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(true);
		when(anteRoundHelper.numberOfPlayersPayedAnte(Mockito.anyCollection())).thenReturn(1);
		assertThat(anteRound.isCanceled(), is(true));

		// both accepted: not canceled
		when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(true);
		when(anteRoundHelper.numberOfPlayersPayedAnte(Mockito.anyCollection())).thenReturn(2);
		assertThat(anteRound.isCanceled(), is(false));
	}

	@Test
	public void testTimeoutDeclinesAllOutstandingAntes() {
		//        for (PokerPlayer player : getAllSeatedPlayers()) {
		//            if (!player.hasActed()) {
		//                log.debug("Player["+player+"] ante timed out. Will decline entry bet.");
		//                act(new PokerAction(player.getId(), PokerActionType.DECLINE_ENTRY_BET, true));
		//            }
		//        }

		AnteRound anteRound = new AnteRound(game, anteRoundHelper);

		SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(1, player1);
		playerMap.put(2, player2);
		when(player1.hasActed()).thenReturn(true);
		when(player2.hasActed()).thenReturn(false);

		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);

		anteRound.timeout();

		verify(player1, never()).setHasActed(true);
		verify(player2).setHasActed(true);
		verify(player2).setHasFolded(true);
		verify(player2).setHasPostedEntryBet(false);
		verify(serverAdapter).notifyActionPerformed(Mockito.any(PokerAction.class), Mockito.anyLong());

		//        player.setHasActed(true);
		//        player.setHasFolded(true);
		//        player.setHasPostedEntryBet(false);
		//        game.getServerAdapter().notifyActionPerformed(action, player.getBalance());


	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSitOutDeclinesAnte() {
		
		when(player2.isSittingOut()).thenReturn(true);
		
		SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(1, player1);
		playerMap.put(2, player2);
		when(player1.hasActed()).thenReturn(true);
		when(player2.hasActed()).thenReturn(false);
		
		
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);

		AnteRound anteRound = new AnteRound(game, anteRoundHelper);
		
		// Verify that only player 1 got a request for Ante since player 2 is sitting out
		ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
		verify(anteRoundHelper).requestAntes(captor.capture(), Mockito.eq(0), Mockito.eq(game));
		Collection<PokerPlayer> captured = captor.getValue();
		
		assertThat(captured.size(), is(1));
		assertThat((PokerPlayer) captured.iterator().next(), is(player1));
		
		// Player 1 posts ANTE
		// Player 2 should have been auto act to decline ante
		
		PokerAction action1 = new PokerAction(player1.getId(), PokerActionType.ANTE);
		anteRound.act(action1);
		
		verify(player1).setHasActed(true);
		verify(player1).setHasPostedEntryBet(true);
		verify(player2).setHasActed(true);
		verify(player2).setHasFolded(true);
		verify(player2).setHasPostedEntryBet(false);
	}

}
