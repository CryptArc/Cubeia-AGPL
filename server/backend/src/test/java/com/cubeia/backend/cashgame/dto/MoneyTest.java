package com.cubeia.backend.cashgame.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MoneyTest {

    @Test
    public void testCreation() {
        Money money = new Money(1234, "SEK", 2);
        assertThat(money.getAmount(), is(1234L));
        assertThat(money.getCurrencyCode(), is("SEK"));
        assertThat(money.getFractionalDigits(), is(2));
    }
    
    @Test
    public void testToString() {
        Money money = new Money(1234, "SEK", 2);
        assertThat(money.toString(), is("12.34 SEK"));
    }

}
