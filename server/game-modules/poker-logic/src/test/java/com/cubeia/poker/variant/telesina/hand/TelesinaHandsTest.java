/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.variant.telesina.hand;

import com.cubeia.poker.*;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.variant.telesina.TelesinaDeck;
import com.cubeia.poker.variant.telesina.TelesinaDeckFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Random;

import static com.cubeia.poker.action.PokerActionType.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelesinaHandsTest extends AbstractTelesinaHandTester {

    @Override
    protected void setUp() throws Exception {
        super.setUpTelesina(new TelesinaDeckFactory(), 10);
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
        assertThat(call.getMinAmount(), CoreMatchers.is(20L));


        PossibleAction raise = request.getOption(RAISE);
        assertThat(raise, notNullValue()); // Not allowed in no-limit games
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

    @Test
    public void testAllButOneFoldsOnSidePotFinishesTheHand() throws Exception {
        TelesinaDeckFactory deckFactory = mock(TelesinaDeckFactory.class);

        TelesinaDeck deck = mock(TelesinaDeck.class);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        when(deck.deal()).thenReturn(
                new Card(1, "2H"), new Card(2, "3H"), new Card(3, "4H"), new Card(4, "5H"), new Card(5, "6H"), // Unknown :-(
                new Card(6, "9D"), new Card(7, "6D"), new Card(8, "6H"), new Card(9, "5H"), new Card(10, "TD"), // first round
                new Card(11, "KS"), new Card(12, "8C"), new Card(13, "QD"), new Card(14, "QH"),                     // second round
                new Card(15, "TC"), new Card(16, "7D"), new Card(17, "8D"),                                         // third round

                new Card(18, "7D"), new Card(19, "8D"), new Card(20, "9D"),
                new Card(21, "JD"), new Card(22, "QD"), new Card(23, "KD"), new Card(24, "AD"));

        super.setUpTelesina(deckFactory, 2, new RakeSettings(new BigDecimal("0.01"), 500L, 150L));

        MockPlayer[] mp = TestUtils.createMockPlayers(5);
        setBalanceAndPlayerId(0, mp, 1995583417, 170);
        setBalanceAndPlayerId(1, mp, 1995583572, 2);
        setBalanceAndPlayerId(2, mp, 1995583424, 158);
        setBalanceAndPlayerId(3, mp, 1995583448, 422);
        setBalanceAndPlayerId(4, mp, 1995583478, 638);

        long initialBalance = mp[0].getBalance() + mp[1].getBalance() + mp[2].getBalance() + mp[3].getBalance() + mp[4].getBalance();


        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE
        act(p[0], ANTE);
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[3], ANTE);
        act(p[4], ANTE);

        assertThat(mp[0].getBalance(), is((long) 170 - 2));
        assertThat(mp[1].getBalance(), is((long) 0));
        assertThat(mp[2].getBalance(), is((long) 158 - 2));
        assertThat(mp[3].getBalance(), is((long) 422 - 2));
        assertThat(mp[4].getBalance(), is((long) 638 - 2));

        // make deal initial pocket cards round end
        game.timeout();

        assertThat(mp[1].isAllIn(), is(true));

        act(p[4], BET, 4);
        act(p[0], RAISE, 8);
        // p[1] is all in, can't act
        act(p[2], CALL);
        act(p[3], FOLD);
        act(p[4], RAISE, 12);
        act(p[0], CALL);
        act(p[2], RAISE, 80);
        act(p[4], CALL);
        act(p[0], CALL);

        game.timeout();


        act(p[0], FOLD);
        act(p[2], CHECK);
        act(p[4], CHECK);

        game.timeout();

        act(p[4], FOLD);

        game.timeout();
        game.timeout();
        game.timeout();
        game.timeout();

        long totalRake = game.getPotHolder().calculateRake().getTotalRake();
        assertThat(totalRake, is(2L));

        game.timeout();

        // player 1 (1995583572) won, all other folded
        assertThat(mp[1].getBalance(), is(2 * 5L));     // winner of first pot (10)

        assertThat(mp[0].getBalance(), is(88L));
        assertThat(mp[2].getBalance(), is(76L + 240 - 2));        // winner of seconds pot (240), rake = 2
        assertThat(mp[3].getBalance(), is(420L));
        assertThat(mp[4].getBalance(), is(556L));

        long resultingBalance = mp[0].getBalance() + mp[1].getBalance() + mp[2].getBalance() + mp[3].getBalance() + mp[4].getBalance();
        assertThat(resultingBalance, is(initialBalance - totalRake));
    }

    private void setBalanceAndPlayerId(int index, MockPlayer[] mp, int playerId, int balance) {
        mp[index].setPlayerId(playerId);
        mp[index].setBalance(balance);
    }


}
