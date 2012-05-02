package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

import static com.cubeia.poker.action.PokerActionType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TelesinaAllinTest extends AbstractTexasHandTester {

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
        assertThat(mp[1].isActionPossible(PokerActionType.ANTE), is(true));
        assertThat(mp[0].isActionPossible(PokerActionType.ANTE), is(true));
        act(p[1], PokerActionType.ANTE);
        act(p[0], PokerActionType.ANTE);

        // make deal initial pocket cards round end
        state.timeout();

        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

        state.timeout();

        PossibleAction betRequest = mp[1].getActionRequest().getOption(PokerActionType.BET);
        assertThat(betRequest.getMinAmount(), is(40L));
        assertThat(betRequest.getMaxAmount(), is(43L));
        act(p[1], PokerActionType.BET, 40);

        PossibleAction callRequest = mp[0].getActionRequest().getOption(PokerActionType.CALL);
        assertThat(callRequest.getMinAmount(), is(40L));
        assertThat(callRequest.getMaxAmount(), is(40L));

        PossibleAction raiseRequest = mp[0].getActionRequest().getOption(PokerActionType.RAISE);
        assertThat(raiseRequest.getMinAmount(), is(63L));
        assertThat(raiseRequest.getMaxAmount(), is(63L));

    }


    @Test
    public void testAllInNoBettingAllowed() {

        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Set initial balances
        mp[0].setBalance(500);
        mp[1].setBalance(100);

        // Force start
        state.timeout();

        // Blinds
        assertThat(mp[1].isActionPossible(PokerActionType.ANTE), is(true));
        assertThat(mp[0].isActionPossible(PokerActionType.ANTE), is(true));
        act(p[1], PokerActionType.ANTE);
        act(p[0], PokerActionType.ANTE);

        // make deal initial pocket cards round end
        state.timeout();

        act(p[1], PokerActionType.BET, 80); // ALL IN
        act(p[0], PokerActionType.CALL);     // Calls but is not all in

        state.timeout();

        // TODO: Verify that player 0 is all in
        // verify that no further betting is allowed or requested

        assertThat(mp[1].isAllIn(), is(true));

        PossibleAction option = mp[1].getActionRequest().getOption(PokerActionType.BET);
        assertThat(option, nullValue());

        option = mp[0].getActionRequest().getOption(PokerActionType.BET);
        assertThat(option, nullValue());
    }

    @Test
    public void testHighBetLevelShouldNotCauseAllIn() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2, 1000);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        assertThat(mp[1].isActionPossible(ANTE), is(true));
        assertThat(mp[0].isActionPossible(ANTE), is(true));
        act(p[1], ANTE);
        act(p[0], ANTE);

        // make deal initial pocket cards round end
        state.timeout();

        act(p[1], BET, 970);
        act(p[0], CALL);

        state.timeout();

        PossibleAction betRequest = mp[1].getActionRequest().getOption(PokerActionType.BET);
        assertThat(betRequest.getMinAmount(), is(10L));
        assertThat(betRequest.getMaxAmount(), is(10L));

        act(p[1], CHECK);

        betRequest = mp[0].getActionRequest().getOption(PokerActionType.BET);
        assertNotNull(betRequest);
        assertThat(betRequest.getMinAmount(), is(10L));
        assertThat(betRequest.getMaxAmount(), is(10L));

    }

}
