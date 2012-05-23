package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.*;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TelesinaAnteSitInTest extends AbstractTexasHandTester {

    private PokerContext context;

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(20);
        context = state.getContext();
    }


    /**
     * Mock Game is staked at 20/10'
     */
    @Test
    public void testAnteSitIns() {
        MockPlayer[] mp = TestUtils.createMockPlayers(6, 100);
        MockPlayer[] startingPlayers = new MockPlayer[]{mp[0], mp[1]};
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, startingPlayers);

        // Force start
        state.timeout();

        // Blinds
        assertThat(mp[1].isActionPossible(PokerActionType.ANTE), is(true));
        assertThat(mp[0].isActionPossible(PokerActionType.ANTE), is(true));
        act(p[1], PokerActionType.ANTE);

        assertEquals(2, state.getSeatedPlayers().size());

        state.addPlayer(mp[3]);

        assertEquals(3, state.getSeatedPlayers().size());

        act(p[0], PokerActionType.ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

        state.timeout();
        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

    }

    @Test
    public void testDoubleAnte() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();
        try {
            // Blinds
            act(p[1], PokerActionType.ANTE);
            act(p[1], PokerActionType.ANTE);
            fail("Should not be able to post Ante two times in a row");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    @Test
    public void testAnteSitOutThenSitIt() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[1], PokerActionType.ANTE);
        act(p[2], PokerActionType.DECLINE_ENTRY_BET);

        // Assert that player 2 not in the current players
        assertNull(state.getPlayerInCurrentHand(p[2]));

        act(p[0], PokerActionType.ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        // Now player 2 should not be in the hard nor be awarded cards
        assertEquals(2, state.getPlayerInCurrentHand(p[1]).getPocketCards().getCards().size());
        assertEquals(2, state.getPlayerInCurrentHand(p[0]).getPocketCards().getCards().size());


        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);
    }

}
