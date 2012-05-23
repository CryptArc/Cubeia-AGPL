package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.variant.HandFinishedListener;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class TelesinaBettingRoundFinishedTest {

    @Mock
    PokerContext context;
    @Mock
    private PotHolder potHolder;
    @Mock
    private TelesinaDeckFactory deckFactory;
    @Mock
    private TelesinaDeck deck;
    @Mock
    private TelesinaRoundFactory roundFactory;
    @Mock
    private TelesinaDealerButtonCalculator dealerButtonCalculator;
    @Mock
    private HandFinishedListener handFinishedListener;
    @Mock
    private ServerAdapterHolder serverAdapterHolder;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private BlindsInfo blindsInfo;
    private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    private TelesinaForTesting telesina;

    private SortedMap<Integer, PokerPlayer> seatingMap;


    @Before
    public void setup() {
        initMocks(this);

        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(context.getPotHolder()).thenReturn(potHolder);
        when(context.getBlindsInfo()).thenReturn(blindsInfo);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        telesina = new TelesinaForTesting(new DummyRNGProvider(), deckFactory, roundFactory, dealerButtonCalculator);
        telesina.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        telesina.addHandFinishedListener(handFinishedListener);
    }

    @Test
    public void testLastPlayerToBeCalledIsSet() {
        telesina.startHand();
        BettingRound bettingRound = mock(BettingRound.class);
        when(bettingRound.getLastPlayerToBeCalled()).thenReturn(player1);
        telesina.visit(bettingRound);
        verify(context).setLastPlayerToBeCalled(player1);
    }

    PokerPlayer mockPlayer1;
    PokerPlayer mockPlayer2;
    BettingRound bettingRound;
    Map<Integer, PokerPlayer> playerMap;

    private void setupForHandInfoTests() {
        telesina.startHand();

        bettingRound = mock(BettingRound.class);

        mockPlayer1 = mock(PokerPlayer.class);
        mockPlayer2 = mock(PokerPlayer.class);
        seatingMap.put(1, player1);
        seatingMap.put(2, player2);

        playerMap = new HashMap<Integer, PokerPlayer>();
        playerMap.put(player1.getId(), player1);
        playerMap.put(player2.getId(), player2);
    }

    @Test
    public void testNotSendingHandInfoWhenWinOnAllFolds() {
        setupForHandInfoTests();

        when(mockPlayer1.hasFolded()).thenReturn(false);
        when(mockPlayer2.hasFolded()).thenReturn(true);
        when(context.countNonFoldedPlayers()).thenReturn(1);

        telesina.visit(bettingRound);

        ArgumentCaptor<HandResult> resultCaptor = ArgumentCaptor.forClass(HandResult.class);
        verify(handFinishedListener).handFinished(resultCaptor.capture(), Mockito.eq(HandEndStatus.NORMAL));
        HandResult hr = resultCaptor.getValue();
        Assert.assertThat(hr.getPlayerHands().size(), CoreMatchers.is(0));
    }

    @Test
    public void testSendingHandInfoWhenNormalWin() {
        setupForHandInfoTests();

        telesina.currentRoundId = 5;

        when(mockPlayer1.hasFolded()).thenReturn(false);
        when(mockPlayer2.hasFolded()).thenReturn(false);
        when(context.countNonFoldedPlayers()).thenReturn(2);

        when(context.getCurrentHandPlayerMap()).thenReturn(playerMap);

        telesina.visit(bettingRound);

        ArgumentCaptor<HandResult> resultCaptor = ArgumentCaptor.forClass(HandResult.class);
        verify(handFinishedListener).handFinished(resultCaptor.capture(), Mockito.eq(HandEndStatus.NORMAL));
        HandResult hr = resultCaptor.getValue();
        Assert.assertThat(hr.getPlayerHands().size(), CoreMatchers.is(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClearBetStacksOnFoldedPlayersWhenRoundFinishes() {
        telesina.startHand();

        BettingRound bettingRound = mock(BettingRound.class);

        PokerPlayer player1 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(1337);
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player2.getId()).thenReturn(1338);

        when(player1.getBetStack()).thenReturn(100L);
        when(player2.getBetStack()).thenReturn(0L);

        seatingMap.put(1, player1);
        seatingMap.put(2, player2);

        when(player1.hasFolded()).thenReturn(false);
        when(player2.hasFolded()).thenReturn(true);
        when(context.countNonFoldedPlayers()).thenReturn(1);

        telesina.visit(bettingRound);

        verify(serverAdapter).notifyRakeInfo(Matchers.<RakeInfoContainer>any());


    }


}
