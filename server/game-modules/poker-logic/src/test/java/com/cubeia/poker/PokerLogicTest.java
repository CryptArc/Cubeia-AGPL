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

package com.cubeia.poker;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.tournament.RoundReport;
import com.cubeia.poker.util.SitoutCalculator;

/**
 * Integration test for poker logic.
 */
public class PokerLogicTest extends GuiceTest {

    private MockPlayer[] ps;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

//    public void testSimpleHoldemHand() {
//        MockPlayer[] mp = TestUtils.createMockPlayers(4);
//        int[] p = TestUtils.createPlayerIdArray(mp);
//        assertEquals(4, p.length);
//        addPlayers(state, mp);
//        assertEquals(4, state.getSeatedPlayers().size());
//
//        int chipsInPlay = countChipsAtTable(p);
//
//        // Force start
//        state.timeout();
//
//        // Blinds
//        act(p[1], PokerActionType.SMALL_BLIND);
//
//        assertTrue(mp[2].isActionPossible(PokerActionType.BIG_BLIND));
//        assertEquals(102, mockServerAdapter.getLastActionRequest().getPlayerId());
//        act(p[2], PokerActionType.BIG_BLIND);
//
//        assertTrue(mp[2].hasOption());
//        assertAllPlayersHaveCards(mp, 2);
//
//        assertEquals(0, state.getCommunityCards().size());
//
//        // Pre flop round
//        assertEquals(103, mockServerAdapter.getLastActionRequest().getPlayerId());
//        act(p[3], PokerActionType.CALL);
//        assertTrue(mp[3].hasActed());
//        assertEquals(100, mockServerAdapter.getLastActionRequest().getPlayerId());
//        act(p[0], PokerActionType.CALL);
//        act(p[1], PokerActionType.CALL);
//        act(p[2], PokerActionType.CHECK);
//        // everyone checked so now we should be in DealCommunityCards round
//
//        assertEquals(3, state.getCommunityCards().size());
//
//        assertThat(getCurrentRoundId(), is(0));
//
//        // Trigger deal community cards
//        state.timeout(); // timeout deal community cards. Starts a new betting round
//
//        assertThat(getCurrentRoundId(), is(1));
//
//        assertEquals(3, state.getCommunityCards().size());
//
//        // Flop round
//        act(p[1], PokerActionType.BET);
//        act(p[2], PokerActionType.CALL);
//        act(p[3], PokerActionType.CALL);
//        act(p[0], PokerActionType.CALL);
//
//        assertEquals(4, state.getCommunityCards().size());
//
//        // Trigger deal community cards
//        state.timeout();// timeout deal community cards. Starts a new betting round
//
//
//        // Turn round
//        act(p[1], PokerActionType.CHECK);
//        act(p[2], PokerActionType.BET);
//        act(p[3], PokerActionType.FOLD);
//        act(p[0], PokerActionType.FOLD);
//        act(p[1], PokerActionType.CALL);
//
//        // Trigger deal community cards
//        state.timeout();
//
//        assertEquals(5, state.getCommunityCards().size());
//
//        // River round
//        act(p[1], PokerActionType.CHECK);
//        act(p[2], PokerActionType.BET);
//        act(p[1], PokerActionType.FOLD);
//
//        // Assertions
//        assertTrue(state.isFinished());
//
//        // Check that we didn't create or lose any chips.
//        assertEquals(chipsInPlay, countChipsAtTable(p));
//    }

    // TODO!
//    private int getCurrentRoundId() {
//        int roundId = ((TexasHoldem) state.getGameType()).getCurrentRoundId();
//        return roundId;
//    }

    private int countChipsAtTable(int[] p) {
        int chipsInPlay = 0;
        for (int pid : p) {
            chipsInPlay += state.getBalance(pid);
        }
        return chipsInPlay;
    }

    private void act(int playerId, PokerActionType actionType) {
//		System.out.println("Options: "+mockServerAdapter.getActionRequest().getOptions());
//		System.out.println("Option["+actionType+"]: "+mockServerAdapter.getActionRequest().getOption(actionType));
        act(playerId, actionType, mockServerAdapter.getLastActionRequest().getOption(actionType).getMinAmount());
    }

    private void act(int playerId, PokerActionType actionType, long amount) {
        PokerAction action = new PokerAction(playerId, actionType);
        action.setBetAmount(amount);
        state.act(action);
    }

