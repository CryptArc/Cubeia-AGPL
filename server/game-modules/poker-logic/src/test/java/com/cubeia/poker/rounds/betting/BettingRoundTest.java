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

import com.cubeia.poker.*;
import com.cubeia.poker.action.*;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.texasholdem.TexasHoldemFutureActionsCalculator;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class BettingRoundTest extends TestCase  { // implements TestListener

    public void testSomething() {
        
    }

    // TODO FIXTESTS
//    private ActionRequest requestedAction;
//
//    private MockGame game;
//
//    private BettingRound round;
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        game = new MockGame();
//        game.listeners.add(this);
//
//    }
//
//    @Test
//    public void testHeadsUpBetting() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        verifyAndAct(p[1], PokerActionType.BET, 100);
//        verifyAndAct(p[0], PokerActionType.FOLD, 100);
//
//        assertTrue(round.isFinished());
//    }
//
//    @Test
//    public void testCallAmount() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2, 100);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//        act(p[1], PokerActionType.BET, 70);
//
//        PossibleAction bet = requestedAction.getOption(PokerActionType.CALL);
//        assertEquals(70, bet.getMaxAmount());
//
//
//    }
//
//    @Test
//    public void testCallTellsState() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//        round.call(player);
//
//        verify(state).callOrRaise();
//
//    }
//
//    @Test
//    public void testCall() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//        long betStack = 75L;
//        when(player.getBetStack()).thenReturn(betStack);
//        when(player.getBalance()).thenReturn(betStack * 10);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//        round.highBet = 100;
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        long amountCalled = round.call(player);
//
//        verify(state).callOrRaise();
//        assertThat(amountCalled, is(round.highBet - betStack));
//
//    }
//
//    @Test
//    public void testCallNotifiesBetStackUpdates() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round.call(player);
//
//        verify(state).notifyBetStacksUpdated();
//    }
//
//    @Test
//    public void testHandleActionOnCallSetsAmountOnResponse() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//        round.highBet = 100;
//        long betStack = 75L;
//        when(player.getBetStack()).thenReturn(betStack);
//        when(player.getBalance()).thenReturn(betStack * 10);
//
//        PokerAction action = new PokerAction(1337, PokerActionType.CALL);
//
//        round.handleAction(action, player);
//
//        assertThat(action.getBetAmount(), is(round.highBet - betStack));
//        verify(player).setHasActed(true);
//
//    }
//
//    @Test
//    public void testRaise() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        verifyAndAct(p[1], PokerActionType.BET, 100);
//
//        assertTrue(requestedAction.isOptionEnabled(PokerActionType.RAISE));
//        verifyAndAct(p[0], PokerActionType.RAISE, 200);
//
//
//    }
//
//    @Test
//    public void testRaiseNotifiesBetStackUpdates() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round.raise(player, 10L);
//
//        verify(state).notifyBetStacksUpdated();
//    }
//
//    @Test
//    public void testRaiseNotifiesCallOrRaise() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round.raise(player, 10L);
//
//        verify(state).callOrRaise();
//    }
//
//    @Test
//    public void testNoRaiseAllowedWhenAllOtherPlayersAreAllIn() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        actMax(PokerActionType.BET);
//        assertFalse(requestedAction.isOptionEnabled(PokerActionType.RAISE));
//    }
//
//
//    private MockPlayer[] createAndGetPlayersAddThemToTheGameAndCreateABettingRound(int numberOfPlayers) {
//        MockPlayer[] p = TestUtils.createMockPlayers(numberOfPlayers);
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//        return p;
//    }
//
//    @Test
//    public void testCallSetsLastPlayerToBeCalled() {
//        MockPlayer[] p = createAndGetPlayersAddThemToTheGameAndCreateABettingRound(2);
//
//        act(p[1], PokerActionType.BET, 100);
//        act(p[0], PokerActionType.CALL, 100);
//
//        PokerPlayer player = p[1];
//        assertThat(round.getLastPlayerToBeCalled(), CoreMatchers.is(player));
//    }
//
//    @Test
//    public void testRaiseAnAllInBetSetsLastCallerToAllInPlayer() {
//        MockPlayer[] p = createAndGetPlayersAddThemToTheGameAndCreateABettingRound(2);
//        act(p[1], PokerActionType.BET, 100);
//        act(p[0], PokerActionType.RAISE, 200);
//        PokerPlayer player = p[1];
//        assertThat(round.getLastPlayerToBeCalled(), CoreMatchers.is(player));
//    }
//
//    @Test
//    public void testBetNotifiesBetStackUpdates() {
//        GameType game = mock(GameType.class);
//        IPokerState state = mock(IPokerState.class);
//        when(game.getState()).thenReturn(state);
//        PokerPlayer player = Mockito.mock(PokerPlayer.class);
//
//        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        ActionRequest actionRequest = Mockito.mock(ActionRequest.class);
//        when(player.getActionRequest()).thenReturn(actionRequest);
//        PossibleAction possibleAction = Mockito.mock(PossibleAction.class);
//        when(actionRequest.getOption(PokerActionType.BET)).thenReturn(possibleAction);
//        when(possibleAction.getMinAmount()).thenReturn(5L);
//
//        round.bet(player, 10L);
//
//        verify(state).notifyBetStacksUpdated();
//    }
//
//    private void actMax(PokerActionType action) {
//        PossibleAction option = requestedAction.getOption(action);
//        PokerAction a = new PokerAction(requestedAction.getPlayerId(), action, option.getMaxAmount());
//        round.act(a);
//    }
//
//    @Test
//    public void testTimeoutTwice() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        round.timeout();
//        round.timeout();
//
//        assertTrue(round.isFinished());
//    }
//
//    @Test
//    public void testTimeout() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        verifyAndAct(p[1], PokerActionType.BET, 100);
//        round.timeout();
//        assertTrue(round.isFinished());
//    }
//
//    @Test
//    public void testDealerLeft() {
//        MockPlayer[] p = TestUtils.createMockPlayers(2);
//
//        game.addPlayers(p);
//        round = new BettingRound(game, 3, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new TexasHoldemFutureActionsCalculator());
//
//        assertFalse(game.roundFinished);
//
//        round.timeout();
//        round.timeout();
//
//        assertTrue(round.isFinished());
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testFutureActionsNotifiedWhenInitializingVanillaBetRound() {
//        //setup players
//        int p0Id = 1337;
//        PokerPlayer p0 = mock(PokerPlayer.class);
//        when(p0.getId()).thenReturn(p0Id);
//        when(p0.getBalance()).thenReturn(100L);
//
//        int p1Id = 1338;
//        PokerPlayer p1 = mock(PokerPlayer.class);
//        when(p1.getId()).thenReturn(p1Id);
//        when(p1.getBalance()).thenReturn(100L);
//
//        int p2Id = 1339;
//        PokerPlayer p2 = mock(PokerPlayer.class);
//        when(p2.getId()).thenReturn(p2Id);
//        when(p2.getBalance()).thenReturn(100L);
//
//        //setup state
//        IPokerState state = mock(IPokerState.class);
//        GameType game = mock(Telesina.class);
//        when(game.getState()).thenReturn(state);
//        ServerAdapter serverAdapter = mock(ServerAdapter.class);
//        when(game.getServerAdapterHolder()).thenReturn(serverAdapter);
//
//        // all players ready to start
//        ArrayList<PokerPlayer> playersReadyToStart = new ArrayList<PokerPlayer>();
//        playersReadyToStart.add(p0);
//        playersReadyToStart.add(p1);
//        playersReadyToStart.add(p2);
//        when(state.getPlayersReadyToStartHand()).thenReturn(playersReadyToStart);
//
//        // all players are in seating map
//        SortedMap<Integer, PokerPlayer> playerSeatingMap = new TreeMap<Integer, PokerPlayer>();
//        playerSeatingMap.put(0, p0);
//        playerSeatingMap.put(1, p1);
//        playerSeatingMap.put(2, p2);
//        when(state.getCurrentHandSeatingMap()).thenReturn(playerSeatingMap);
//
//        // all players are in playermap
//        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
//        playerMap.put(p0.getId(), p0);
//        playerMap.put(p1.getId(), p1);
//        playerMap.put(p2.getId(), p2);
//        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
//
//        // p0 starts to act
//        PlayerToActCalculator playerToActCalculator = mock(PlayerToActCalculator.class);
//        when(playerToActCalculator.getFirstPlayerToAct(0, playerSeatingMap, new ArrayList<Card>())).thenReturn(p0);
//
//        // p1 is next to act
//        when(playerToActCalculator.getNextPlayerToAct(0, playerSeatingMap)).thenReturn(p1);
//
//        // init round
//        ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
//        FutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator();
//        round = new BettingRound(game, 0, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
//
//        // starting player gets empty list the others get check and fold
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1), argThat(new IsListOfNElements(2)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p2), argThat(new IsListOfNElements(2)));
//
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testFutureActionsNotNotifiedWhenInitializingBetRoundAndAllPlayersSittingOut() {
//        //setup players
//        int p0Id = 1337;
//        PokerPlayer p0 = mock(PokerPlayer.class);
//        when(p0.getId()).thenReturn(p0Id);
//        when(p0.getBalance()).thenReturn(100L);
//        when(p0.isSittingOut()).thenReturn(true);
//        when(p0.isAllIn()).thenReturn(true);
//
//        int p1Id = 1338;
//        PokerPlayer p1 = mock(PokerPlayer.class);
//        when(p1.getId()).thenReturn(p1Id);
//        when(p1.getBalance()).thenReturn(100L);
//        when(p1.isSittingOut()).thenReturn(true);
//        when(p1.isAllIn()).thenReturn(true);
//
//        int p2Id = 1339;
//        PokerPlayer p2 = mock(PokerPlayer.class);
//        when(p2.getId()).thenReturn(p2Id);
//        when(p2.getBalance()).thenReturn(100L);
//        when(p2.isSittingOut()).thenReturn(true);
//        when(p2.isAllIn()).thenReturn(true);
//
//        //setup state
//        IPokerState state = mock(IPokerState.class);
//        GameType game = mock(Telesina.class);
//        when(game.getState()).thenReturn(state);
//        ServerAdapter serverAdapter = mock(ServerAdapter.class);
//        when(game.getServerAdapterHolder()).thenReturn(serverAdapter);
//
//        // All sitting out and all in
//        when(state.isEveryoneSittingOut()).thenReturn(true);
//
//        // all players are in seating map
//        SortedMap<Integer, PokerPlayer> playerSeatingMap = new TreeMap<Integer, PokerPlayer>();
//        playerSeatingMap.put(0, p0);
//        playerSeatingMap.put(1, p1);
//        playerSeatingMap.put(2, p2);
//        when(state.getCurrentHandSeatingMap()).thenReturn(playerSeatingMap);
//
//        // all players are in playermap
//        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
//        playerMap.put(p0.getId(), p0);
//        playerMap.put(p1.getId(), p1);
//        playerMap.put(p2.getId(), p2);
//        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
//
//        // p0 starts to act
//        PlayerToActCalculator playerToActCalculator = mock(PlayerToActCalculator.class);
//        when(playerToActCalculator.getFirstPlayerToAct(0, playerSeatingMap, new ArrayList<Card>())).thenReturn(p0);
//
//        // p1 is next to act
//        when(playerToActCalculator.getNextPlayerToAct(0, playerSeatingMap)).thenReturn(p1);
//
//        // init round
//        ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
//        FutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator();
//        round = new BettingRound(game, 0, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
//
//        // starting player gets empty list the others get check and fold
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p2), argThat(new IsListOfNElements(0)));
//
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testFutureActionsNotNotifiedWhenAllPlayersButOneAreAllIn() {
//        //setup players
//        int p0Id = 1337;
//        PokerPlayer p0 = mock(PokerPlayer.class);
//        when(p0.getId()).thenReturn(p0Id);
//        when(p0.getBalance()).thenReturn(100L);
//        when(p0.isAllIn()).thenReturn(false);
//
//        int p1Id = 1338;
//        PokerPlayer p1 = mock(PokerPlayer.class);
//        when(p1.getId()).thenReturn(p1Id);
//        when(p1.getBalance()).thenReturn(0L);
//        when(p1.isAllIn()).thenReturn(true);
//
//        int p2Id = 1339;
//        PokerPlayer p2 = mock(PokerPlayer.class);
//        when(p2.getId()).thenReturn(p2Id);
//        when(p2.getBalance()).thenReturn(0L);
//        when(p2.isAllIn()).thenReturn(true);
//
//        //setup state
//        IPokerState state = mock(IPokerState.class);
//        GameType game = mock(Telesina.class);
//        when(game.getState()).thenReturn(state);
//        ServerAdapter serverAdapter = mock(ServerAdapter.class);
//        when(game.getServerAdapterHolder()).thenReturn(serverAdapter);
//
//        // all players ready to start
//        ArrayList<PokerPlayer> playersReadyToStart = new ArrayList<PokerPlayer>();
//        playersReadyToStart.add(p0);
//        playersReadyToStart.add(p1);
//        playersReadyToStart.add(p2);
//        when(state.getPlayersReadyToStartHand()).thenReturn(playersReadyToStart);
//
//        // all players are in seating map
//        SortedMap<Integer, PokerPlayer> playerSeatingMap = new TreeMap<Integer, PokerPlayer>();
//        playerSeatingMap.put(0, p0);
//        playerSeatingMap.put(1, p1);
//        playerSeatingMap.put(2, p2);
//        when(state.getCurrentHandSeatingMap()).thenReturn(playerSeatingMap);
//
//        // all players are in playermap
//        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
//        playerMap.put(p0.getId(), p0);
//        playerMap.put(p1.getId(), p1);
//        playerMap.put(p2.getId(), p2);
//        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
//
//        // p0 starts to act
//        PlayerToActCalculator playerToActCalculator = mock(PlayerToActCalculator.class);
//        when(playerToActCalculator.getFirstPlayerToAct(0, playerSeatingMap, new ArrayList<Card>())).thenReturn(p0);
//
//        // p1 is next to act
//        when(playerToActCalculator.getNextPlayerToAct(0, playerSeatingMap)).thenReturn(p1);
//
//        // init round
//        ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
//        FutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator();
//        round = new BettingRound(game, 0, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
//
//        // starting player gets empty list the others get check and fold
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p2), argThat(new IsListOfNElements(0)));
//
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testFutureActionsNotifiedWhenPlayerActed() {
//        //setup players
//        int p0Id = 1337;
//        PokerPlayer p0 = mock(PokerPlayer.class);
//        when(p0.getId()).thenReturn(p0Id);
//        when(p0.getBalance()).thenReturn(100L);
//        when(p0.isAllIn()).thenReturn(false);
//
//        int p1Id = 1338;
//        PokerPlayer p1 = mock(PokerPlayer.class);
//        when(p1.getId()).thenReturn(p1Id);
//        when(p1.getBalance()).thenReturn(100L);
//        when(p1.isAllIn()).thenReturn(false);
//
//        int p2Id = 1339;
//        PokerPlayer p2 = mock(PokerPlayer.class);
//        when(p2.getId()).thenReturn(p2Id);
//        when(p2.getBalance()).thenReturn(100L);
//        when(p2.isAllIn()).thenReturn(false);
//
//        //setup state
//        IPokerState state = mock(IPokerState.class);
//        GameType game = mock(Telesina.class);
//        when(game.getState()).thenReturn(state);
//        ServerAdapter serverAdapter = mock(ServerAdapter.class);
//        when(game.getServerAdapterHolder()).thenReturn(serverAdapter);
//
//        // all players ready to start
//        ArrayList<PokerPlayer> playersReadyToStart = new ArrayList<PokerPlayer>();
//        playersReadyToStart.add(p0);
//        playersReadyToStart.add(p1);
//        playersReadyToStart.add(p2);
//        when(state.getPlayersReadyToStartHand()).thenReturn(playersReadyToStart);
//
//        // all players are in seating map
//        SortedMap<Integer, PokerPlayer> playerSeatingMap = new TreeMap<Integer, PokerPlayer>();
//        playerSeatingMap.put(0, p0);
//        playerSeatingMap.put(1, p1);
//        playerSeatingMap.put(2, p2);
//        when(state.getCurrentHandSeatingMap()).thenReturn(playerSeatingMap);
//
//        // all players are in playermap
//        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
//        playerMap.put(p0.getId(), p0);
//        playerMap.put(p1.getId(), p1);
//        playerMap.put(p2.getId(), p2);
//        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
//
//        // get player in current hand
//        when(state.getPlayerInCurrentHand(p0Id)).thenReturn(p0);
//        when(state.getPlayerInCurrentHand(p1Id)).thenReturn(p1);
//        when(state.getPlayerInCurrentHand(p2Id)).thenReturn(p2);
//
//        // p0 starts to act
//        PlayerToActCalculator playerToActCalculator = mock(PlayerToActCalculator.class);
//        when(playerToActCalculator.getFirstPlayerToAct(0, playerSeatingMap, new ArrayList<Card>())).thenReturn(p0);
//
//        // p1 is next to act
//        when(playerToActCalculator.getNextPlayerToAct(0, playerSeatingMap)).thenReturn(p1);
//
//        // init round
//        ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
//        FutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator();
//        round = new BettingRound(game, 0, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
//        round.playerToAct = p0Id;
//
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1), argThat(new IsListOfNElements(2)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p2), argThat(new IsListOfNElements(2)));
//
//        // player checks
//        PokerAction action = mock(PokerAction.class);
//        when(action.getActionType()).thenReturn(PokerActionType.CHECK);
//        when(action.getPlayerId()).thenReturn(p0Id);
//
//        ActionRequest actionRequest = mock(ActionRequest.class);
//        when(actionRequest.isOptionEnabled(PokerActionType.CHECK)).thenReturn(true);
//        when(actionRequest.matches(action)).thenReturn(true);
//
//        when(p0.getActionRequest()).thenReturn(actionRequest);
//        when(actionRequest.getPlayerId()).thenReturn(p1Id);
//
//        round.act(action);
//
//        // next player gets empty list the others get check and fold
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0), argThat(new IsListOfNElements(2)));
//        verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1), argThat(new IsListOfNElements(0)));
//        verify(serverAdapter, times(2)).notifyFutureAllowedActions(Mockito.eq(p2), argThat(new IsListOfNElements(2)));
//
//    }
//
//    // HELPERS
//
//    private void act(MockPlayer player, PokerActionType action, long amount) {
//        PokerAction a = new PokerAction(player.getId(), action);
//        a.setBetAmount(amount);
//        round.act(a);
//    }
//
//    private void verifyAndAct(MockPlayer player, PokerActionType action, long amount) {
//        assertTrue("Tried to " + action + " but available actions were: "
//                + player.getActionRequest().getOptions(), player
//                .getActionRequest().isOptionEnabled(action));
//        assertTrue(requestedAction.isOptionEnabled(action));
//        assertEquals(player.getId(), requestedAction.getPlayerId());
//        act(player, action, amount);
//    }
//
//    public void notifyActionRequested(ActionRequest r) {
//        this.requestedAction = r;
//    }
//
//    @SuppressWarnings("rawtypes")
//    class IsListOfNElements extends ArgumentMatcher<List> {
//        private final int n;
//
//        public IsListOfNElements(int n) {
//            this.n = n;
//
//        }
//
//        public boolean matches(Object list) {
//            return ((List) list).size() == n;
//        }
//
//    }

}
