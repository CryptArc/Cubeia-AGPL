package com.cubeia.poker.rounds.ante;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;

public class AnteRoundHelperTest {

    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
    private AnteRoundHelper arh = new AnteRoundHelper();
    
    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testHasAllPlayersActed() {
        when(player1.hasActed()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player3.hasActed()).thenReturn(false);
        
        assertThat(arh.hasAllPlayersActed(asList(player1, player2, player3)), is(false));
        assertThat(arh.hasAllPlayersActed(asList(player1, player2)), is(true));
        assertThat(arh.hasAllPlayersActed(asList(player1, player3)), is(false));
        assertThat(arh.hasAllPlayersActed(Collections.<PokerPlayer>emptyList()), is(true));
    }
    
    @Test
    public void testNumberOfPlayersPayedAnte() {
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(false);
        when(player3.hasPostedEntryBet()).thenReturn(true);
        
        assertThat(arh.numberOfPlayersPayedAnte(asList(player1, player2, player3)), is(2));
        assertThat(arh.numberOfPlayersPayedAnte(asList(player1, player2)), is(1));
        assertThat(arh.numberOfPlayersPayedAnte(Collections.<PokerPlayer>emptyList()), is(0));
    }
    
    @Test
    public void testNumberOfPendingPlayers() {
        when(player1.hasActed()).thenReturn(true);
        when(player2.hasActed()).thenReturn(false);
        when(player3.hasActed()).thenReturn(true);
        
        assertThat(arh.numberOfPendingPlayers(asList(player1, player2, player3)), is(1));
        assertThat(arh.numberOfPendingPlayers(asList(player1, player2)), is(1));
        assertThat(arh.numberOfPendingPlayers(Collections.<PokerPlayer>emptyList()), is(0));
    }

//    @Test
//    public void testGetNextPlayerToAct() {
//        when(player1.hasActed()).thenReturn(true);
//        when(player2.hasActed()).thenReturn(true);
//        when(player3.hasActed()).thenReturn(false);
//        
//        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
//        seatingMap.put(0, player1);
//        seatingMap.put(1, player2);
//        seatingMap.put(2, player3);
//
//        assertThat(arh.getNextPlayerToAct(0, seatingMap), is(player3));
//        
//        when(player1.hasActed()).thenReturn(false);
//        when(player2.hasActed()).thenReturn(false);
//        when(player3.hasActed()).thenReturn(false);
//        
//        assertThat(arh.getNextPlayerToAct(-1, seatingMap), is(player1));
//        assertThat(arh.getNextPlayerToAct(0, seatingMap), is(player2));
//        assertThat(arh.getNextPlayerToAct(1, seatingMap), is(player3));
//        assertThat(arh.getNextPlayerToAct(2, seatingMap), is(player1));
//    }
    
    @Test
    public void testCanPlayerAct() {
        when(player1.hasActed()).thenReturn(false);
        when(player1.isAllIn()).thenReturn(false);
        when(player1.isSittingOut()).thenReturn(false);
        when(player1.hasFolded()).thenReturn(false);
        assertThat(arh.canPlayerAct(player1), is(true));
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.isAllIn()).thenReturn(false);
        when(player1.isSittingOut()).thenReturn(false);
        when(player1.hasFolded()).thenReturn(false);
        assertThat(arh.canPlayerAct(player1), is(false));
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.isAllIn()).thenReturn(true);
        when(player1.isSittingOut()).thenReturn(true);
        when(player1.hasFolded()).thenReturn(true);
        assertThat(arh.canPlayerAct(player1), is(false));
    }    

    @Test
    public void testRequestAnte() {
        int anteLevel = 100;
        GameType game = Mockito.mock(GameType.class);
        ActionRequest actionRequest = new ActionRequest();
        when(player1.getActionRequest()).thenReturn(actionRequest );
        
        arh.requestAnte(player1, anteLevel, game);
        
        verify(game).requestAction(actionRequest);

        ArgumentCaptor<PossibleAction> possibleActionCaptor = ArgumentCaptor.forClass(PossibleAction.class);
        
        verify(player1, times(2)).enableOption(possibleActionCaptor.capture());
        
        assertThat(possibleActionCaptor.getAllValues().get(0).getActionType(), is(PokerActionType.ANTE));
        assertThat(possibleActionCaptor.getAllValues().get(0).getMinAmount(), is((long) anteLevel));
        assertThat(possibleActionCaptor.getAllValues().get(1).getActionType(), is(PokerActionType.DECLINE_ENTRY_BET));
    }

    @Test
    public void testIsImpossibleToStartRound() {
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(true);
        when(player3.hasActed()).thenReturn(false);
        when(player3.hasPostedEntryBet()).thenReturn(false);
        List<PokerPlayer> players = Arrays.asList(player1, player2, player3);
        
        assertThat(arh.isImpossibleToStartRound(players), is(false));
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(false);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(false);
        when(player3.hasActed()).thenReturn(false);
        when(player3.hasPostedEntryBet()).thenReturn(false);
        
        assertThat(arh.isImpossibleToStartRound(players), is(true));
    }
    
    @Test
    public void testSetAllPendingPlayersToDeclineEntryBet() {
        when(player1.hasActed()).thenReturn(true);
        when(player2.hasActed()).thenReturn(false);
        when(player3.hasActed()).thenReturn(true);
        
        
        List<PokerPlayer> players = Arrays.asList(player1, player2, player3);
        arh.setAllPendingPlayersToDeclineEntryBet(players);
        verify(player1, never()).setHasActed(true);
        verify(player2).setHasActed(true);
        verify(player3, never()).setHasActed(true);
        
    }
    
}
