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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.MockGame;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.TestListener;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.variant.FutureActionsCalculator;
import com.cubeia.poker.variant.telesina.Telesina;

public class BettingRoundTest extends TestCase implements TestListener {

	private ActionRequest requestedAction;

	private MockGame game;

	private BettingRound round;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		game = new MockGame();
		game.listeners.add(this);
		
	}

    @Test
	public void testHeadsUpBetting() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
		verifyAndAct(p[0], PokerActionType.FOLD, 100);

		assertTrue(round.isFinished());
	}

    @Test
	public void testCallAmount() {
		MockPlayer[] p = TestUtils.createMockPlayers(2, 100);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);
		act(p[1], PokerActionType.BET, 70);
		
		PossibleAction bet = requestedAction.getOption(PokerActionType.CALL);
		assertEquals(70, bet.getMaxAmount());
		
		
	}	
	
	@Test
    public void testCallTellsState() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state );
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        
        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        round.call(player);
        
        verify(state).call();
        
    }   
	
    @Test
    public void testCall() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state);
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        long betStack = 75L;
        when(player.getBetStack()).thenReturn(betStack);
        when(player.getBalance()).thenReturn(betStack * 10);
        
        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        round.highBet = 100;
        
        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        long amountCalled = round.call(player);
        
        verify(state).call();
        assertThat(amountCalled, is(round.highBet - betStack));

    }
    
    @Test
    public void testCallNotifiesBetStackUpdates() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state);
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        
        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        round.call(player);
                
        verify(state).notifyBetStacksUpdated();
    }
    
    @Test 
    public void testHandleActionOnCallSetsAmountOnResponse() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state);

        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        
        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
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

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
			
		assertTrue(requestedAction.isOptionEnabled(PokerActionType.RAISE));
		verifyAndAct(p[0], PokerActionType.RAISE, 200);
		
		
	}
    
    @Test
    public void testRaiseNotifiesBetStackUpdates() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state);
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        
        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        round.raise(player, 10L);
                
        verify(state).notifyBetStacksUpdated();
    }
	
    @Test
	public void testNoRaiseAllowedWhenAllOtherPlayersAreAllIn() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		actMax(PokerActionType.BET);
		assertFalse(requestedAction.isOptionEnabled(PokerActionType.RAISE));
	}
    
    
    private MockPlayer[] createAndGetPlayersAddThemToTheGameAndCreateABettingRound(int numberOfPlayers){
		MockPlayer[] p = TestUtils.createMockPlayers(numberOfPlayers);
		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
		return p;
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
    public void testRaiseAnAllInBetSetsLastCallerToAllInPlayer(){
    	MockPlayer[] p = createAndGetPlayersAddThemToTheGameAndCreateABettingRound(2);
    	act(p[1], PokerActionType.BET, 100);
    	act(p[0], PokerActionType.RAISE, 200);
    	PokerPlayer player = p[1];
		assertThat(round.getLastPlayerToBeCalled(), CoreMatchers.is(player));
    }
    
    @Test
    public void testBetNotifiesBetStackUpdates() {
        GameType game = mock(GameType.class);
        IPokerState state = mock(IPokerState.class);
        when(game.getState()).thenReturn(state);
        PokerPlayer player = Mockito.mock(PokerPlayer.class);
        
        BettingRound round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());
        
        ActionRequest actionRequest = Mockito.mock(ActionRequest.class);
        when(player.getActionRequest()).thenReturn(actionRequest);
        PossibleAction possibleAction = Mockito.mock(PossibleAction.class);
        when(actionRequest.getOption(PokerActionType.BET)).thenReturn(possibleAction);
        when(possibleAction.getMinAmount()).thenReturn(5L);
        
        round.bet(player, 10L);
                
        verify(state).notifyBetStacksUpdated();
    }
	
	private void actMax(PokerActionType action) {
		PossibleAction option = requestedAction.getOption(action);
		PokerAction a = new PokerAction(requestedAction.getPlayerId(), action, option.getMaxAmount());
		round.act(a);		
	}

    @Test
	public void testTimeoutTwice() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		round.timeout();
		round.timeout();

		assertTrue(round.isFinished());
	}	
	
    @Test
	public void testTimeout() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 0, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		verifyAndAct(p[1], PokerActionType.BET, 100);
		round.timeout();
		assertTrue(round.isFinished());
	}	

    @Test
	public void testDealerLeft() {
		MockPlayer[] p = TestUtils.createMockPlayers(2);

		game.addPlayers(p);
		round = new BettingRound(game, 3, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()), new FutureActionsCalculator());

		assertFalse(game.roundFinished);

		round.timeout();
		round.timeout();

		assertTrue(round.isFinished());		
	}
    
    @SuppressWarnings("unchecked")
	@Test
    public void testFutureActionsNotified() {
    	int p0Id = 1337;
    	PokerPlayer p0 = mock(PokerPlayer.class);
		when(p0.getId()).thenReturn(p0Id);
		
		int p1Id = 1338;
    	PokerPlayer p1 = mock(PokerPlayer.class);
    	when(p1.getId()).thenReturn(p1Id);

    	int p2Id = 1339;
    	PokerPlayer p2 = mock(PokerPlayer.class);
    	when(p2.getId()).thenReturn(p2Id);
    	
    	IPokerState state = mock(IPokerState.class);
		GameType game = mock(Telesina.class);
		when(game.getState()).thenReturn(state);
		ServerAdapter serverAdapter = mock(ServerAdapter.class);
		when(game.getServerAdapter()).thenReturn(serverAdapter);
		
		SortedMap<Integer, PokerPlayer> playerSeatingMap = new TreeMap<Integer, PokerPlayer>();
		playerSeatingMap.put(0,p0);
		playerSeatingMap.put(1,p1);
		playerSeatingMap.put(2,p2);
		when(state.getCurrentHandSeatingMap()).thenReturn(playerSeatingMap);
		
		SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
		playerMap.put(p0.getId(),p0);
		playerMap.put(p1.getId(),p1);
		playerMap.put(p2.getId(),p2);
		when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
		
		PlayerToActCalculator playerToActCalculator = mock(PlayerToActCalculator.class);
		
		ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
		FutureActionsCalculator futureActionsCalculator = new FutureActionsCalculator();
		round = new BettingRound(game, 0, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
		
		verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p0),Mockito.anyList());
		verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p1),Mockito.anyList());
		verify(serverAdapter).notifyFutureAllowedActions(Mockito.eq(p2),Mockito.anyList());
		
    }

	// HELPERS
	
	private void act(MockPlayer player, PokerActionType action, long amount) {
		PokerAction a = new PokerAction(player.getId(), action);
		a.setBetAmount(amount);
		round.act(a);
	}	

	private void verifyAndAct(MockPlayer player, PokerActionType action, long amount) {
		assertTrue("Tried to " + action + " but available actions were: "
				+ player.getActionRequest().getOptions(), player
				.getActionRequest().isOptionEnabled(action));
		assertTrue(requestedAction.isOptionEnabled(action));
		assertEquals(player.getId(), requestedAction.getPlayerId());
		act(player, action, amount);
	}

	public void notifyActionRequested(ActionRequest r) {
		this.requestedAction = r;
	}

}
