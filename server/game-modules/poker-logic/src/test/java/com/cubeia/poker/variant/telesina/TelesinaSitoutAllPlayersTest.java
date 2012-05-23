package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TelesinaSitoutAllPlayersTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(2);
    }

    @Test
    public void testEveryoneSittingOutShouldGiveOneActionPerBettingRound() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        assertThat(mp[1].isActionPossible(ANTE), is(true));
        assertThat(mp[0].isActionPossible(ANTE), is(true));
        act(p[1], ANTE);
        act(p[0], ANTE);

        mockServerAdapter.clear();
        state.timeout();

        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);

        state.timeout();

        assertEquals(2, mockServerAdapter.getPerformedActionCount());

        mockServerAdapter.clear();
        state.timeout();
        assertEquals(2, mockServerAdapter.getPerformedActionCount());

        state.timeout();

        mockServerAdapter.clear();
        state.timeout();
        assertEquals(2, mockServerAdapter.getPerformedActionCount());

    }

}
