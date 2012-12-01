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

package com.cubeia.games.poker.tournament.payouts;

import com.cubeia.games.poker.tournament.configuration.payouts.IntRange;
import com.cubeia.games.poker.tournament.configuration.payouts.Payout;
import com.cubeia.games.poker.tournament.configuration.payouts.Payouts;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PayoutHandlerTest {

    private PayoutHandler payoutHandler;

    @Before
    public void setup() {
        // Total prize pool is $200.
        Payouts payouts = new Payouts(new IntRange(20, 25), list(45.45, 22.73, 15.15, 9.09, 7.58, 6.12), 20000);
        payoutHandler = new PayoutHandler(payouts);
    }

    @Test
    public void testSimpleCase() {
        // Simple case, player finishes in place 4.
        // 9.09% of $200 is $18.18
        Map<Integer, Long> balanceAtStart = of(7, 70L);
        Map<Integer,Long> playerIdToCentsWon = payoutHandler.calculatePayouts(ImmutableSet.<Integer>of(7), balanceAtStart, 4);
        assertThat(playerIdToCentsWon.get(7), is(1818L));
    }

    @Test
    public void testTwoPlayersOut() {
        // Two players are out and they had different amounts of chips when the hand started.
        // 15.15% of $200 is $30.30
        Map<Integer, Long> balanceAtStart = of(7, 70L, 8, 80L);
        Map<Integer,Long> playerIdToCentsWon = payoutHandler.calculatePayouts(ImmutableSet.<Integer>of(7, 8), balanceAtStart, 4);
        assertThat(playerIdToCentsWon.get(7), is(1818L));
        assertThat(playerIdToCentsWon.get(8), is(3030L));
    }

    @Test
    public void testThreePlayersOutWithSameStartChips() {
        // Two players are out and they had different amounts of chips when the hand started.
        // 7.58% of $200 is $15.16. 15.16 + 18.18 + 30.30 = 63.64
        // 63.64 / 3 = 21.21
        // 21.21 * 3 = 63.63 => 1 cent remainder
        Map<Integer, Long> balanceAtStart = of(7, 70L, 8, 70L, 9, 70L);
        Map<Integer, Long> playerIdToCentsWon = payoutHandler.calculatePayouts(ImmutableSet.<Integer>of(7, 8, 9), balanceAtStart, 5);

        assertThat(playerIdToCentsWon.get(7), is(2121L));
        assertThat(playerIdToCentsWon.get(8), is(2122L));
        assertThat(playerIdToCentsWon.get(9), is(2121L));
    }

    @Test
    public void testFourPlayersTwoOfWhichHadTheSameStartingChips() {
        // Two players are out and they had different amounts of chips when the hand started.
        // 15.16 + 18.18 = 33.34
        // 33.34 / 2 = 16.67
        // 6.12% of $200 = 12.24
        Map<Integer, Long> balanceAtStart = of(7, 70L, 8, 70L, 9, 80L, 10, 20L);
        Map<Integer, Long> playerIdToCentsWon = payoutHandler.calculatePayouts(ImmutableSet.<Integer>of(7, 8, 10), balanceAtStart, 6);

        assertThat(playerIdToCentsWon.get(7), is(1667L));
        assertThat(playerIdToCentsWon.get(8), is(1667L));
        assertThat(playerIdToCentsWon.get(9), is(3030L));
        assertThat(playerIdToCentsWon.get(10), is(1224L));
    }

    private List<Payout> list(double ... percentages) {
        List<Payout> payouts = newArrayList();
        int position = 1;
        for (double percentage : percentages) {
            payouts.add(new Payout(new IntRange(position, position), BigDecimal.valueOf(percentage)));
            position++;
        }
        return payouts;
    }

}
