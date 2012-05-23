package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.states.ServerAdapterHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TelesinaPrepareHandTest {

    @Mock
    private PokerContext context;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Before
    public void setup() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPrepareNewHand() {
        List<Card> communityCards = mock(List.class);
        when(context.getCommunityCards()).thenReturn(communityCards);
        Telesina telesina = new Telesina(null, null, null, null);
        telesina.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        telesina.prepareNewHand();
        verify(communityCards).clear();
    }

}
