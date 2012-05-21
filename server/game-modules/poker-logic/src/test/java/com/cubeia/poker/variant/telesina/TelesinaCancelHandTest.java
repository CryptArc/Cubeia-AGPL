package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.variant.HandFinishedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TelesinaCancelHandTest {

    @Mock
    private PokerContext context;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private HandFinishedListener handFinishedListener;

    private Telesina telesina;

    @Before
    public void setup() {
        initMocks(this);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        telesina = new Telesina(null, null, null, null);
        telesina.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        telesina.addHandFinishedListener(handFinishedListener);
    }

    @Test
    public void testCancelHand() {
        PokerState state = mock(PokerState.class);
        Integer player1Id = 1222;
        Integer player2Id = 2333;

        PokerPlayer player1 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(player1Id);
        when(player1.getBetStack()).thenReturn(0L);

        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player2.getId()).thenReturn(player2Id);
        when(player2.getBetStack()).thenReturn(100L);

        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(player1Id, player1);
        playerMap.put(player2Id, player2);
        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);

        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);

        telesina.handleCanceledHand();

        verify(handFinishedListener).handFinished(Mockito.any(HandResult.class), Mockito.eq(HandEndStatus.CANCELED_TOO_FEW_PLAYERS));
        verify(serverAdapter, never()).notifyTakeBackUncalledBet(player1.getId(), 0);
        verify(serverAdapter).notifyTakeBackUncalledBet(player2.getId(), 100);
//        verify(state).notifyPlayerBalance(player1Id);
//        verify(state).notifyPlayerBalance(player2Id);

        verify(state).notifyRakeInfo();
    }
}
