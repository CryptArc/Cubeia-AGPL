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

    @Before
    public void setup() {
        initMocks(this);
        stateUnderTest = new WaitingToStartSTM(null, context, serverAdapterHolder, stateChanger);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
    }
    
    @Test
    public void testTimeout() {
        when(context.isTournamentTable()).thenReturn(false);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(context.getPlayersReadyToStartHand(Matchers.<Predicate<PokerPlayer>>any())).thenReturn(asList(player1, player2));

        ArrayList<PokerPlayer> seatedPlayers = new ArrayList<PokerPlayer>();
        when(context.getSeatedPlayers()).thenReturn(seatedPlayers);

        stateUnderTest.timeout();

        verify(serverAdapter).performPendingBuyIns(seatedPlayers);
        verify(context).setHandFinished(false);
//        verify(stateUnderTest).setPlayersWithoutMoneyAsSittingOut(); TODO. Test this in another way?
        verify(context).commitPendingBalances();
        verify(context).sitOutPlayersMarkedForSitOutNextRound();
        verify(gameType).startHand();
        verify(serverAdapter).cleanupPlayers(Matchers.<SitoutCalculator>any());
    }

    @Test
    public void testTimeoutTooFewPlayers() {
        when(context.isTournamentTable()).thenReturn(false);
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

}
