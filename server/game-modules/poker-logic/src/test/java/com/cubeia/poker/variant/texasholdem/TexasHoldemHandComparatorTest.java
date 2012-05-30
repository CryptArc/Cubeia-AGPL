package com.cubeia.poker.variant.texasholdem;

import com.cubeia.poker.hand.Hand;
import org.junit.Before;
import org.junit.Test;

import static com.cubeia.poker.util.TestHelpers.isLessThan;
import static org.junit.Assert.assertThat;

public class TexasHoldemHandComparatorTest {

    private TexasHoldemHandComparator comparator;

    @Before
    public void setup() {
        comparator = new TexasHoldemHandComparator();
    }

    @Test
    public void testTwoFlushesDifferentSecondHighCard() {
        Hand winner = new Hand("6C 8C 6D 9C AC 5C");
        Hand loser = new Hand("3C 8C 6D 9C AC 5C");

        // Should really be >= 1, but someone botched the compare semantics.
        assertThat(comparator.compare(winner, loser), isLessThan(0));
    }

}
