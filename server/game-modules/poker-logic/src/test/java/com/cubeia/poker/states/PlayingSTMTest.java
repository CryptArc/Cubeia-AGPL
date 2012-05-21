package com.cubeia.poker.states;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.telesina.Telesina;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlayingSTMTest {
    
    @Mock
    private PokerSettings settings;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private StateChanger stateChanger;
    
    int anteLevel;
    
    @Before
    public void setup() {
        initMocks(this);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
    }
    
    @Test
    public void testNotifyHandFinished() {
        TimingProfile timingProfile = mock(TimingProfile.class);
        when(settings.getTiming()).thenReturn(timingProfile);
        when(settings.getMaxBuyIn()).thenReturn(10000);

        PlayingSTM playing = new PlayingSTM();

        Telesina telesina = mock(Telesina.class);
        PokerContext context = new PokerContext(settings);
        
        playing.context = context;
        playing.gameType = telesina;
        playing.stateChanger = stateChanger;

        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();

        PokerPlayer player1 = createMockPlayer(1337, anteLevel - 1);
        PokerPlayer player2 = createMockPlayer(666, anteLevel);
        PokerPlayer player3 = createMockPlayer(123, 0);
        when(player3.getBalanceNotInHand()).thenReturn((long) anteLevel);

        Result result1 = mock(Result.class);
        Result result2 = mock(Result.class);
        Result result3 = mock(Result.class);
        results.put(player1, result1);
        results.put(player2, result2);
        results.put(player3, result3);

        HandResult result = new HandResult(results, new ArrayList<RatedPlayerHand>(), Collections.<PotTransition>emptyList(), null, new ArrayList<Integer>());
        context.playerMap = new HashMap<Integer, PokerPlayer>();
        context.playerMap.put(player1.getId(), player1);
        context.playerMap.put(player2.getId(), player2);
        context.playerMap.put(player3.getId(), player3);

        context.seatingMap = new TreeMap<Integer, PokerPlayer>();
        context.seatingMap.put(0, player1);
        context.seatingMap.put(1, player2);
        context.seatingMap.put(2, player3);

        context.currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();
        context.getCurrentHandPlayerMap().put(player1.getId(), player1);
        context.getCurrentHandPlayerMap().put(player2.getId(), player2);
        context.getCurrentHandPlayerMap().put(player3.getId(), player3);

        when(telesina.canPlayerAffordEntryBet(player1, settings, true)).thenReturn(false);
        when(telesina.canPlayerAffordEntryBet(player2, settings, true)).thenReturn(true);
        when(telesina.canPlayerAffordEntryBet(player3, settings, true)).thenReturn(true);

        Long winningsIncludingOwnBets = 344L;
        when(result1.getWinningsIncludingOwnBets()).thenReturn(winningsIncludingOwnBets);

        playing.handFinished(result, HandEndStatus.NORMAL);

        verify(player1).addChips(winningsIncludingOwnBets);

        InOrder inOrder = Mockito.inOrder(serverAdapter);
        inOrder.verify(serverAdapter).notifyHandEnd(result, HandEndStatus.NORMAL);
        inOrder.verify(serverAdapter).performPendingBuyIns(context.playerMap.values());


        verify(serverAdapter).scheduleTimeout(Mockito.anyLong());
        assertThat(context.isFinished(), is(true));
        verify(stateChanger).changeState(isA(WaitingToStartSTM.class));
        verify(player3, Mockito.never()).setSitOutStatus(SitOutStatus.SITTING_OUT);

        verify(serverAdapter).notifyPlayerBalance(player1);
        verify(serverAdapter).notifyPlayerBalance(player2);
        verify(serverAdapter).notifyPlayerBalance(player3);

        verify(serverAdapter).notifyBuyInInfo(player1.getId(), true);
        verify(player2, Mockito.never()).setSitOutStatus(SitOutStatus.SITTING_OUT);
    }
    
    private PokerPlayer createMockPlayer(int playerId, int balance) {
        PokerPlayer player = mock(PokerPlayer.class);
        when(player.getBalance()).thenReturn((long) balance);
        when(player.getId()).thenReturn(playerId);
        return player;
    }
    
}
