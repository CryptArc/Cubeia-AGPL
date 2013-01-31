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

package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNG;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;
import junit.framework.Assert;
import org.apache.log4j.Logger;

import static com.cubeia.poker.action.PokerActionType.*;

public class TelesinaDisconnectTest extends AbstractTexasHandTester {

    Logger log = Logger.getLogger(this.getClass());

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNG();
        sitoutTimeLimitMilliseconds = 1;
        super.setUp();
        setAnteLevel(10);
    }

    public void testDisconnectFolding() throws InterruptedException {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // 1. Disconnect player 0
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));

        // timeout the deal initialCardsRound
        state.timeout();

        // 2. Place bet
        act(p[2], BET);
        // 3. Verify that player 0 is folding
        Assert.assertTrue(mp[0].hasFolded());
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));

    }


    public void testDisconnectAndReconnect() throws InterruptedException {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // TODO
        // 1. Disconnect player 0
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));

        // timeout the dealInitialCardsRound
        state.timeout();

        // 2. check
        act(p[2], CHECK);
        // 3. Verify that player 0 has checked and not folded
        Assert.assertFalse(mp[0].hasFolded());
        act(p[1], CHECK);

        state.timeout();

        // 2. Verify that a reconnect lets player 0 act again
        state.playerIsSittingIn(p[0]);
        assertEquals(PokerPlayerStatus.SITIN, mockServerAdapter.getPokerPlayerStatus(p[0]));
        act(p[1], CHECK);
        act(p[2], CHECK);

        state.timeout();

        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        state.timeout();

        act(p[2], CHECK);
        act(p[0], CHECK);
        act(p[1], CHECK);

    }


    public void testDisconnectBug() throws InterruptedException {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        //  --- ANTE ROUND ---
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        assertPlayersNumberOfCards(mp, 2, 2, 2);

        // --- NEW BETTING ROUND ---

        //timeout the DealInitalCardsRound
        state.timeout();

        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
        act(p[2], CHECK);
        act(p[0], CHECK);
        PokerAction latestActionPerformed = mockServerAdapter.getLatestActionPerformed();
        assertEquals(p[1], latestActionPerformed.getPlayerId().intValue());
        assertEquals(PokerActionType.CHECK, latestActionPerformed.getActionType());
        state.timeout();

        assertPlayersNumberOfCards(mp, 3, 3, 3);

        // --- NEW BETTING ROUND ---
        act(p[0], BET);
        act(p[2], CALL);
        Assert.assertTrue(mp[1].hasFolded());

        assertPlayersNumberOfCards(mp, 4, 3, 4);

        state.timeout();

        // Make sure mp[0] does not get any more cards
        assertPlayersNumberOfCards(mp, 4, 3, 4);
    }

    public void assertPlayersNumberOfCards(MockPlayer[] mp, int p0NumberOfCards, int p1NumberOfCards, int p2NumberOfCards) {
        assertEquals(p0NumberOfCards, mp[0].getPocketCards().getCards().size());
        assertEquals(p1NumberOfCards, mp[1].getPocketCards().getCards().size());
        assertEquals(p2NumberOfCards, mp[2].getPocketCards().getCards().size());
    }

}
