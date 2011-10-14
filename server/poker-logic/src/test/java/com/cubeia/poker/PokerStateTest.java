package com.cubeia.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

public class PokerStateTest {

    @Test
    public void testNotifyHandFinished() {
        PokerState state = new PokerState();
        int anteLevel = 100;
        PokerSettings settings = mock(PokerSettings.class);
        when(settings.getRakeSettins()).thenReturn(TestUtils.createDummyRakeSettings());
        when(settings.getAnteLevel()).thenReturn(anteLevel);
        when(settings.getVariant()).thenReturn(PokerVariant.TELESINA);
        TimingProfile timingProfile = mock(TimingProfile.class);
		when(settings.getTiming()).thenReturn(timingProfile);
        
		state.init(null, settings);
        state.serverAdapter = mock(ServerAdapter.class);
        state.setTournamentTable(false);
        
        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        
        int player1Id = 1337;
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        Result result1 = mock(Result.class);
        when(player1.getBalance()).thenReturn((long)anteLevel-1);
        when(player1.getId()).thenReturn(player1Id);
        
        int player2Id = 666;
        PokerPlayer player2 = mock(PokerPlayer.class);
        Result result2 = mock(Result.class);
        when(player2.getBalance()).thenReturn((long)anteLevel);
        when(player2.getId()).thenReturn(player2Id);
        
       
        results.put(player1, result1);
        results.put(player2, result2);
        
        HandResult result = new HandResult(results, new ArrayList<RatedPlayerHand>(), Collections.<PotTransition>emptyList());
        state.playerMap = new HashMap<Integer, PokerPlayer>();
        
		state.playerMap.put(player1Id, player1);
        
		state.playerMap.put(player2Id, player2);
        
        Long winningsIncludingOwnBets = 344L;
        when(result1.getWinningsIncludingOwnBets()).thenReturn(winningsIncludingOwnBets );
        
        state.notifyHandFinished(result, HandEndStatus.NORMAL);
        
        verify(player1).addChips(winningsIncludingOwnBets);
        verify(state.serverAdapter).notifyHandEnd(result, HandEndStatus.NORMAL);
        verify(player1).commitPendingBalance();
        verify(player1).setSitOutStatus(SitOutStatus.SITTING_OUT);
        verify(state.serverAdapter).scheduleTimeout(Mockito.anyLong());
        assertThat(state.isFinished(), is(true));
        assertThat(state.currentState, is(PokerState.WAITING_TO_START));

        verify(state.serverAdapter).notifyBuyInInfo(player1Id,true);
        
        verify(player2, Mockito.never()).setSitOutStatus(SitOutStatus.SITTING_OUT);
    }

    @Test
    public void testCommitPendingBalances() {
        PokerState state = new PokerState();
        PokerPlayer player1 = Mockito.mock(PokerPlayer.class);
        PokerPlayer player2 = Mockito.mock(PokerPlayer.class);
        Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(0, player1);
        playerMap.put(1, player2);
        state.playerMap = playerMap;
        
        state.commitPendingBalances();
        
        verify(player1).commitPendingBalance();
        verify(player2).commitPendingBalance();
    }

    @Test 
    public void testNotifyPotUpdated() {
        PokerState state = new PokerState();
//        state.rakeCalculator = mock(RakeCalculatorImpl.class);
        state.potHolder = mock(PotHolder.class);
        state.serverAdapter = mock(ServerAdapter.class);
        
        Collection<Pot> pots = new ArrayList<Pot>();
        when(state.potHolder.getPots()).thenReturn(pots);
        
//        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer();
//        when(state.rakeCalculator.calculateRake(pots)).thenReturn(rakeInfoContainer);
        
        Collection<PotTransition> potTransitions = new ArrayList<PotTransition>();
        state.notifyPotAndRakeUpdates(potTransitions);
        
//        verify(state.rakeCalculator).calculateRake(pots);
        verify(state.serverAdapter).notifyPotUpdates(pots, potTransitions);
    }
    
}
