package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.Rank.ACE;
import static com.cubeia.poker.hand.Rank.EIGHT;
import static com.cubeia.poker.hand.Rank.FIVE;
import static com.cubeia.poker.hand.Rank.FOUR;
import static com.cubeia.poker.hand.Rank.JACK;
import static com.cubeia.poker.hand.Rank.KING;
import static com.cubeia.poker.hand.Rank.NINE;
import static com.cubeia.poker.hand.Rank.QUEEN;
import static com.cubeia.poker.hand.Rank.SEVEN;
import static com.cubeia.poker.hand.Rank.SIX;
import static com.cubeia.poker.hand.Rank.TEN;
import static com.cubeia.poker.hand.Rank.THREE;
import static com.cubeia.poker.hand.Rank.TWO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RankTest {

    @Test
    public void testOrder() {
        assertThat(Rank.values(), is(new Rank[] {
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
