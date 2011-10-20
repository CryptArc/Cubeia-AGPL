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

    @Test
    public void testBalance() {
        DefaultPokerPlayer player = new DefaultPokerPlayer(1337);
        long balance = 10000;
        player.setBalance(balance);
        assertThat(player.getBalance(), is(balance));

        long bet = 100;
        player.addBet(bet);
        assertThat(player.getBalance(), is(balance - bet));
    }
    
    @Test
    public void testCommitPendingAmount() {
        DefaultPokerPlayer player = new DefaultPokerPlayer(1337);
        long balance = 10000;
        player.setBalance(balance);

        boolean hadPendingBalance = player.commitPendingBalance();
        assertThat(hadPendingBalance, is(false));
        
        long pendingBalance = 333;
        player.addPendingAmount(pendingBalance);
        
        assertThat(player.getPendingBalance(), is(pendingBalance));
        assertThat(player.getBalance(), is(balance));
    
        hadPendingBalance = player.commitPendingBalance();
        assertThat(hadPendingBalance, is(true));
        assertThat(player.getPendingBalance(), is(0L));
        assertThat(player.getBalance(), is(balance + pendingBalance));
    }
    
    @Test
    public void testAddPendingAmount() {
        DefaultPokerPlayer player = new DefaultPokerPlayer(1337);
        
        assertThat(player.getPendingBalance(), is(0L));
        
        long pendingAmount = 333;
        player.addPendingAmount(pendingAmount);
        assertThat(player.getPendingBalance(), is(pendingAmount));
        
        player.addPendingAmount(pendingAmount);
        assertThat(player.getPendingBalance(), is(pendingAmount * 2));
    }
}
