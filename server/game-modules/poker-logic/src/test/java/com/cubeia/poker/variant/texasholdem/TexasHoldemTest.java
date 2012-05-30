package com.cubeia.poker.variant.texasholdem;

import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.variant.HandFinishedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.cubeia.poker.util.TestHelpers.assertSameListsDisregardingOrder;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TexasHoldemTest {

    private TexasHoldem texas;
    @Mock
    private PokerContext context;
    @Mock
    private RNGProvider rngProvider;
    @Mock
    private BettingRound bettingRound;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private HandFinishedListener listener;

    private MockPlayer player1;

    private MockPlayer player2;

    private PotHolder potHolder;

    private TreeMap<Integer, PokerPlayer> seatingMap;

    private Map<Integer, PokerPlayer> playerMap;

    @Before
    public void setup() {
        initMocks(this);
        RakeSettings rakeSettings = new RakeSettings(new BigDecimal("0.06"), 500, 150);
        potHolder = new PotHolder(new LinearRakeWithLimitCalculator(rakeSettings));
        when(context.getPotHolder()).thenReturn(potHolder);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);


        texas = new TexasHoldem(rngProvider);
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        texas.addHandFinishedListener(listener);

        player1 = new MockPlayer(1);
        player2 = new MockPlayer(2);

        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(1, player1);
        seatingMap.put(2, player2);

        playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(101, player1);
        playerMap.put(102, player2);

        when(context.getPlayerInDealerSeat()).thenReturn(player1);
        when(context.getLastPlayerToBeCalled()).thenReturn(player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getCurrentHandPlayerMap()).thenReturn(playerMap);

        createPot();
    }

    @Test
    public void testHandResultForFlushWithKicker() {
        // This is the scenario we want to set up, there are 4 clubs on the board, and the two players have one low club on their hand each.

        // So, given:
        when(context.getCommunityCards()).thenReturn(new Hand("8C 6D 9C AC 5C").getCards());
        player1.setPocketCards(new Hand("QS 3C"));
        player2.setPocketCards(new Hand("6C 9D"));

        // When:
        texas.handleFinishedHand();
        ArgumentCaptor<HandResult> captor = ArgumentCaptor.forClass(HandResult.class);
        verify(listener).handFinished(captor.capture(), eq(HandEndStatus.NORMAL));

        // Then: player2 should win, because his 6 of clubs is higher than player1's 3 of clubs in the flushes.
        HandResult handResult = captor.getValue();
        Result result = handResult.getResults().get(player2);
        RatedPlayerHand ratedPlayerHand = handResult.getPlayerHands().get(0);

        assertEquals(1200L, result.getWinningsIncludingOwnBets());
        assertEquals(Integer.valueOf(102), ratedPlayerHand.getPlayerId());
        assertSameListsDisregardingOrder(new Hand("6C 8C 9C AC 5C").getCards(), ratedPlayerHand.getBestHandCards());
        assertEquals(HandType.FLUSH, ratedPlayerHand.getBestHandType());
    }

    private void createPot() {
        potHolder.getActivePot().bet(player1, 600L);
        potHolder.getActivePot().bet(player2, 600L);
    }

}
