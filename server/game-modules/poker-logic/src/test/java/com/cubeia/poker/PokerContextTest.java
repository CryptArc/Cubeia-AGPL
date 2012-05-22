package com.cubeia.poker;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PokerContextTest {

    @Mock
    private Predicate<PokerPlayer> readyPlayerFilter;

    @Mock
    private PokerSettings settings;

    private PokerContext context;
    private PokerPlayer player1;
    private PokerPlayer player2;
    private PokerPlayer player3;

    @Before
    public void setup() {
        initMocks(this);
        context = new PokerContext(null);
        context.settings = settings;

        when(settings.getRakeSettings()).thenReturn(TestUtils.createOnePercentRakeSettings());

        player1 = mockPlayer(1);
        player2 = mockPlayer(2);
        player3 = mockPlayer(3);
    }

    @Test
    public void testCreateCopyWithNotReadyPlayersExcluded() {
        when(readyPlayerFilter.apply(Mockito.any(PokerPlayer.class))).thenReturn(true).thenReturn(false);

        Map<Integer, PokerPlayer> map = new HashMap<Integer, PokerPlayer>();
        map.put(1, player1);
        map.put(2, player2);
        map.put(3, player3);

        SortedMap<Integer,PokerPlayer> copy = context.createCopyWithNotReadyPlayersExcluded(map, readyPlayerFilter);
        assertThat(copy.size(), is(1));
        assertThat(copy.get(1), is(player1));
    }

    @Test
    public void testResetValuesAtStartOfHand() {
        PotHolder oldPotHolder = new PotHolder(null);
        context.potHolder = oldPotHolder;
        context.addPlayer(player1);
        context.addPlayer(player2);

        context.resetValuesAtStartOfHand();

        verify(player1).resetBeforeNewHand();
        verify(player2).resetBeforeNewHand();
        assertThat(context.getPotHolder(), not(sameInstance(oldPotHolder)));
    }

    private PokerPlayer mockPlayer(int id) {
        PokerPlayer player = mock(PokerPlayer.class);
        when(player.getId()).thenReturn(id);
        when(player.getSeatId()).thenReturn(id);
        when(player.isSittingOut()).thenReturn(false);
        return player;
    }

}
