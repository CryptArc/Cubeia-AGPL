package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.rounds.ExposePrivateCardsRound;
import com.cubeia.poker.variant.telesina.Telesina;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ExposePrivateCardsRoundTest {


    @Test
    public void testExposePrivateCardsRound() {

        Telesina game = mock(Telesina.class);

        IPokerState state = mock(PokerState.class);
        when(game.getState()).thenReturn(state);
        new ExposePrivateCardsRound(game);
        verify(state).exposeShowdownCards();
        verify(game).sendAllNonFoldedPlayersBestHand();

    }

}
