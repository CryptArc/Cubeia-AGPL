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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;

public class BettingRoundTimeoutTest {

    @Mock private GameType telesina;
    @Mock private PokerState state;
    @Mock private PlayerToActCalculator playerToActCalculator;
    @Mock private PokerPlayer player;
    @Mock private ActionRequest actionRequest;
    @Mock private ServerAdapter serverAdapter;
	private BettingRound round;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    when(telesina.getState()).thenReturn(state);
	    when(telesina.getServerAdapter()).thenReturn(serverAdapter);
        round = new BettingRound(telesina, 0, playerToActCalculator, new ActionRequestFactory(new NoLimitBetStrategy()));
	}

    @Test
	public void testMakeDefaultActionAndThenSitOutOnTimeout() {
        int playerId = 1334;
        round.playerToAct = playerId;
        when(state.getPlayerInCurrentHand(playerId)).thenReturn(player);
        when(player.getId()).thenReturn(playerId);
        when(actionRequest.matches(Mockito.any(PokerAction.class))).thenReturn(true);
        when(player.getActionRequest()).thenReturn(actionRequest);
        
        round.timeout();
        
        verify(state).playerIsSittingOut(playerId, SitOutStatus.TIMEOUT);
        verify(serverAdapter).notifyActionPerformed(Mockito.any(PokerAction.class), Mockito.eq(player));
        verify(serverAdapter).notifyPlayerBalance(player);
        verify(player).setHasFolded(true);

	}
    
}
