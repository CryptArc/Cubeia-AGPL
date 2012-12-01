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

package com.cubeia.poker.betlevel;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;

/**
 * Integration test for poker logic.
 */
public class BalanceTest extends AbstractTexasHandTester {

    /**
     * Mock Game is staked at 100/50
     * Mock Players have a chip count of 5000.
     * <p/>
     * This test relies on that no rake is deducted from the winnings
     */
    public void testSimpleHoldemHand() {
        setAnteLevel(100);
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        assertEquals(4, p.length);
        addPlayers(state, mp);
        assertEquals(4, state.getSeatedPlayers().size());

        // Force start
        state.timeout();
        assertEquals(5000, mp[0].getBalance());
        assertEquals(5000, mp[1].getBalance());
        assertEquals(5000, mp[2].getBalance());
        assertEquals(5000, mp[3].getBalance());

        // Blinds
        act(p[1], PokerActionType.SMALL_BLIND);
        act(p[2], PokerActionType.BIG_BLIND); //
        act(p[3], PokerActionType.CALL);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CALL);
        act(p[2], PokerActionType.CHECK);

        // Family pot, all remaining balances = 4900
        assertEquals(400, state.getPotHolder().getTotalPotSize());
        assertEquals(4900, mp[0].getBalance());
        assertEquals(4900, mp[1].getBalance());
        assertEquals(4900, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // FLOP
        act(p[1], PokerActionType.BET, 1000);
        act(p[2], PokerActionType.FOLD);
        act(p[3], PokerActionType.RAISE, 2000);
        act(p[0], PokerActionType.CALL, 2000);
        act(p[1], PokerActionType.CALL, 2000);

        assertEquals(6400, state.getPotHolder().getTotalPotSize());
        assertEquals(2900, mp[0].getBalance());
        assertEquals(2900, mp[1].getBalance());
        assertEquals(4900, mp[2].getBalance());
        assertEquals(2900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // TURN
        act(p[1], PokerActionType.CHECK);
        act(p[3], PokerActionType.BET, 1000);
        act(p[0], PokerActionType.RAISE, 2000);
        act(p[1], PokerActionType.CALL, 2000);
        act(p[3], PokerActionType.FOLD);

        assertEquals(11400, state.getPotHolder().getTotalPotSize());
        assertEquals(900, mp[0].getBalance());
        assertEquals(900, mp[1].getBalance());
        assertEquals(4900, mp[2].getBalance());
        assertEquals(1900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // RIVER
        act(p[1], PokerActionType.BET, 400);
        act(p[0], PokerActionType.RAISE, 800);
        act(p[1], PokerActionType.FOLD);

        assertEquals(12700, mp[0].getBalance());
        assertEquals(500, mp[1].getBalance());
        assertEquals(4900, mp[2].getBalance());
        assertEquals(1900, mp[3].getBalance());
        assertEquals(20000, mp[0].getBalance() + mp[1].getBalance() + mp[2].getBalance() + mp[3].getBalance());
        // GAME OVER
    }

    /**
     * Mock Game is staked at 100/50
     * Mock Players have a chip count of 5000.
     * <p/>
     * This test relies on that no rake is deducted from the winnings
     */
    public void testBetAmounts1() {
        setAnteLevel(100);
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        assertEquals(4, p.length);
        addPlayers(state, mp);
        assertEquals(4, state.getSeatedPlayers().size());

        // Force start
        state.timeout();
        assertEquals(5000, mp[0].getBalance());
        assertEquals(5000, mp[1].getBalance());
        assertEquals(5000, mp[2].getBalance());
        assertEquals(5000, mp[3].getBalance());

        // Blinds
        act(p[1], PokerActionType.SMALL_BLIND);
        act(p[2], PokerActionType.BIG_BLIND); //
        act(p[3], PokerActionType.CALL);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CALL);
        act(p[2], PokerActionType.CHECK);

        // Family pot, all remaining balances = 4900
        assertEquals(400, state.getPotHolder().getTotalPotSize());
        assertEquals(4900, mp[0].getBalance());
        assertEquals(4900, mp[1].getBalance());
        assertEquals(4900, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // FLOP
        act(p[1], PokerActionType.BET, 4000);

        assertTrue(mp[2].isActionPossible(PokerActionType.CALL));
        assertTrue(mp[2].isActionPossible(PokerActionType.RAISE));
        assertEquals(4000, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(4000, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());
        assertEquals(4900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.RAISE).getMinAmount());
        assertEquals(4900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.RAISE).getMaxAmount());

        act(p[2], PokerActionType.CALL, 4000);
        act(p[3], PokerActionType.FOLD);
        act(p[0], PokerActionType.FOLD);

        // Trigger deal community cards
        state.timeout();

        // TURN
        assertTrue(mp[1].isActionPossible(PokerActionType.CHECK));
        assertTrue(mp[1].isActionPossible(PokerActionType.BET));
        assertEquals(100, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMaxAmount());
        act(p[1], PokerActionType.BET, 900);

        assertTrue(mp[2].isActionPossible(PokerActionType.CALL));
        assertFalse(mp[2].isActionPossible(PokerActionType.RAISE));
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());
    }

