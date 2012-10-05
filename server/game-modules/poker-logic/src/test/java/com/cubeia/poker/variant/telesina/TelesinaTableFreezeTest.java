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
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

public class TelesinaTableFreezeTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNG();
        super.setUp();
        setAnteLevel(2);
    }

    @Test
    public void testBettingRoundEndFreeze() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3, 100);
        mp[2].setBalance(20);

        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[1], PokerActionType.ANTE);
        act(p[0], PokerActionType.ANTE);
        act(p[2], PokerActionType.ANTE);
        state.timeout();

        act(p[2], PokerActionType.BET, 18); // ALL IN
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CALL);

        state.playerSitsOut(p[0], SitOutStatus.SITTING_OUT);
        state.playerSitsOut(p[1], SitOutStatus.SITTING_OUT);

        int numberOfTimeoutsRequested = mockServerAdapter.getTimeoutRequests();

        state.timeout();

        int numberOfTimeoutsRequestedAfterCall = mockServerAdapter.getTimeoutRequests();

        // Check that we have a new scheduled timeout.
        assertEquals(numberOfTimeoutsRequested + 1, numberOfTimeoutsRequestedAfterCall);

    }

}
