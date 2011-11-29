package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CALL;
import static com.cubeia.poker.action.PokerActionType.FOLD;
import static com.cubeia.poker.action.PokerActionType.RAISE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaHandsTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNGProvider();
        super.setUp();
        setAnteLevel(10);
    }

    @Test
    public void testAnteTimeoutHand2() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        assertEquals(2, mp[1].getPocketCards().getCards().size());
        assertEquals(2, mp[2].getPocketCards().getCards().size());
        assertEquals(2, mp[0].getPocketCards().getCards().size());

        // make deal initial pocket cards round end
        game.timeout();

        act(p[2], PokerActionType.CHECK);
        act(p[0], PokerActionType.FOLD);
        act(p[1], PokerActionType.CHECK);

        game.timeout();

        assertEquals(3, mp[1].getPocketCards().getCards().size());
        assertEquals(3, mp[2].getPocketCards().getCards().size());
        assertEquals(2, mp[0].getPocketCards().getCards().size());
    }

    
    @Test
    public void testRaiseLevelWhenNoMinBet() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 110);
        mp[0].setBalance(29);

        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // make deal initial pocket cards round end
        game.timeout();

        
        act(p[2], BET, 20);
        act(p[0], CALL, 19); // All in

        // Now p[1] should be able to raise by 1 to 20 or more since p[0]'s raise never reached
        // the min raise level of 2x10 = 20.
        ActionRequest request = mp[1].getActionRequest();

        PossibleAction call = request.getOption(CALL);
        assertThat(call, notNullValue());
        System.out.println("Call: " + call);
        assertThat(call.getMinAmount(), CoreMatchers.is(20L));


        PossibleAction raise = request.getOption(RAISE);
        assertThat(raise, notNullValue()); // Not allowed in no-limit games
        System.out.println("Raise: " + raise);
        assertThat(raise.getMinAmount(), CoreMatchers.is(40L));
    }

    @Test
    public void testNotAllowedToRaiseWhenUnderMinRaise() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 110);
        mp[0].setBalance(29);

        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // make deal initial pocket cards round end
        game.timeout();

        act(p[2], BET, 20);
        act(p[0], CALL, 19);
        act(p[1], CALL, 19);

        // Now p[2] should not be allowed to raise since the raise by p[0] was under min raise (min raise: 2xBet = 20).
        // Note: This is only for no-limit and the rule is different for fixed limit if we ever wanted to implement that.
        ActionRequest request = mp[2].getActionRequest();
        assertThat(request.getOption(FOLD), nullValue());
        assertThat(request.getOption(CALL), nullValue());
        assertThat(request.getOption(RAISE), nullValue()); // Not allowed in no-limit games
    }

    @Test
    public void testRaiseLevel() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);

        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE - 90 left after
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // make deal initial pocket cards round end
        game.timeout();

        assertThat(mp[2].getActionRequest().getOption(BET).getMinAmount(), is(20L));
        act(p[2], BET, 20);

        assertThat(mp[0].getActionRequest().getOption(CALL).getMinAmount(), is(20L));
        assertThat(mp[0].getActionRequest().getOption(RAISE).getMinAmount(), is(40L));
        assertThat(mp[0].getActionRequest().getOption(RAISE).getMaxAmount(), is(90L));
        act(p[0], CALL);
        act(p[1], RAISE, 40);

        assertThat(mp[2].getActionRequest().getOption(CALL).getMinAmount(), is(20L)); // CALL by 20 to reach 40
        assertThat(mp[2].getActionRequest().getOption(RAISE).getMinAmount(), is(60L)); // Min raise by 40 + 20 in the bet stack = 60 in total
        assertThat(mp[2].getActionRequest().getOption(RAISE).getMaxAmount(), is(90L));
        act(p[2], CALL);

    }
}