    /**
     * Mock Game is staked at 100/50
     * Mock Players have a chip count of 5000.
     * <p/>
     * This test relies on that no rake is deducted from the winnings
     */
    public void testBetAmounts2() {
        setAnteLevel(100);
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        assertEquals(4, p.length);
        addPlayers(state, mp);
        assertEquals(4, state.getSeatedPlayers().size());
        mp[1].setBalance(10000); // This player has more cash
        mp[2].setBalance(1000); // This player has little cash

        // Force start
        state.timeout();
        assertEquals(5000, mp[0].getBalance());
        assertEquals(10000, mp[1].getBalance());
        assertEquals(1000, mp[2].getBalance());
        assertEquals(5000, mp[3].getBalance());

        // Blinds
        act(p[1], PokerActionType.SMALL_BLIND);
        act(p[2], PokerActionType.BIG_BLIND); //
        act(p[3], PokerActionType.CALL);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CALL);
        act(p[2], PokerActionType.CHECK);

        // Family pot, all remaining balances = 4900
        assertEquals(400, state.getPotHolder().getTotalPotSize());
        assertEquals(4900, mp[0].getBalance());
        assertEquals(9900, mp[1].getBalance());
        assertEquals(900, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // FLOP
        act(p[1], PokerActionType.BET, 4000);

        assertTrue(mp[2].isActionPossible(PokerActionType.CALL));
        assertFalse(mp[2].isActionPossible(PokerActionType.RAISE));
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());

        act(p[2], PokerActionType.FOLD);
        act(p[3], PokerActionType.FOLD);
        act(p[0], PokerActionType.CALL);

