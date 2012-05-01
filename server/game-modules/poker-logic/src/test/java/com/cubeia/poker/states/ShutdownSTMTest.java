package com.cubeia.poker.states;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ShutdownSTMTest {

    @Test
    public void testTimeoutShouldNotDoAnything() {
        ShutdownSTM shutdownSTM = new ShutdownSTM();
        PokerState pokerGame = mock(PokerState.class);
        GameType gameType = mock(GameType.class);
        when(pokerGame.getGameType()).thenReturn(gameType);

        shutdownSTM.timeout(pokerGame);

        verifyZeroInteractions(gameType);
    }

    @Test
    public void testActShouldNotDoAnything() {
        ShutdownSTM shutdownSTM = new ShutdownSTM();
        PokerAction action = mock(PokerAction.class);
        PokerState pokerGame = mock(PokerState.class);
        GameType gameType = mock(GameType.class);
        when(pokerGame.getGameType()).thenReturn(gameType);

        shutdownSTM.act(action, pokerGame);

        verifyZeroInteractions(gameType);
    }

}
