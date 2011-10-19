package com.cubeia.poker.rounds.ante;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
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
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAnteRoundCreation() {
        
        int anteLevel = 1000;
        when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(dealerButtonSeatId), Mockito.any(SortedMap.class))).thenReturn(player1);
        
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        
        verify(player1).clearActionRequest();
        verify(player2).clearActionRequest();
        verify(state).notifyDealerButton(dealerButtonSeatId);
        verify(anteRoundHelper).requestAnte(player1, anteLevel, game);
        
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
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
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
        
        verify(anteRoundHelper).requestAnte(player2, anteLevel, game);
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
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
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
        verify(anteRoundHelper).requestAnte(player2, anteLevel, game);
    }    
    
    @Test
    public void testCancelHandWhenAllButOneRejectedAnte(){   	
    	AnteRound anteRound = new AnteRound(game, anteRoundHelper);

    	int player1Id = 1;
    	DefaultPokerPlayer player1 = createPlayer(player1Id, 1000L);
    	int player2Id = 2;
    	DefaultPokerPlayer player2 = createPlayer(player2Id, 1000L);
        
        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(player1Id, player1);
        playerMap.put(player2Id, player2);
        
        addAnteActionRequestToPlayer(player1);
        
        when(game.getState().getPlayerInCurrentHand(player1Id)).thenReturn(player1);
        when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);
        PokerAction action = new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET);
        
        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setOptions(Arrays.asList(new PossibleAction(PokerActionType.ANTE)));
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);
		anteRound.act(action);
		
		assertThat(anteRound.isCanceled(),is(true));
    }

    @Test
    public void testCancelHandWhenAllButOneRejectedAnte3Players(){   	
    	AnteRound anteRound = new AnteRound(game, anteRoundHelper);

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
        when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);
        
        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setOptions(Arrays.asList(new PossibleAction(PokerActionType.ANTE)));
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);

		PokerAction action1 = new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action1);
		assertThat(anteRound.isCanceled(), is(false));
		
        PokerAction action2 = new PokerAction(player2Id, PokerActionType.DECLINE_ENTRY_BET);
		anteRound.act(action2);
		assertThat(anteRound.isCanceled(),is(true));
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
    
    @Test
    public void testIsCanceled() {
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        
        // both declined: canceled
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(false);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(false);
        assertThat(anteRound.isCanceled(), is(true));
        
        // one declined: canceled
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(false);
        assertThat(anteRound.isCanceled(), is(true));
        
        // both accepted: not canceled
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(true);
        assertThat(anteRound.isCanceled(), is(true));
        
        // two accepted one declined: not canceled
        PokerPlayer player3 = mock(PokerPlayer.class);
        playerMap.put(2, player3);
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(true);
        when(player3.hasActed()).thenReturn(true);
        when(player3.hasPostedEntryBet()).thenReturn(false);
        assertThat(anteRound.isCanceled(), is(true));
    }
    

}
