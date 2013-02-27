/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FixedLimitBetStrategyTest {

    private FixedLimitBetStrategy strategy;

    @Mock
    private BettingRoundContext context;

    @Mock
    private PokerPlayer player;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new FixedLimitBetStrategy(10, false);
    }

    /**
     * Example 1: Player A bets $10, player B calls and player C raises to $20. Min raise-to for player A is now $30, meaning
     * he can pay $20 to raise the bet by $10, from $20 to $30.
     *
     *
     * If the player cannot afford to raise, 0 should be returned.
     *
     */
    @Test
    public void testGetMinRaiseToAmountExample1() {
        when(player.getBalance()).thenReturn(100L);
        when(context.getHighestBet()).thenReturn(20L);
        when(context.getHighestCompleteBet()).thenReturn(20L);

        assertThat(strategy.getMinRaiseToAmount(context, player), is(30L));
    }

    /**
     * Example 2: Player A bets $10, player B calls and player C goes all-in to $14 (which is an incomplete bet both in no-limit and fixed limit).
     * Min raise-to for player A is now $20, because C's all-in counts as a call.
     */
    @Test
    public void testGetMinRaiseToAmountExample2() {
        when(player.getBalance()).thenReturn(100L);
        when(context.getHighestBet()).thenReturn(10L);
        when(context.getHighestCompleteBet()).thenReturn(10L);

        assertThat(strategy.getMinRaiseToAmount(context, player), is(20L));
    }

    /**
     * If the player cannot afford to raise, 0 should be returned.
     *
     */
    @Test
    public void testGetMinRaiseToAmountExample3() {
        when(player.getBalance()).thenReturn(9L);
        when(context.getHighestBet()).thenReturn(20L);
        when(context.getHighestCompleteBet()).thenReturn(20L);

        assertThat(strategy.getMinRaiseToAmount(context, player), is(0L));
    }

    /**
     * If the player cannot afford a full raise, return the highest possible raise.
     *
     */
    @Test
    public void testGetMinRaiseToAmountExample4() {
        when(player.getBetStack()).thenReturn(10L);
        when(player.getBalance()).thenReturn(12L);
        when(context.getHighestBet()).thenReturn(20L);
        when(context.getHighestCompleteBet()).thenReturn(20L);

        assertThat(strategy.getMinRaiseToAmount(context, player), is(22L));
    }

    @Test
    public void testGetMaxRaiseToAmount() {
        when(player.getBetStack()).thenReturn(10L);
        when(player.getBalance()).thenReturn(12L);
        when(context.getHighestBet()).thenReturn(20L);
        when(context.getHighestCompleteBet()).thenReturn(20L);

        assertThat(strategy.getMaxRaiseToAmount(context, player), is(22L));
    }

    /**
     * Gets the minimum allowed bet amount.
     *
     */
    @Test
    public void testGetMinBetAmount() {
        when(player.getBalance()).thenReturn(12L);
        when(context.getHighestCompleteBet()).thenReturn(0L);

        assertThat(strategy.getMinBetAmount(context, player), is(10L));
    }

    /**
     * If the player does not have enough money for a min bet,
     * the player's balance should be returned.
     */
    @Test
    public void testGetMinBetAmountWhenNotEnoughForCompleteBet() {
        when(player.getBalance()).thenReturn(8L);
        when(context.getHighestCompleteBet()).thenReturn(0L);

        assertThat(strategy.getMinBetAmount(context, player), is(8L));
    }

    /**
     * Gets the maximum allowed bet amount.
     *
     */
    @Test
    public void testGetMaxBetAmount() {
        when(player.getBalance()).thenReturn(8L);
        when(context.getHighestCompleteBet()).thenReturn(0L);

        assertThat(strategy.getMaxBetAmount(context, player), is(8L));
    }

    /**
     * Gets the amount need for the given player to call. If the player does not have enough money for a call,
     * the player's balance should be returned.
     *
     */
    @Test
    public void testGetCallAmount() {
        when(player.getBalance()).thenReturn(100L);
        when(player.getBetStack()).thenReturn(10L);
        when(context.getHighestBet()).thenReturn(20L);

        assertThat(strategy.getCallAmount(context, player), is(10L));
    }

    @Test
    public void testGetCallAmountWhenThereIsAnIncompleteBet() {
        when(player.getBalance()).thenReturn(100L);
        when(player.getBetStack()).thenReturn(10L);
        when(context.getHighestBet()).thenReturn(14L);

        assertThat(strategy.getCallAmount(context, player), is(4L));
    }

    @Test
    public void testGetCallAmountWhenPlayerCannotAffordToCall() {
        when(player.getBetStack()).thenReturn(10L);
        when(player.getBalance()).thenReturn(6L);
        when(context.getHighestBet()).thenReturn(20L);

        assertThat(strategy.getCallAmount(context, player), is(6L));
    }

    /**
     * Fixed limit examples:
     * 1. A bets $10, B raises to $20. Next valid raise level = $30
     *
     */
    @Test
    public void testGetNextValidRaiseToLevelExample1() {
        when(context.getHighestCompleteBet()).thenReturn(20L);
        assertThat(strategy.getNextValidRaiseToLevel(context), is(30L));
    }

    /**
     * Fixed limit examples:
     * 2. A bets $10, B goes all-in for $12, next valid raise level = $20. (Because $12 is not a complete bet)
     *
     */
    @Test
    public void testGetNextValidRaiseToLevelExample2() {
        when(context.getHighestCompleteBet()).thenReturn(10L);
        when(context.getHighestBet()).thenReturn(12L);
        assertThat(strategy.getNextValidRaiseToLevel(context), is(20L));
    }

    /**
     * Fixed limit examples:
     * 3. A bets $10, A goes all-in for $15 (which is a complete bet). Next valid raise level = $30.
     *
     */
    @Test
    public void testGetNextValidRaiseToLevelExample3() {
        when(context.getHighestCompleteBet()).thenReturn(20L);
        when(context.getHighestBet()).thenReturn(15L);
        assertThat(strategy.getNextValidRaiseToLevel(context), is(30L));
    }

    /**
     * Fixed limit:
     * If the current bet level is $10 and the next level is $20, then a raise to $15 or more is considered complete, anything below is incomplete.
     *
     */
    @Test
    public void testIsCompleteBetOrRaise() throws Exception {
        when(context.getHighestCompleteBet()).thenReturn(10L);

        assertThat(strategy.isCompleteBetOrRaise(context, 14), is(false));
        assertThat(strategy.isCompleteBetOrRaise(context, 15), is(true));
    }

    @Test
    public void testShouldBettingBeCapped() {
        assertThat(strategy.shouldBettingBeCapped(3, false), is(false));
        assertThat(strategy.shouldBettingBeCapped(4, false), is(true)); // We reached max bets, betting is capped.

        assertThat(strategy.shouldBettingBeCapped(3, true), is(false));
        assertThat(strategy.shouldBettingBeCapped(4, true), is(false)); // We are heads up, betting should not be capped.
        assertThat(strategy.shouldBettingBeCapped(4, true), is(false)); // We are heads up, betting should not be capped.
    }

}
