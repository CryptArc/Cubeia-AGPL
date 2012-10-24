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
import com.cubeia.poker.variant.PokerVariant;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TelesinaActivePlayerTest extends AbstractTexasHandTester {

    @Override
    protected void setUp() throws Exception {
        variant = PokerVariant.TELESINA;
        rng = new NonRandomRNG();
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