    public void testPostBlindsAndFold() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);

        // Small blind folds, hand should finish.
        assertFalse(state.isFinished());
        act(p[0], PokerActionType.FOLD);

        // Assertions
        assertTrue(state.isFinished());
    }

    public void testDeclinedPlayerSitsOut() {
        MockPlayer[] mp = TestUtils.createMockPlayers(3);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[1], PokerActionType.DECLINE_ENTRY_BET);
        act(p[2], PokerActionType.BIG_BLIND);

        assertEquals(2, mp[0].getPocketCards().getCards().size());
        assertTrue(mp[1].isSittingOut());
        assertEquals(0, mp[1].getPocketCards().getCards().size());

        act(p[0], PokerActionType.FOLD);
        assertTrue(state.isFinished());
    }

    public void testPostBlindsAndTimeout() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);

        // Small blind folds, hand should finish.
        assertFalse(state.isFinished());
        state.timeout();

        // Assertions
        assertTrue(state.isFinished());
    }

    public void testSmallBlindTimeout() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        state.timeout();

        assertFalse(mockServerAdapter.getLastActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND));
    }

    public void testPostBlindsCallAndFold() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);

        // Small blind folds, hand should finish.
        act(p[0], PokerActionType.CALL);
        assertFalse(state.isFinished());
        act(p[1], PokerActionType.FOLD);

        // Assertions
        assertTrue(state.isFinished());
    }

    public void testConsecutiveHands() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        int chipsInPlay = countChipsAtTable(p);

        // Force start
        state.timeout();

        // Blinds
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);
        act(p[0], PokerActionType.FOLD);

        // Assertions
        assertTrue(state.isFinished());
        assertEquals(chipsInPlay, countChipsAtTable(p));

        // Second hand, check that pocket cards have been cleared.
        state.timeout();
        assertFalse(state.isFinished());
        act(p[1], PokerActionType.SMALL_BLIND);
        act(p[0], PokerActionType.BIG_BLIND);

        assertAllPlayersHaveCards(mp, 2);
        act(p[1], PokerActionType.CALL);
        act(p[0], PokerActionType.CHECK);

        // Trigger deal community cards
        state.timeout();

        assertEquals(3, state.getCommunityCards().size());
        act(p[0], PokerActionType.BET);
        act(p[1], PokerActionType.FOLD);

        assertTrue(state.isFinished());
        assertEquals(chipsInPlay, countChipsAtTable(p));

        // Third hand, check that community cards have been cleared.
        state.timeout();
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);

        assertAllPlayersHaveCards(mp, 2);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CHECK);

        // Trigger deal community cards
        state.timeout();

        assertEquals(3, state.getCommunityCards().size());
        act(p[1], PokerActionType.BET);
        act(p[0], PokerActionType.FOLD);

        assertTrue(state.isFinished());

        assertEquals(chipsInPlay, countChipsAtTable(p));
    }

    public void testTimeoutInfiniteLoop() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Small blind times out.
        state.timeout();

        // Start next hand
        state.timeout();

        // FIXME: This test is correct, but we are currently auto-sitting in people,
        // see fixme in WaitingToStartState.
        // assertEquals(PokerState.NOT_STARTED, game.getGameState());
    }

    // TODO!
//    public void testStartGame() {
//        createGame(3);
//        // Trigger timeout that should start the game
//        state.timeout();
//
//        assertEquals(PokerState.PLAYING, state.getGameState());
//    }

    // TODO!
