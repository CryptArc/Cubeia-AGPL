package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

public class TelesinaAnteRageQuitTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(20);
    }

    @Test
    public void testAnteSitOutThenSitOut() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Antes
        act(p[1], PokerActionType.ANTE);
        state.playerIsSittingOut(p[1], SitOutStatus.SITTING_OUT);
        act(p[0], PokerActionType.ANTE);

        state.timeout();

        // Verify that both players get 2 cards dealt (one hidden and one public)
        // The rage quit bug was that the player sitting out, p[1], did not get the hidden card.
        assertEquals(2, state.getPlayerInCurrentHand(p[1]).getPocketCards().getCards().size());
        assertEquals(1, state.getPlayerInCurrentHand(p[1]).getPrivatePocketCards().size());
        assertEquals(2, state.getPlayerInCurrentHand(p[0]).getPocketCards().getCards().size());
        assertEquals(1, state.getPlayerInCurrentHand(p[0]).getPrivatePocketCards().size());
    }

}
