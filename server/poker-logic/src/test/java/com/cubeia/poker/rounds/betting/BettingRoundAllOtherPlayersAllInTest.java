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
import static org.mockito.Mockito.when;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.player.PokerPlayer;

public class BettingRoundAllOtherPlayersAllInTest {

    @Mock private GameType telesina;
    @Mock private PokerState state;
    @Mock private PlayerToActCalculator playerToActCalculator;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private PokerPlayer player3;
	private BettingRound round;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	        
	    when(telesina.getState()).thenReturn(state);
	    SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
	    
        round = new BettingRound(telesina, 0, playerToActCalculator, new ActionRequestFactory(new NoLimitBetStrategy()));
	}

    @Test
	public void noPlayerAllIn() {
        when(player1.isAllIn()).thenReturn(false);
        when(player2.isAllIn()).thenReturn(false);
        when(player3.isAllIn()).thenReturn(false);
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player1), is(false));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player2), is(false));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player3), is(false));
	}
    
    @Test
    public void somePlayersAllIn() {
        when(player1.isAllIn()).thenReturn(true);
        when(player2.isAllIn()).thenReturn(true);
        when(player3.isAllIn()).thenReturn(false);
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player1), is(false));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player2), is(false));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player3), is(true));
    }

    @Test
    public void allPlayersAllIn() {
        when(player1.isAllIn()).thenReturn(true);
        when(player2.isAllIn()).thenReturn(true);
        when(player3.isAllIn()).thenReturn(true);
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player1), is(true));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player2), is(true));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player3), is(true));
    }
    
    @Test
    public void headsUpWhenOtherIsSitOutAndNotAllIn() {
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
        when(player1.isAllIn()).thenReturn(false);
        when(player2.isAllIn()).thenReturn(false);
        when(player2.isSittingOut()).thenReturn(true);
        
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player1), is(false));
        assertThat(round.allOtherNonFoldedPlayersAreAllIn(player2), is(false));
    }
    
}
