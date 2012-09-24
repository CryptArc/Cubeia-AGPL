/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.texasholdem.TexasHoldemFutureActionsCalculator;
import com.google.common.base.Predicate;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BettingRoundTest extends TestCase {

    private int minBet;

    private PokerContext context;

    @Mock
    private ServerAdapterHolder adapterHolder;

    @Mock
    private ServerAdapter adapter;

    @Mock
    private PokerSettings settings;

    private ActionRequest requestedAction;

    private BettingRound round;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        context = new PokerContext(settings);
        when(settings.getTiming()).thenReturn(new DefaultTimingProfile());
        when(settings.getRakeSettings()).thenReturn(RakeSettings.createNoLimitRakeSettings(new BigDecimal(0.01)));
        when(adapterHolder.get()).thenReturn(adapter);
        minBet = 10;
    }

    @Test
    public void testHeadsUpBetting() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);

        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse("Round should not be finished.", round.isFinished());

        verifyAndAct(p[1], PokerActionType.BET, 100);
        verifyAndAct(p[0], PokerActionType.FOLD, 100);

        assertTrue(round.isFinished());
    }

    @Test
    public void testCallAmount() {
        MockPlayer[] p = TestUtils.createMockPlayers(2, 100);

        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());
        act(p[1], PokerActionType.BET, 70);

        PossibleAction bet = requestedAction.getOption(PokerActionType.CALL);
        assertEquals(70, bet.getMaxAmount());
    }

    @Test
    public void testCallTellsState() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        context = createMockContext();
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);
        round.call(player);

        verify(context).callOrRaise();
    }

    @Test
    public void testCall() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        long betStack = 75L;
        when(player.getBetStack()).thenReturn(betStack);
        when(player.getBalance()).thenReturn(betStack * 10);
        preparePlayers(player);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);
        round.highBet = 100;

        long amountToCall = round.getAmountToCall(player);
        round.call(player);

        assertThat(amountToCall, is(round.highBet - betStack));
    }

    @Test
    public void testCallNotifiesRakeInfo() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        preparePlayers(player);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);
        round.call(player);

        verify(adapter).notifyRakeInfo(Mockito.<RakeInfoContainer>any());
    }

    @Test
    public void testHandleActionOnCallSetsAmountOnResponse() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        preparePlayers(player);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);
        round.highBet = 100;
        long betStack = 75L;
        when(player.getBetStack()).thenReturn(betStack);
        when(player.getBalance()).thenReturn(betStack * 10);

        PokerAction action = new PokerAction(1337, PokerActionType.CALL);

        round.handleAction(action, player);

        assertThat(action.getBetAmount(), is(round.highBet - betStack));
        verify(player).setHasActed(true);
    }

    @Test
    public void testRaise() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);

        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());

        verifyAndAct(p[1], PokerActionType.BET, 100);

        assertTrue(requestedAction.isOptionEnabled(PokerActionType.RAISE));
        verifyAndAct(p[0], PokerActionType.RAISE, 200);
    }

    @Test
    public void testRaiseNotifiesRakeInfo() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        preparePlayers(player);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        round.raise(player, 10L);

        verify(adapter).notifyRakeInfo(Mockito.<RakeInfoContainer>any());
    }

    @Test
    public void testRaiseNotifiesCallOrRaise() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        preparePlayers(player);
        context = createMockContext();
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        round.raise(player, 10L);
        verify(context).callOrRaise();
    }

    @Test
    public void testNoRaiseAllowedWhenAllOtherPlayersAreAllIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);
        preparePlayers(p);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());

        actMax(PokerActionType.BET);
        assertFalse(requestedAction.isOptionEnabled(PokerActionType.RAISE));
    }

    @Test
    public void testCallSetsLastPlayerToBeCalled() {
        MockPlayer[] p = createAndGetPlayersAddThemToTheGameAndCreateABettingRound(2);

        act(p[1], PokerActionType.BET, 100);
        act(p[0], PokerActionType.CALL, 100);

        PokerPlayer player = p[1];
        assertThat(round.getLastPlayerToBeCalled(), CoreMatchers.is(player));
    }

    @Test
    public void testRaiseAnAllInBetSetsLastCallerToAllInPlayer() {
        MockPlayer[] p = createAndGetPlayersAddThemToTheGameAndCreateABettingRound(2);
        act(p[1], PokerActionType.BET, 100);
        act(p[0], PokerActionType.RAISE, 200);
        PokerPlayer player = p[1];
        assertThat(round.getLastPlayerToBeCalled(), CoreMatchers.is(player));
    }

    @Test
    public void testBetNotifiesRakeInfo() {
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        preparePlayers(player);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        ActionRequest actionRequest = Mockito.mock(ActionRequest.class);
        when(player.getActionRequest()).thenReturn(actionRequest);
        PossibleAction possibleAction = Mockito.mock(PossibleAction.class);
        when(actionRequest.getOption(PokerActionType.BET)).thenReturn(possibleAction);
        when(possibleAction.getMinAmount()).thenReturn(5L);

        round.bet(player, 10L);

        verify(adapter).notifyRakeInfo(Mockito.<RakeInfoContainer>any());
    }

    @Test
    public void testTimeoutTwice() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);
        preparePlayers(p);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());

        round.timeout();
        round.timeout();

        assertTrue(round.isFinished());
    }

    @Test
    public void testTimeout() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);
        preparePlayers(p);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());

        verifyAndAct(p[1], PokerActionType.BET, 100);
        round.timeout();
        assertTrue(round.isFinished());
    }

    @Test
    public void testDealerLeft() {
        MockPlayer[] p = TestUtils.createMockPlayers(2);
        preparePlayers(p);

        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        assertFalse(round.isFinished());

        round.timeout();
        round.timeout();

        assertTrue(round.isFinished());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFutureActionsNotifiedWhenInitializingVanillaBetRound() {
        MockPlayer[] p = TestUtils.createMockPlayers(3);
        preparePlayers(p);

        // init round
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        // starting player gets empty list the others get check and fold
        verify(adapter).notifyFutureAllowedActions(eq(p[1]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[0]), argThat(new IsListOfNElements(2)));
        verify(adapter).notifyFutureAllowedActions(eq(p[2]), argThat(new IsListOfNElements(2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFutureActionsNotNotifiedWhenInitializingBetRoundAndAllPlayersSittingOut() {
        MockPlayer[] p = TestUtils.createMockPlayers(3);
        p[0].forceAllIn(true);
        p[1].forceAllIn(true);
        p[2].forceAllIn(true);
        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        verify(adapter).notifyFutureAllowedActions(eq(p[0]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[1]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[2]), argThat(new IsListOfNElements(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFutureActionsNotNotifiedWhenAllPlayersButOneAreAllIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(3);
        p[0].forceAllIn(true);
        p[1].forceAllIn(true);
        p[2].forceAllIn(false);
        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        verify(adapter).notifyFutureAllowedActions(eq(p[0]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[1]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[2]), argThat(new IsListOfNElements(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFutureActionsNotifiedWhenPlayerActed() {
        MockPlayer[] p = TestUtils.createMockPlayers(3);
        preparePlayers(p);

        // init round
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);

        // starting player gets empty list the others get check and fold
        verify(adapter).notifyFutureAllowedActions(eq(p[0]), argThat(new IsListOfNElements(2)));
        verify(adapter).notifyFutureAllowedActions(eq(p[1]), argThat(new IsListOfNElements(0)));
        verify(adapter).notifyFutureAllowedActions(eq(p[2]), argThat(new IsListOfNElements(2)));

        PokerAction action = new PokerAction(p[1].getId(), PokerActionType.CHECK);
        round.act(action);

        // next player gets empty list the others get check and fold
        verify(adapter, times(2)).notifyFutureAllowedActions(eq(p[0]), argThat(new IsListOfNElements(2)));
        verify(adapter).notifyFutureAllowedActions(eq(p[1]), argThat(new IsListOfNElements(2)));
        verify(adapter).notifyFutureAllowedActions(eq(p[2]), argThat(new IsListOfNElements(0)));
    }

    // HELPERS

    private void act(MockPlayer player, PokerActionType action, long amount) {
        PokerAction a = new PokerAction(player.getId(), action);
        a.setBetAmount(amount);
        round.act(a);
        requestedAction = getRequestedAction();
    }

    private void verifyAndAct(MockPlayer player, PokerActionType action, long amount) {
        requestedAction = getRequestedAction();
        assertTrue("Tried to " + action + " but available actions were: "
                + player.getActionRequest().getOptions(), player
                .getActionRequest().isOptionEnabled(action));
        assertTrue(requestedAction.isOptionEnabled(action));
        assertEquals(player.getId(), requestedAction.getPlayerId());
        act(player, action, amount);
    }

    private ActionRequest getRequestedAction() {
        ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
        verify(adapter, atLeastOnce()).requestAction(captor.capture());
        return captor.getValue();
    }

    @SuppressWarnings("rawtypes")
    class IsListOfNElements extends ArgumentMatcher<List> {
        private final int n;

        public IsListOfNElements(int n) {
            this.n = n;

        }

        public boolean matches(Object list) {
            return ((List) list).size() == n;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Should be a list of " + n + " elements");
        }
    }

    private void preparePlayers(PokerPlayer ... p) {
        for (PokerPlayer player : p) {
            context.addPlayer(player);
        }
        context.prepareHand(readyPlayerFilter());
    }

    private Predicate<PokerPlayer> readyPlayerFilter() {
        return new Predicate<PokerPlayer>() {
            @Override
            public boolean apply(@Nullable PokerPlayer pokerPlayer) {
                return true;
            }
        };
    }

    private PokerContext createMockContext() {
        PokerContext mock = mock(PokerContext.class);
        when(mock.getTimingProfile()).thenReturn(new DefaultTimingProfile());
        return mock;
    }

    private MockPlayer[] createAndGetPlayersAddThemToTheGameAndCreateABettingRound(int numberOfPlayers) {
        MockPlayer[] p = TestUtils.createMockPlayers(numberOfPlayers);
        preparePlayers(p);
        round = new BettingRound(0, context, adapterHolder, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator(), minBet);
        return p;
    }

    private void actMax(PokerActionType action) {
        requestedAction = getRequestedAction();
        PossibleAction option = requestedAction.getOption(action);
        PokerAction a = new PokerAction(requestedAction.getPlayerId(), action, option.getMaxAmount());
        round.act(a);
    }
}
