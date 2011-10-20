package com.cubeia.poker.hand;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class IndexCardIdGeneratorTest {

    @Test
    public void test() {
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(34, Rank.NINE, Suit.SPADES);

        List<Card> idCards = new IndexCardIdGenerator().copyAndAssignIds(Arrays.asList(card1, card2));
        assertThat(idCards.size(), is(2));
        assertThat(idCards.get(0), is(new Card(0, Rank.ACE, Suit.HEARTS)));
        assertThat(idCards.get(1), is(new Card(1, Rank.NINE, Suit.SPADES)));
    }

}
