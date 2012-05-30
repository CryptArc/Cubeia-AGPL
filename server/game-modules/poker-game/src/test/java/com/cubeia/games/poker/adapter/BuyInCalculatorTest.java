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

package com.cubeia.games.poker.adapter;

import com.cubeia.games.poker.adapter.BuyInCalculator.MinAndMaxBuyInResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BuyInCalculatorTest {

    @Test
    public void testCalculateBelowMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();

        MinAndMaxBuyInResult result;

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 0);
        assertThat(result.getMinBuyIn(), is(tableMinBuyIn));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn));
        assertThat(result.isBuyInPossible(), is(true));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 70);
        assertThat(result.getMinBuyIn(), is(tableMinBuyIn - 70));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 70));
        assertThat(result.isBuyInPossible(), is(true));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 99);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 99));
        assertThat(result.isBuyInPossible(), is(true));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMinBuyIn);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - tableMinBuyIn));
        assertThat(result.isBuyInPossible(), is(true));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 5000);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 5000));
        assertThat(result.isBuyInPossible(), is(true));
    }

    @Test
    public void testCalculateBalanceNearMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();

        MinAndMaxBuyInResult result;

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn - anteLevel);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(anteLevel));
        assertThat(result.isBuyInPossible(), is(true));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn - anteLevel / 2);
        assertThat(result.getMinBuyIn(), is(anteLevel / 2));
        assertThat(result.getMaxBuyIn(), is(anteLevel / 2));
        assertThat(result.isBuyInPossible(), is(true));

    }

    @Test
    public void testCalculateBalanceAboveMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();

        MinAndMaxBuyInResult result;

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn);
        assertThat(result.getMinBuyIn(), is(0));
        assertThat(result.getMaxBuyIn(), is(0));
        assertThat(result.isBuyInPossible(), is(false));

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn + 1);
        assertThat(result.getMinBuyIn(), is(0));
        assertThat(result.getMaxBuyIn(), is(0));
        assertThat(result.isBuyInPossible(), is(false));
    }

    @Test
    public void testCalculateReserveAmount() {
        int tableMaxBuyIn = 20000;
        BuyInCalculator blc = new BuyInCalculator();

        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 5000, 20000), is(20000 - 5000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 5000, 2000), is(2000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 0, 20000), is(20000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 20000, 20000), is(0));
    }

}
