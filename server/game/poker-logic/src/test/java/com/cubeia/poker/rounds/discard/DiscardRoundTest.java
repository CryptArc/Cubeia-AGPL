package com.cubeia.poker.rounds.discard;

import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.betting.PlayerToActCalculator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DiscardRoundTest {

    private DiscardRound round;
    @Mock
    private PokerContext context;
    @Mock
    private ServerAdapterHolder adapterHolder;
    @Mock
    private PlayerToActCalculator calculator;
    private SortedMap<Integer, PokerPlayer> players = new TreeMap<>();
    @Mock
    private ServerAdapter adapter;
    @Captor
    private ArgumentCaptor<DiscardAction> discardActionCaptor;

    @Before
    public void setUp() {
        initMocks(this);
        when(adapterHolder.get()).thenReturn(adapter);
        round = new DiscardRound(context, adapterHolder, calculator, 2, true);
    }

    @Test
    public void testTimeout() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        round.timeout();

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p1.getPocketCards().getCards().size(), is(1));
        assertThat(p2.getPocketCards().getCards().size(), is(1));

        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(1));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(2));
    }
}
