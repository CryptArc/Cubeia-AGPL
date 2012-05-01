package com.cubeia.poker.hand;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


public class CardTest {

    @Test
    public void testToString() throws Exception {
        Card card = new Card(Rank.ACE, Suit.SPADES);
        Assert.assertEquals("AS", card.toString());

        card = new Card(Rank.FIVE, Suit.CLUBS);
        Assert.assertEquals("5C", card.toString());
    }

    @Test
    public void testFromString() throws Exception {
        Card card = new Card("AS");
        Assert.assertEquals(Rank.ACE, card.getRank());
        Assert.assertEquals(Suit.SPADES, card.getSuit());

        card = new Card("5c");
        Assert.assertEquals(Rank.FIVE, card.getRank());
        Assert.assertEquals(Suit.CLUBS, card.getSuit());
    }

    @Test
    public void testEquals() throws Exception {
        Card card1 = new Card(Rank.ACE, Suit.SPADES);
        Card card2 = new Card(Rank.ACE, Suit.SPADES);
        Assert.assertEquals(card1, card2);
    }

    @Test
    public void testMakeCopyWithId() {
        Card card = new Card(Rank.ACE, Suit.SPADES);
        Card card2 = card.makeCopyWithId(434);
        assertThat(card2.getId(), is(434));
        assertThat(card2.getSuit(), is(card.getSuit()));
        assertThat(card2.getRank(), is(card.getRank()));
    }

    @Test
    public void testMakeCopyWithoutId() {
        Card card = new Card(43, Rank.ACE, Suit.SPADES);
        Card card2 = card.makeCopyWithoutId();
        assertThat(card2.getId(), nullValue());
        assertThat(card2.getSuit(), is(card.getSuit()));
        assertThat(card2.getRank(), is(card.getRank()));
    }

    @Test
    public void testMakeCopyWithoutIds() {
        Card card1 = new Card(43, Rank.ACE, Suit.SPADES);
        Card card2 = new Card(44, Rank.KING, Suit.CLUBS);

        List<Card> cardsCopy = Card.makeCopyWithoutIds(Arrays.asList(card1, card2));

        assertThat(cardsCopy.size(), is(2));
        assertThat(cardsCopy.get(0).getId(), nullValue());
        assertThat(cardsCopy.get(0).getSuit(), is(card1.getSuit()));
        assertThat(cardsCopy.get(0).getRank(), is(card1.getRank()));
        assertThat(cardsCopy.get(1).getId(), nullValue());
        assertThat(cardsCopy.get(1).getSuit(), is(card2.getSuit()));
        assertThat(cardsCopy.get(1).getRank(), is(card2.getRank()));

    }
}