        assertEquals(8400, state.getPotHolder().getTotalPotSize());
        assertEquals(900, mp[0].getBalance());
        assertEquals(5900, mp[1].getBalance());
        assertEquals(900, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // TURN
        assertTrue(mp[1].isActionPossible(PokerActionType.CHECK));
        assertTrue(mp[1].isActionPossible(PokerActionType.BET));
        assertEquals(100, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMinAmount());
        assertEquals(5900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMaxAmount());
        act(p[1], PokerActionType.BET, 4000);

        assertEquals(p[0], mockServerAdapter.getLastActionRequest().getPlayerId());
        assertTrue(mp[0].isActionPossible(PokerActionType.CALL));
        assertFalse(mp[0].isActionPossible(PokerActionType.RAISE));
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());
    }

    /**
     * Mock Game is staked at 100/50
     * Mock Players have a chip count of 5000.
     * <p/>
     * This test relies on that no rake is deducted from the winnings
     */
    public void testAllIn() {
        setAnteLevel(100);
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        assertEquals(4, p.length);
        addPlayers(state, mp);
        assertEquals(4, state.getSeatedPlayers().size());
        mp[1].setBalance(10000); // This player has more cash
        mp[2].setBalance(1000); // This player has little cash

        // Force start
        state.timeout();
        assertEquals(5000, mp[0].getBalance());
        assertEquals(10000, mp[1].getBalance());
        assertEquals(1000, mp[2].getBalance());
        assertEquals(5000, mp[3].getBalance());

        // Blinds
        act(p[1], PokerActionType.SMALL_BLIND);
        act(p[2], PokerActionType.BIG_BLIND); //
        act(p[3], PokerActionType.CALL);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CALL);
        act(p[2], PokerActionType.CHECK);

        // Family pot, all remaining balances = 4900
        assertEquals(400, state.getPotHolder().getTotalPotSize());
        assertEquals(4900, mp[0].getBalance());
        assertEquals(9900, mp[1].getBalance());
        assertEquals(900, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // FLOP
        act(p[1], PokerActionType.BET, 4000);

        assertTrue(mp[2].isActionPossible(PokerActionType.CALL));
        assertFalse(mp[2].isActionPossible(PokerActionType.RAISE));
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());

        act(p[2], PokerActionType.CALL, 900);
        assertTrue(mp[2].isAllIn());
        act(p[3], PokerActionType.FOLD);
        act(p[0], PokerActionType.CALL);

        assertEquals(9300, state.getPotHolder().getTotalPotSize());
        assertEquals(900, mp[0].getBalance());
        assertEquals(5900, mp[1].getBalance());
        assertEquals(0, mp[2].getBalance());
        assertEquals(4900, mp[3].getBalance());

        // Trigger deal community cards
        state.timeout();

        // TURN
        assertTrue(mp[1].isActionPossible(PokerActionType.CHECK));
        assertTrue(mp[1].isActionPossible(PokerActionType.BET));
        assertEquals(100, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMinAmount());
        assertEquals(5900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.BET).getMaxAmount());
        act(p[1], PokerActionType.BET, 4000);

        assertEquals(p[0], mockServerAdapter.getLastActionRequest().getPlayerId());
        assertTrue(mp[0].isActionPossible(PokerActionType.CALL));
        assertFalse(mp[0].isActionPossible(PokerActionType.RAISE));
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMinAmount());
        assertEquals(900, mockServerAdapter.getLastActionRequest().getOption(PokerActionType.CALL).getMaxAmount());
        act(p[0], PokerActionType.CALL, 900);

        // Timeout after pocket cards have been flipped open.
        state.timeout();

        // Trigger deal community cards
        state.timeout();

        // RIVER
        assertTrue(mp[2].isAllIn());
        assertTrue(mp[0].isAllIn());

        state.timeout();

        assertTrue(state.isFinished());
    }

    /**
     * Mock Game is staked at 100/50
     * Mock Players have a chip count of 5000.
     * <p/>
     * This test relies on that no rake is deducted from the winnings
     */
    public void testAllInShowDown() {
        setAnteLevel(100);
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        assertEquals(4, p.length);
        addPlayers(state, mp);
        assertEquals(4, state.getSeatedPlayers().size());
        mp[1].setBalance(2000); // This player has more cash
        mp[2].setBalance(200); // This player has little cash
        mp[3].setBalance(200); // This player has little cash
        mp[0].setBalance(200); // This player has little cash

        // Force start
        state.timeout();
        assertEquals(200, mp[0].getBalance());
        assertEquals(2000, mp[1].getBalance());
        assertEquals(200, mp[2].getBalance());
        assertEquals(200, mp[3].getBalance());

        // Blinds
        act(p[1], PokerActionType.SMALL_BLIND); // 100
        act(p[2], PokerActionType.BIG_BLIND); //  200
        act(p[3], PokerActionType.CALL); // 200
        act(p[0], PokerActionType.CALL); // 200

        // tot 700

        assertTrue(mp[1].isActionPossible(PokerActionType.RAISE));

        act(p[1], PokerActionType.RAISE, 1000); // 100 will be added. The rest is an over bet and will be returned to balance
        act(p[2], PokerActionType.CALL); // ALL IN = 0
        act(p[3], PokerActionType.CALL); // ALL IN = 0
        act(p[0], PokerActionType.CALL); // ALL IN = 0

        // tot 800

        // Family pot, everyone is all in
        assertEquals(800, state.getPotHolder().getTotalPotSize()); // each of the four players payed 200.
        assertEquals(0, mp[0].getBalance());
        assertEquals(1800, mp[1].getBalance()); // remember that this guy bet 1100 (small blind 100 + raise 1000) but it was a over bet and only 200 was used of the initial 2000
        assertEquals(0, mp[2].getBalance());
        assertEquals(0, mp[3].getBalance());

        // Timeout after pocket cards have been exposed
        state.timeout();

        // Trigger deal community cards
        state.timeout();

        // FLOP
        state.timeout();

        // Trigger deal community cards
        state.timeout();

        // TURN
        state.timeout();

        // Trigger deal community cards
        state.timeout();

        // RIVER
        state.timeout();

        assertTrue(state.isFinished());


    }

}
