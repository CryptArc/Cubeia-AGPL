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

package com.cubeia.backend.cashgame.dto;

import org.junit.Test;

import com.cubeia.games.poker.common.Money;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MoneyTest {

    @Test
    public void testCreation() {
        Money money = new Money(1234, "SEK", 2);
        assertThat(money.getAmount(), is(1234L));
        assertThat(money.getCurrencyCode(), is("SEK"));
        assertThat(money.getFractionalDigits(), is(2));
    }

    @Test
    public void testAdd() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-1000, "SEK", 2);
        Money m3 = new Money(1234, "SEK", 2);
        assertThat(m1.add(m2), is(new Money(0, "SEK", 2)));
        assertThat(m1.add(m3), is(new Money(2234, "SEK", 2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFailOnIncompatibleCurrencies() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-1000, "EUR", 2);
        m1.add(m2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFailOnIncompatibleFractionalDigits() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-1000, "SEK", 3);
        m1.add(m2);
    }

    @Test
    public void testNegate() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-123, "EUR", 3);
        assertThat(m1.negate(), is(new Money(-1000, "SEK", 2)));
        assertThat(m2.negate(), is(new Money(123, "EUR", 3)));
    }

    @Test
    public void testSubtract() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-123, "SEK", 2);
        assertThat(m1.subtract(m2), is(new Money(1123, "SEK", 2)));
        assertThat(m2.subtract(m1), is(new Money(-1123, "SEK", 2)));
        assertThat(m1.subtract(m1), is(new Money(0, "SEK", 2)));
    }

    @Test
    public void testAddScalar() {
        Money m1 = new Money(1000, "SEK", 2);
        Money m2 = new Money(-1000, "EUR", 3);
        assertThat(m1.add(1234), is(new Money(2234, "SEK", 2)));
        assertThat(m2.add(1000), is(new Money(0, "EUR", 3)));
    }

    @Test
    public void testToString() {
        Money money = new Money(1234, "SEK", 2);
        assertThat(money.toString(), is("12.34 SEK"));
    }

}
