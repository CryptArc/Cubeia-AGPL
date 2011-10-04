package com.cubeia.poker.player;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

public class DefaultPokerPlayerTest {

    @Test
    public void testClearHand() {
        DefaultPokerPlayer player = new DefaultPokerPlayer(1337);
        player.addPocketCard(new Card(Rank.ACE, Suit.CLUBS), false);
        player.addPocketCard(new Card(Rank.TWO, Suit.CLUBS), true);
        
        assertThat(player.getPocketCards().getCards().isEmpty(), is(false));
        assertThat(player.getPublicPocketCards().isEmpty(), is(false));
        player.clearHand();
        assertThat(player.getPocketCards().getCards().isEmpty(), is(true));
        assertThat(player.getPublicPocketCards().isEmpty(), is(true));
    }

}
