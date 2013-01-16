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

package com.cubeia.games.poker.tournament.configuration.payouts;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class PayoutsTest {

    private Payouts payouts;

    private Payouts sitAndGoPayouts;
    private Payouts payoutsWithRange;

    @Before
    public void setup() {
        PayoutStructure structure = PayoutStructureParserTest.createTestStructure();
        this.payouts = structure.getPayoutsForEntrantsAndPrizePool(56, 20000);
        this.sitAndGoPayouts = structure.getPayoutsForEntrantsAndPrizePool(10, 1000);
        this.payoutsWithRange = structure.getPayoutsForEntrantsAndPrizePool(235, 500000);
    }

    @Test
    public void testPlayerNotInTheMoneyGetsZero() {
        assertThat(payouts.getPayoutsForPosition(56), is(0));
    }

    @Test
    public void testBubbleGetsBubble() {
        assertThat(sitAndGoPayouts.getPayoutList().size(), is(3));
        assertThat(sitAndGoPayouts.getPayoutsForPosition(3), not(0));
        assertThat(sitAndGoPayouts.getPayoutsForPosition(4), is(0));
    }

    @Test
    public void testRangedPayouts() {
        // 0.72% of $5000 = 36
        assertThat(payoutsWithRange.getPayoutsForPosition(36), is(3600));
    }

}
