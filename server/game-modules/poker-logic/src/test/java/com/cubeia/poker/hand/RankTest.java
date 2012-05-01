package com.cubeia.poker.hand;

import org.junit.Test;

import static com.cubeia.poker.hand.Rank.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RankTest {

    @Test
    public void testOrder() {
        assertThat(Rank.values(), is(new Rank[]{
                TWO,
                THREE,
                FOUR,
                FIVE,
                SIX,
                SEVEN,
                EIGHT,
                NINE,
                TEN,
                JACK,
                QUEEN,
                KING,
                ACE
        }));
    }
}
