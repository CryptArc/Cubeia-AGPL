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

import com.cubeia.poker.player.PokerPlayer;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoLimitBetStrategyTest extends TestCase {

    private NoLimitBetStrategy strategy;

    private BettingRoundContext context;

    private PokerPlayer player;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = mock(BettingRoundContext.class);
        player = mock(PokerPlayer.class);
        strategy = new NoLimitBetStrategy();
    }

    public void testGetMinBetAmount() {
        // Player has 100 chips and min bet is 50. Result should be 50.
        when(player.getBalance()).thenReturn(100L);
        when(context.getMinBet()).thenReturn(50L);
        assertEquals(50, strategy.getMinBetAmount(context, player));
    }

    public void testGetMaxBetAmount() {
        // Player has 100 chips and min bet is 50. Result should be 100.
        when(player.getBalance()).thenReturn(100L);
        when(context.getMinBet()).thenReturn(50L);
        assertEquals(100, strategy.getMaxBetAmount(context, player));
    }

    public void testGetMinBetAmountWhenPlayerHasLessThanMinBet() {
        // Player has 50 chips and min bet is 100. Result should be 50.
        when(player.getBalance()).thenReturn(50L);
        when(context.getMinBet()).thenReturn(100L);
        assertEquals(50, strategy.getMinBetAmount(context, player));
    }

    public void testGetMaxBetAmountWhenPlayerHasLessThanMinBet() {
        // Player has 50 chips and min bet is 100. Result should be 50.
        when(player.getBalance()).thenReturn(50L);
        when(context.getMinBet()).thenReturn(100L);
        assertEquals(50, strategy.getMaxBetAmount(context, player));
    }

    public void testGetCallAmount() {
        // Player has bet 0 so far. Current high bet is 50 and player has 100. Result should be 50.
        when(player.getBetStack()).thenReturn(0L);
        when(player.getBalance()).thenReturn(100L);
        when(context.getHighestBet()).thenReturn(50L);
        assertEquals(50, strategy.getCallAmount(context, player));
    }

    public void testGetCallAmountWhenPlayerHasBeenRaised() {
        // Player has bet 50 so far. Current high bet is 200 and player has 300. Result should be 150.
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(300L);
        when(context.getHighestBet()).thenReturn(200L);
        assertEquals(150, strategy.getCallAmount(context, player));
    }

    public void testGetCallAmountWhenPlayerHasBeenRaisedAndDoesNotHaveEnoughChips() {
        // Player has bet 50 so far. Current high bet is 200 and player has 100. Result should be 100.
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(100L);
        when(context.getHighestBet()).thenReturn(200L);
        assertEquals(100, strategy.getCallAmount(context, player));
    }

    public void testGetCallAmountWhenPlayerHasAlreadyBetEnough() {
        // Player has bet 50 so far. Current high bet is 50. The question is illegal (player should never be asked to call).
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(300L);
        when(context.getHighestBet()).thenReturn(50L);
        try {
            strategy.getCallAmount(context, player);
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testGetCallAmountWhenPlayerHasAlreadyBetMoreThanHighBet() {
        // Player has bet 50 so far. Current high bet is 40. The question is illegal (player should never be asked to call).
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(300L);
        when(context.getHighestBet()).thenReturn(40L);
        try {
            strategy.getCallAmount(context, player);
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testGetNormalMinRaiseAmount() {
        // Player has bet 0 so far. The current (and only) bet is 100. Min raise is 100, to 200.
        when(player.getBetStack()).thenReturn(0L);
        when(player.getBalance()).thenReturn(500L);
        when(context.getHighestBet()).thenReturn(100L);
        when(context.getSizeOfLastBetOrRaise()).thenReturn(100L);
        when(context.getNextValidRaiseLevel()).thenReturn(200L);
        assertEquals(200, strategy.getMinRaiseToAmount(context, player));
        assertAllInRaiseCorrect();
    }

    public void testGetReRaiseAmount() {
        // Player has bet 0 so far. The current high bet is 100 and the last raise amount was 30. Min raise is 30, to 130.
        when(player.getBetStack()).thenReturn(0L);
        when(player.getBalance()).thenReturn(500L);
        when(context.getHighestBet()).thenReturn(100L);
        when(context.getSizeOfLastBetOrRaise()).thenReturn(30L);
        when(context.getNextValidRaiseLevel()).thenReturn(130L);
        assertEquals(130, strategy.getMinRaiseToAmount(context, player));
        assertAllInRaiseCorrect();
    }

    public void testGetReRaiseAmountWhenPlayerHasAlreadyBet() {
        // Player has bet 50 so far. The current high bet is 150 and the last raise amount was 100. Min raise is 100, to 250.
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(500L);
        when(context.getHighestBet()).thenReturn(150L);
        when(context.getSizeOfLastBetOrRaise()).thenReturn(100L);
        when(context.getNextValidRaiseLevel()).thenReturn(250L);
        assertEquals(250, strategy.getMinRaiseToAmount(context, player));
        assertAllInRaiseCorrect();
    }

    private void assertAllInRaiseCorrect() {
        assertEquals(player.getBalance() + player.getBetStack(), strategy.getMaxRaiseToAmount(context, player));
    }

    public void testGetReRaiseAmountWhenPlayerDoesNotHaveEnoughChips() {
        // Player has bet 50 so far. The current high bet is 150 and the last raise amount was 100. Min raise is 100, to 250.
        // Player only has 190 chips left.
        when(player.getBetStack()).thenReturn(50L);
        when(player.getBalance()).thenReturn(190L);
        when(context.getHighestBet()).thenReturn(150L);
        when(context.getSizeOfLastBetOrRaise()).thenReturn(100L);
        when(context.getNextValidRaiseLevel()).thenReturn(250L);
        assertEquals(240, strategy.getMinRaiseToAmount(context, player));
        assertEquals(240, strategy.getMaxRaiseToAmount(context, player));
    }

    public void testGetRaiseAmountWhenAllOtherPlayersAreAllIn() {
        // Current high bet is 100 but all other players are all in. Min raise and max raise are 0.
        when(player.getBalance()).thenReturn(500L);
        when(context.getHighestBet()).thenReturn(100L);
        when(context.getSizeOfLastBetOrRaise()).thenReturn(100L);
        when(context.allOtherNonFoldedPlayersAreAllIn(player)).thenReturn(true);
        assertEquals(0, strategy.getMinRaiseToAmount(context, player));
        assertEquals(0, strategy.getMaxRaiseToAmount(context, player));
    }

    public void testGetRaiseWhenPlayerHasBetMoreThanCurrentMax() {
        // Throw exception if the current high bet is lower than the player's bet stack.
        when(player.getBetStack()).thenReturn(300L);
        when(player.getBalance()).thenReturn(0L);
        when(context.getHighestBet()).thenReturn(100L);
        try {
            strategy.getMinRaiseToAmount(context, player);
            fail();
        } catch (IllegalStateException expected) {
        }
    }

}
