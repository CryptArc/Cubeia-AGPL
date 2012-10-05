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

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.apache.log4j.Logger;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNG;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaSitoutTest extends AbstractTexasHandTester {

    Logger log = Logger.getLogger(this.getClass());
    private MockPlayer[] mp;
    int[] p;

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNG();
        sitoutTimeLimitMilliseconds = 1;
        super.setUp();
        setAnteLevel(10);

        mp = TestUtils.createMockPlayers(3, 100);
        p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // ANTE
        act(p[1], ANTE);
        act(p[2], ANTE);
        act(p[0], ANTE);

        // make deal initial pocket cards round end
        state.timeout();
    }

    public void testAllSittingOutButOne() throws InterruptedException {
        // Disconnect player 0 & 1
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));

        // Player 2 checks
        act(p[2], CHECK);

        // Verify that sit out players are checked and not folded
        Assert.assertFalse(mp[0].hasFolded());
        Assert.assertFalse(mp[1].hasFolded());

        // Move to next betting round
        state.timeout();

        // Assert that player 2 is requested to act
        act(p[2], CHECK);

        state.timeout();

        Assert.assertEquals(4, mp[2].getPocketCards().getCards().size());

        act(p[2], BET); // This should force the other players to auto-fold

        assertTrue(state.isFinished());
    }

    public void testAllSittingOutButOneFirstBettingRoundBug() throws InterruptedException {
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);

        Assert.assertEquals(2, mp[2].getPocketCards().getCards().size());
        assertNotNull(mp[2].getActionRequest().getOption(CHECK));

        act(p[2], CHECK); // Player 2 acts, the other should be auto-checked

        state.timeout();

        Assert.assertEquals(3, mp[2].getPocketCards().getCards().size());

        // Assert that player 2 is now asked to act (This was the bug)
        assertNotNull(mp[2].getActionRequest().getOption(CHECK));
        act(p[2], CHECK); // Player 2 acts, the other should be auto-checked

        state.timeout();
        Assert.assertEquals(4, mp[2].getPocketCards().getCards().size());

    }

    public void testSitOutTwiceOnlyNotifiesOnce() {
        ServerAdapter serverAdapter = mock(ServerAdapter.class);
        state.setServerAdapter(serverAdapter);

        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerIsSittingIn(p[0]);
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);

        verify(serverAdapter, times(2)).notifyPlayerStatusChanged(p[0], PokerPlayerStatus.SITOUT, true);
    }

    public void testSitInTwiceOnlyNotifiesOnce() {
        ServerAdapter serverAdapter = mock(ServerAdapter.class);
        state.setServerAdapter(serverAdapter);

        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerIsSittingIn(p[0]);
        state.playerIsSittingIn(p[0]);

        verify(serverAdapter, times(1)).notifyPlayerStatusChanged(p[0], PokerPlayerStatus.SITIN, true);
    }

    public void testEveryoneSittingOutDoesNotLeadToAllInScenario() throws InterruptedException {
        mockServerAdapter.clear();
        // Disconnect EVERYONE!
        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[2], SitOutStatus.SITTING_OUT);
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
        assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[2]));

        state.timeout();

        assertEquals(3, mockServerAdapter.getPerformedActionCount());

        PokerAction check1 = mockServerAdapter.getNthAction(0);
        assertEquals(new Integer(p[2]), check1.getPlayerId());

        PokerAction check2 = mockServerAdapter.getNthAction(1);
        assertEquals(new Integer(p[0]), check2.getPlayerId());

        PokerAction check3 = mockServerAdapter.getNthAction(2);
        assertEquals(new Integer(p[1]), check3.getPlayerId());

        mockServerAdapter.clear();
        state.timeout();
        assertEquals(3, mockServerAdapter.getPerformedActionCount());

        state.timeout(); // Deal pocket cards round

        mockServerAdapter.clear();
        state.timeout();
        assertEquals(3, mockServerAdapter.getPerformedActionCount());
    }

}
