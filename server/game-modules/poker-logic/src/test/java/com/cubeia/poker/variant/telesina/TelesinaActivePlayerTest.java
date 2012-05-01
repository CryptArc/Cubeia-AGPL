package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TelesinaActivePlayerTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(20);
    }


    /**
     * Mock Game is staked at 20
     */
    @Test
    public void testAllInTelesinaHand() {

        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Set initial balances
        mp[0].setBalance(83);
        mp[1].setBalance(63);

        // Force start
        state.timeout();

        // Blinds
        assertThat(state.isWaitingForPlayerToAct(p[0]), is(true));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(true));
        act(p[1], PokerActionType.ANTE);
        act(p[0], PokerActionType.ANTE);

        // make deal initial pocket cards round end
        state.timeout();

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(false));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(true));
        act(p[1], PokerActionType.CHECK);

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(true));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(false));
        act(p[0], PokerActionType.CHECK);

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(false));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(false));
        state.timeout();

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(false));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(true));
        act(p[1], PokerActionType.BET, 40);

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(true));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(false));
        act(p[0], PokerActionType.FOLD, 40);

        assertThat(state.isWaitingForPlayerToAct(p[0]), is(false));
        assertThat(state.isWaitingForPlayerToAct(p[1]), is(false));

    }


}
