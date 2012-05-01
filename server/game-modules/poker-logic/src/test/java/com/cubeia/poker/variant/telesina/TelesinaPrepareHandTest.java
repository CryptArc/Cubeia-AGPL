package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Card;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TelesinaPrepareHandTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testPrepareNewHand() {
        PokerState state = mock(PokerState.class);
        List<Card> communityCards = mock(List.class);
        Mockito.when(state.getCommunityCards()).thenReturn(communityCards);
        Telesina telesina = new Telesina(null, state, null, null, null);
        telesina.prepareNewHand();
        verify(communityCards).clear();
    }

}
