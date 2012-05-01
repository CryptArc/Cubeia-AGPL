package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.variant.PokerVariant;
import junit.framework.Assert;
import org.junit.Test;

import static com.cubeia.poker.action.PokerActionType.*;

public class TelesinaBringInMoneyTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(10);
    }

    @Test
    public void testBringInMoneyInHand() {
        setAnteLevel(10);
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        act(p[2], BET, 90);
        act(p[0], CALL);
        act(p[1], CALL);

        // Progress until hand is complete
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        bringInMoneyToMp2(mp, p);
        state.timeout();
        // End of hand

        assertEquals(0, mp[0].getBalance());
        assertEquals(300, mp[1].getBalance());
        assertEquals(50, mp[2].getBalanceNotInHand());

        assertFalse(mp[0].isSittingOut());
        assertFalse(mp[2].isSittingOut());

        state.timeout();

        assertTrue(mp[0].isSittingOut());
        assertFalse(mp[2].isSittingOut());

        assertEquals(50, mp[2].getBalance());
        act(p[2], ANTE);
        act(p[1], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        // Now game should progress to betting round or we have a bug!
        Assert.assertNotNull(mp[2].getActionRequest().getOption(PokerActionType.CHECK));

    }

    @Test
    public void testBringInMoneyBetweenHands() {
        setAnteLevel(10);
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        act(p[2], BET, 90);
        act(p[0], CALL);
        act(p[1], CALL);

        // Progress until hand is complete
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        // End of hand

        assertEquals(0, mp[0].getBalance());
        assertEquals(300, mp[1].getBalance());
        assertEquals(0, mp[2].getBalance());

        assertFalse(mp[0].isSittingOut());
        assertFalse(mp[2].isSittingOut());

        bringInMoneyToMp2(mp, p);

        state.timeout();

        assertTrue(mp[0].isSittingOut());
        assertFalse(mp[2].isSittingOut());

        act(p[2], ANTE);
        act(p[1], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        // Now game should progress to betting round or we have a bug!
        Assert.assertNotNull(mp[2].getActionRequest().getOption(PokerActionType.CHECK));

    }


    @Test
    public void testSitInNextHand() {
        setAnteLevel(10);
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        act(p[2], BET, 90);
        act(p[0], CALL);
        act(p[1], CALL);

        // Progress until hand is complete
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        state.timeout();
        bringInMoneyToMp2(mp, p);

        assertEquals(50, mp[2].getBalanceNotInHand());

        state.timeout();
        // End of hand

        assertFalse(mp[2].isSittingOut());
        assertEquals(0, mp[0].getBalance());
        assertEquals(300, mp[1].getBalance());
        assertEquals(50, mp[2].getBalanceNotInHand());
        assertFalse(mp[0].isSittingOut());

        // Start new hand
        state.timeout();

        assertTrue(mp[0].isSittingOut());

        // PLayer 2 should now have the pending balance committed
        assertEquals(50, mp[2].getBalance());

        act(p[2], ANTE);
        act(p[1], ANTE);

        // timeout the DealInitialCardsRound
        state.timeout();

        state.playerIsSittingIn(p[0]);

        act(p[2], CHECK);
        act(p[1], FOLD);

        assertTrue(state.isFinished());
    }

    private void bringInMoneyToMp2(MockPlayer[] mp, int[] p) {
        // Player 2 brings in more cash between hands
        // Mimic the logic executed in the back end handler, this is brittle - if the back end handler
        // implementation changes then that behavior will not be used here. Never the less...
        int amountReserved = 50;
        if (state.isPlayerInHand(p[2])) {
            System.out.println("player is in hand, adding reserved amount " + amountReserved + " as pending");
            mp[2].addNotInHandAmount(amountReserved);
        } else {
            System.out.println("player is not in hand, adding reserved amount " + amountReserved + " to balance");
            mp[2].addChips(amountReserved);
        }

        state.playerIsSittingIn(p[2]);
    }

}
