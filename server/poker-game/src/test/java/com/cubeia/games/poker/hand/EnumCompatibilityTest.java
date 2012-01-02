package com.cubeia.games.poker.hand;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.games.poker.io.protocol.Enums;

import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

public class EnumCompatibilityTest {
    @Test
    public void testSuitEnumCompatibility() {
        String msg = "Suit enum ordinal diff between protocol and domain";
        assertThat(msg, Enums.Suit.CLUBS.ordinal(), is(Suit.CLUBS.ordinal()));
        assertThat(msg, Enums.Suit.DIAMONDS.ordinal(), is(Suit.DIAMONDS.ordinal()));
        assertThat(msg, Enums.Suit.HEARTS.ordinal(), is(Suit.HEARTS.ordinal()));
        assertThat(msg, Enums.Suit.SPADES.ordinal(), is(Suit.SPADES.ordinal()));
    }

    @Test
    public void testRankEnumCompatibility() {
        String msg = "Suit enum ordinal diff between protocol and domain";
        assertThat(msg, Enums.Rank.ACE.ordinal(), is(Rank.ACE.ordinal()));
        assertThat(msg, Enums.Rank.KING.ordinal(), is(Rank.KING.ordinal()));
        assertThat(msg, Enums.Rank.QUEEN.ordinal(), is(Rank.QUEEN.ordinal()));
        assertThat(msg, Enums.Rank.JACK.ordinal(), is(Rank.JACK.ordinal()));
        assertThat(msg, Enums.Rank.TEN.ordinal(), is(Rank.TEN.ordinal()));
        assertThat(msg, Enums.Rank.NINE.ordinal(), is(Rank.NINE.ordinal()));
        assertThat(msg, Enums.Rank.EIGHT.ordinal(), is(Rank.EIGHT.ordinal()));
        assertThat(msg, Enums.Rank.SEVEN.ordinal(), is(Rank.SEVEN.ordinal()));
        assertThat(msg, Enums.Rank.SIX.ordinal(), is(Rank.SIX.ordinal()));
        assertThat(msg, Enums.Rank.FIVE.ordinal(), is(Rank.FIVE.ordinal()));
        assertThat(msg, Enums.Rank.FOUR.ordinal(), is(Rank.FOUR.ordinal()));
        assertThat(msg, Enums.Rank.THREE.ordinal(), is(Rank.THREE.ordinal()));
        assertThat(msg, Enums.Rank.TWO.ordinal(), is(Rank.TWO.ordinal()));
    }
}
