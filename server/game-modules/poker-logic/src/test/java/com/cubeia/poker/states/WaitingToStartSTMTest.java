package com.cubeia.poker.states;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerContext;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.sitout.SitoutCalculator;
import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class WaitingToStartSTMTest {

    @Mock
    private PokerContext context;
    
    @Mock
    private ServerAdapterHolder serverAdapterHolder;
    
    @Mock
    private StateChanger stateChanger;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private GameType gameType;

    private WaitingToStartSTM stateUnderTest;
    private PokerPlayer player1;
    private PokerPlayer player2;

    @Before
    public void setup() {
        initMocks(this);
        stateUnderTest = new WaitingToStartSTM(gameType, context, serverAdapterHolder, stateChanger);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(context.isTournamentTable()).thenReturn(false);
        player1 = mock(PokerPlayer.class);
        player2 = mock(PokerPlayer.class);
    }
    
    @Test
    public void testTimeout() {
        List<PokerPlayer> players = asList(player1, player2);
        when(context.getPlayersReadyToStartHand(Matchers.<Predicate<PokerPlayer>>any())).thenReturn(players);
        when(context.getSeatedPlayers()).thenReturn(players);

        stateUnderTest.timeout();

        verify(serverAdapter).performPendingBuyIns(players);
        verify(context).setHandFinished(false);
//        verify(stateUnderTest).setPlayersWithoutMoneyAsSittingOut(); TODO. Test this in another way?
        verify(context).commitPendingBalances();
        verify(context).sitOutPlayersMarkedForSitOutNextRound();
        verify(gameType).startHand();
        verify(serverAdapter).cleanupPlayers(Matchers.<SitoutCalculator>any());
    }

    @Test
    public void testTimeoutTooFewPlayers() {
        PokerPlayer player = mock(PokerPlayer.class);
        when(context.getPlayersReadyToStartHand(Matchers.<Predicate<PokerPlayer>>any())).thenReturn(asList(player));
        ArrayList<PokerPlayer> seatedPlayers = new ArrayList<PokerPlayer>();
        when(context.getSeatedPlayers()).thenReturn(seatedPlayers);

        stateUnderTest.timeout();

        verify(serverAdapter).performPendingBuyIns(seatedPlayers);
        verify(context).commitPendingBalances();
        verify(context).sitOutPlayersMarkedForSitOutNextRound();
        verify(context).setHandFinished(true);
        verify(stateChanger).changeState(isA(NotStartedSTM.class));
        verify(serverAdapter).cleanupPlayers(Matchers.<SitoutCalculator>any());

        verify(gameType, never()).startHand();
    }

    @Test
    public void testNotifyBalanceAtStartOfHand() {
        List<PokerPlayer> players = asList(player1, player2);
        when(context.getPlayersReadyToStartHand(Matchers.<Predicate<PokerPlayer>>any())).thenReturn(players);
        when(context.getSeatedPlayers()).thenReturn(players);

        stateUnderTest.startHand();

        verify(serverAdapter).notifyPlayerBalance(player1);
        verify(serverAdapter).notifyPlayerBalance(player2);
    }

}
