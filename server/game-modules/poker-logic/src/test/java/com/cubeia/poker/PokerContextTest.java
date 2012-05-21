package com.cubeia.poker;

import com.cubeia.poker.player.PokerPlayer;
import com.google.common.base.Predicate;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PokerContextTest {

    @Mock
    private Predicate<PokerPlayer> readyPlayerFilter;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testCreateCopyWithNotReadyPlayersExcluded() {
        when(readyPlayerFilter.apply(Mockito.any(PokerPlayer.class))).thenReturn(true).thenReturn(false);

        PokerContext context = new PokerContext(null);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        Map<Integer, PokerPlayer> map = new HashMap<Integer, PokerPlayer>();
        map.put(1, player1);
        map.put(2, player2);
        map.put(3, player3);

        SortedMap<Integer,PokerPlayer> copy = context.createCopyWithNotReadyPlayersExcluded(map, readyPlayerFilter);
        assertThat(copy.size(), is(1));
        assertThat(copy.get(1), is(player1));
    }

}
