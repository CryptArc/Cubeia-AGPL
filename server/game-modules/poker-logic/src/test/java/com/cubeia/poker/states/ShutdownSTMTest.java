package com.cubeia.poker.states;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ShutdownSTMTest {

    @Mock
    private GameType gameType;

    @Mock
    private PokerContext pokerContext;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private StateChanger stateChanger;

    private ShutdownSTM shutdownSTM;

    @Before
    public void setup() {
        initMocks(this);
        shutdownSTM = new ShutdownSTM(gameType, pokerContext, serverAdapterHolder, stateChanger);
    }

    @Test
    public void testTimeoutShouldNotDoAnything() {
        shutdownSTM.timeout();
        verifyZeroInteractions(gameType, pokerContext, serverAdapterHolder, stateChanger);
    }

    @Test
    public void testActShouldNotDoAnything() {
        PokerAction action = mock(PokerAction.class);
        shutdownSTM.act(action);

        verifyZeroInteractions(gameType, pokerContext, serverAdapterHolder, stateChanger);
    }

}
