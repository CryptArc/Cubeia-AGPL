package com.cubeia.poker.result;

import com.cubeia.poker.player.PokerPlayer;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RevealOrderCalculatorTest {

    PokerPlayer player1;
    PokerPlayer player2;
    PokerPlayer player3;
    RevealOrderCalculator roc;
    SortedMap<Integer, PokerPlayer> seatingMap;
    List<Integer> revealOrder;
    int player1Id;
    int player2Id;
    int player3Id;

    private void setup() {
        roc = new RevealOrderCalculator();
        player1 = mock(PokerPlayer.class);
        player2 = mock(PokerPlayer.class);
        player3 = mock(PokerPlayer.class);

        player1Id = 1001;
        player2Id = 1002;
        player3Id = 1003;

        when(player1.getId()).thenReturn(player1Id);
        when(player2.getId()).thenReturn(player2Id);
        when(player3.getId()).thenReturn(player3Id);

        seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
    }

    private void assertPlayersId(int id1, int id2, int id3) {
        Iterator<Integer> revealIter = revealOrder.iterator();
        assertThat(revealIter.next(), is(id1));
        assertThat(revealIter.next(), is(id2));
        assertThat(revealIter.next(), is(id3));
    }

    @Test
    public void testRevealOrderWithLastCaller() {
        setup();
        revealOrder = roc.calculateRevealOrder(seatingMap, player2, player1);
        assertPlayersId(player2Id, player3Id, player1Id);
    }

    @Test
    public void testRevealOrderWithNoLastCaller() {
        setup();
        revealOrder = roc.calculateRevealOrder(seatingMap, null, player2);
        assertPlayersId(player3Id, player1Id, player2Id);
    }

    @Test
    public void testRevealOrderWithNoLastCallerDealerButtonAtLastSeat() {
        setup();
        revealOrder = roc.calculateRevealOrder(seatingMap, null, player3);
        assertPlayersId(player1Id, player2Id, player3Id);
    }

    @Test
    public void testRevealOrderOnePlayerFolded() {
        setup();
        when(player2.hasFolded()).thenReturn(true);
        revealOrder = roc.calculateRevealOrder(seatingMap, null, player1);
        assertThat(revealOrder.size(), is(2));
    }

}