//    public void testSeatTwoPlayersAndLeave() {
//        createGame(2);
//
//        // Remove one player
//        state.removePlayer(ps[0]);
//
//        // Trigger timeout
//        state.timeout();
//
//        assertEquals(PokerState.NOT_STARTED, state.getGameState());
//    }

    public void testEndHandReport() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        state.timeout();
        act(p[0], PokerActionType.SMALL_BLIND);
        act(p[1], PokerActionType.BIG_BLIND);
        act(p[0], PokerActionType.CALL);
        act(p[1], PokerActionType.CHECK);

        // Trigger deal community cards
        state.timeout();

        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

        // Trigger deal community cards
        state.timeout();

        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

        // Trigger deal community cards
        state.timeout();

        act(p[1], PokerActionType.CHECK);
        act(p[0], PokerActionType.CHECK);

        assertEquals(7, findByPlayerId(p[0], mockServerAdapter.hands).getHand().getCards().size());
    }

    private RatedPlayerHand findByPlayerId(int playerId, Collection<RatedPlayerHand> hands) {
        for (RatedPlayerHand ph : hands) {
            if (playerId == ph.getPlayerId()) {
                return ph;
            }
        }
        return null;
    }

    public void testRequestAction() {
        createGame(3);
        // Trigger timeout that should start the game
        state.timeout();

        ActionRequest request = mockServerAdapter.getLastActionRequest();
        assertEquals(101, request.getPlayerId());
    }

    /**
     * This test might look messy to the untrained eye. But if you
     * just look hard, it's pretty clever.. :)
     * <p/>
     * Okay okay, I'll tell you (man, you're slow). I'm creating my own
     * server adapter so I can fail if I get the handFinished message
     * before the fold message. NEAT!
     */
    public void testWrongOrder() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);


        state.setServerAdapter(new ServerAdapter() {
            boolean foldActionReceived = false;
            
            @Override
            public Random getSystemRNG() {
            	return new Random();
            }

            @Override
            public void exposePrivateCards(ExposeCardsHolder holder) {
            }

            @Override
            public void notifyActionPerformed(PokerAction action, PokerPlayer pokerPlayer) {
                if (action.getActionType() == PokerActionType.FOLD) {
                    foldActionReceived = true;
                }
            }

            @Override
            public void notifyHandEnd(HandResult result, HandEndStatus status, boolean tournamentTable) {
                if (!foldActionReceived) {
                    fail();
                }
            }

            @Override
            public void notifyNewRound() {
            }

            @Override
            public void notifyCommunityCards(List<Card> cards) {
            }

            @Override
            public void notifyDealerButton(int seatId) {
            }

            @Override
            public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
            }

            @Override
            public void notifyPrivateCards(int playerId, List<Card> cards) {
            }

            @Override
            public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
            }

            @Override
            public void requestAction(ActionRequest request) {
            }

            @Override
            public void requestMultipleActions(Collection<ActionRequest> requests) {
            }

            @Override
            public void scheduleTimeout(long millis) {
            }

            @Override
            public void reportTournamentRound(RoundReport report) {
            }

            @Override
            public void cleanupPlayers(SitoutCalculator sitoutCalculator) {
            }

            @Override
            public void notifyPlayerBalance(PokerPlayer p) {
            }

            @Override
            public void notifyNewHand() {
            }

            @Override
            public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status, boolean isInCurrentHand) {
            }

            @Override
            public void notifyDeckInfo(int size, Rank rankLow) {
            }

            @Override
            public void notifyPotUpdates(Collection<Pot> pots, Collection<PotTransition> potTransitions) {
            }

            @Override
            public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand) {
            }

            @Override
            public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
            }

            @Override
            public void unseatPlayer(int playerId, boolean setAsWatcher) {
            }

            @Override
            public void notifyTakeBackUncalledBet(int playerId, int amount) {
            }

            @Override
            public void notifyExternalSessionReferenceInfo(int playerId, String externalTableReference, String externalTableSessionReference) {
            }

            @Override
            public void notifyFutureAllowedActions(PokerPlayer player, List<PokerActionType> optionList) {
            }

            @Override
            public void performPendingBuyIns(Collection<PokerPlayer> players) {
            }

            @Override
            public void notifyHandStartPlayerStatus(int playerId, PokerPlayerStatus status) {
            }

            @Override
            public void notifyDisconnected(int playerId) {
            }

            @Override
            public String getIntegrationHandId() {
                return "hid";
            }
        });
        state.timeout();
        act(p[0], PokerActionType.SMALL_BLIND, 10);
        act(p[1], PokerActionType.BIG_BLIND, 20);
        act(p[0], PokerActionType.FOLD, 0);
    }

    public void testBlindsActionPerformedNotification() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        state.timeout();

        act(p[0], PokerActionType.SMALL_BLIND);
        assertNotNull(mockServerAdapter.getLatestActionPerformed());
    }

    public void testDenySmallBlind() {
        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        state.timeout();
        mockServerAdapter.hands = null;
        act(p[0], PokerActionType.DECLINE_ENTRY_BET);
        assertEquals(HandEndStatus.CANCELED_TOO_FEW_PLAYERS, mockServerAdapter.handEndStatus);
    }

    public void testPlayerLeavesBeforeStartOfHand() {
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        state.removePlayer(p[1]);

        // Force start
        state.timeout();

        // Blinds
        act(p[2], PokerActionType.SMALL_BLIND);
        act(p[3], PokerActionType.BIG_BLIND);

        // All players fold, hand should finish.
        assertFalse(state.isFinished());
        act(p[0], PokerActionType.FOLD);
        act(p[2], PokerActionType.FOLD);

        // Assertions
        assertTrue(state.isFinished());
    }

    public void testRejectSmallBlindStallsGameBug() {
        MockPlayer[] mp = TestUtils.createMockPlayers(4);
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(state, mp);

        // Force start
        state.timeout();

        // Blinds
        mockServerAdapter.clearActionRequest();
        act(p[1], PokerActionType.DECLINE_ENTRY_BET, 0);
        assertNotNull("The next player should be asked to post big blind.", mockServerAdapter.getLastActionRequest());
    }

    private void createGame(int players) {
        ps = TestUtils.createMockPlayers(players);
        assertEquals(0, mockServerAdapter.getTimeoutRequests());
        addPlayers(state, ps);
        assertEquals(1, mockServerAdapter.getTimeoutRequests());
    }

    private void assertAllPlayersHaveCards(PokerPlayer[] p,
                                           int expectedNumberOfCards) {
        for (PokerPlayer pl : p) {
            assertEquals(expectedNumberOfCards, pl.getPocketCards().getCards().size());
        }
    }

    private void addPlayers(PokerState game, PokerPlayer[] p) {
        for (PokerPlayer pl : p) {
            game.addPlayer(pl);
        }
    }

}
