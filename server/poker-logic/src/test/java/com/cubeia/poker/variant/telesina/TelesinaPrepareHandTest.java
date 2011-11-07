package com.cubeia.poker.variant.telesina;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Card;

public class TelesinaPrepareHandTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testPrepareNewHand() {
        PokerState state = mock(PokerState.class);
        List<Card> communityCards = mock(List.class);
        Mockito.when(state.getCommunityCards()).thenReturn(communityCards);
        Telesina telesina = new Telesina(null, state , null, null);
        telesina.prepareNewHand();
        verify(communityCards).clear();
    }

}
