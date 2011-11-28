package com.cubeia.poker.rounds.betting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.rounds.ExposePrivateCardsRound;
import com.cubeia.poker.variant.telesina.Telesina;

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
