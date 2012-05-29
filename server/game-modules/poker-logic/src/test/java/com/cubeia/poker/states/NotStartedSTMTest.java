package com.cubeia.poker.states;

import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.GameType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NotStartedSTMTest {

    private NotStartedSTM state;

    @Mock
    private GameType gameType;
    @Mock
    private PokerContext context;
    @Mock
    private ServerAdapterHolder serverAdapterHolder;
    @Mock
    private StateChanger stateChanger;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private PokerSettings settings;

    @Before
    public void setup() {
        initMocks(this);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        state = new NotStartedSTM(gameType, context, serverAdapterHolder, stateChanger);
    }

    @Test
    public void shouldRequestBuyInWhenPlayerHasOpenedSession() {
        state.playerOpenedSession(1);

        verify(serverAdapter).notifyBuyInInfo(1, false);
    }
}
