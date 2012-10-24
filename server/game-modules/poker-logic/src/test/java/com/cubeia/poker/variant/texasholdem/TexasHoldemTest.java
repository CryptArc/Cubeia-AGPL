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

package com.cubeia.poker.variant.texasholdem;

import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.blinds.MissedBlindsStatus;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.result.RevealOrderCalculator;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.HandFinishedListener;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.BIG_BLIND;
import static com.cubeia.poker.action.PokerActionType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND;
import static com.cubeia.poker.action.PokerActionType.CALL;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import static com.cubeia.poker.action.PokerActionType.DEAD_SMALL_BLIND;
import static com.cubeia.poker.action.PokerActionType.DECLINE_ENTRY_BET;
import static com.cubeia.poker.action.PokerActionType.FOLD;
import static com.cubeia.poker.action.PokerActionType.RAISE;
import static com.cubeia.poker.action.PokerActionType.SMALL_BLIND;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TexasHoldemTest {

    private TexasHoldem texas;

    @Mock
    private PokerContext context;

    @Mock
    private Random random;
    
    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private HandFinishedListener listener;

    @Mock
    private RevealOrderCalculator revealOrderCalculator;

    private MockPlayer player1;

    private MockPlayer player2;

    private PotHolder potHolder;

    private TreeMap<Integer, PokerPlayer> seatingMap;

    private Map<Integer, PokerPlayer> playerMap;

    private RakeSettings rakeSettings;

    private MockPlayer[] p;

    private Predicate<PokerPlayer> readyPlayersFilter = new Predicate<PokerPlayer>() {
        @Override
        public boolean apply(@Nullable PokerPlayer pokerPlayer) {
            return true;
        }
    };

    @Before
    public void setup() {
        initMocks(this);
        rakeSettings = new RakeSettings(new BigDecimal("0.06"), 500, 150);
        potHolder = new PotHolder(new LinearRakeWithLimitCalculator(rakeSettings));
        when(context.getPotHolder()).thenReturn(potHolder);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(serverAdapter.getSystemRNG()).thenReturn(random);

        texas = new TexasHoldem();
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        texas.addHandFinishedListener(listener);

        player1 = new MockPlayer(1);
        player2 = new MockPlayer(2);
    }

    private void prepareContext(PokerPlayer ... players) {
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        playerMap = new HashMap<Integer, PokerPlayer>();
        for (PokerPlayer player : players) {
            seatingMap.put(player.getSeatId(), player);
            playerMap.put(player.getId(), player);
        }
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getCurrentHandPlayerMap()).thenReturn(playerMap);
    }

    @Test
    public void testMissSmallAndBigBlind() {
        PokerContext context = prepareContext(4);
        startHand(context);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        act(p[3], CALL);
        act(p[0], RAISE, 40);

        act(p[1], FOLD);
        act(p[2], FOLD);
        act(p[3], FOLD);

        // Then the big blind rejects
        startHand(context);
        act(p[2], SMALL_BLIND);
        act(p[3], PokerActionType.DECLINE_ENTRY_BET);
        act(p[0], BIG_BLIND);

        act(p[1], RAISE, 40);
        act(p[2], FOLD);
        act(p[0], FOLD);

        assertEquals(MissedBlindsStatus.MISSED_BIG_BLIND_AND_SMALL_BLIND, p[3].getMissedBlindsStatus());
        assertFalse(p[3].hasPostedEntryBet());

        // The guy who missed the big comes back. He can't play this round though, because he's between the dealer button and the small blind.
        p[3].sitIn();
        p[3].setSitOutNextRound(false);

        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);

        act(p[2], RAISE, 40);
        act(p[0], FOLD);
        act(p[1], FOLD);

        // Now, the dealer button will be on p0, the small blind will be on p1 and bb on p2. p3 can join if he posts bb+sb
        assertTrue(p[3].isSittingIn());
        startHand(context);
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        // Player 3 posts the bb + the dead small.
        long balanceBefore = p[3].getBalance();
        act(p[3], BIG_BLIND_PLUS_DEAD_SMALL_BLIND);

        // Now he should have no missed blinds.
        assertEquals(MissedBlindsStatus.NO_MISSED_BLINDS, p[3].getMissedBlindsStatus());

        // He should have bb+sb less in his account.
        int bbPlusSbCost = context.getSettings().getBigBlindAmount() + context.getSettings().getSmallBlindAmount();
        assertTrue(bbPlusSbCost > 0);
        assertEquals(balanceBefore - bbPlusSbCost, p[3].getBalance());

        // It's p3s turn again. He checks
        act(p[3], CHECK);

        // The small blind should be dead, meaning the next player should only have to call a normal big blind.
        assertEquals(context.getSettings().getBigBlindAmount(), p[0].getActionRequest().getOption(CALL).getMinAmount());

        act(p[0], CALL, 20);
        act(p[1], CALL, 10);
        act(p[2], CHECK);

        // All players call. Pot should be 4 big blinds + dead small blind = 4 * 20 + 10 = 90.
        assertEquals(90, context.getTotalPotSize());
    }

    @Test
    public void testDeadSmallBlind() {
        PokerContext context = prepareContext(4);
        startHand(context);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        act(p[3], CALL);
        act(p[0], RAISE, 40);

        act(p[1], FOLD);
        act(p[2], FOLD);
        act(p[3], FOLD);

        // Then the small blind rejects
        startHand(context);
        act(p[2], DECLINE_ENTRY_BET);
        act(p[3], BIG_BLIND);
        act(p[0], FOLD);

        act(p[1], RAISE, 40);
        act(p[0], FOLD);
        act(p[3], FOLD);

        assertEquals(MissedBlindsStatus.MISSED_SMALL_BLIND, p[2].getMissedBlindsStatus());
        assertFalse(p[2].hasPostedEntryBet());

        // The guy who missed the big comes back.
        p[2].sitIn();
        p[2].setSitOutNextRound(false);

        // He can't play this hand though, because you can't sit in on the dealer button.
        startHand(context);

        act(p[3], SMALL_BLIND);
        act(p[0], BIG_BLIND);

        act(p[1], RAISE, 40);
        act(p[3], FOLD);
        act(p[0], FOLD);

        // This hand he can play.
        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);

        long balanceBefore = p[2].getBalance();

        // Player 2 posts the dead small.
        act(p[2], DEAD_SMALL_BLIND);

        // He should have sb less in his account.
        int sbCost = context.getSettings().getSmallBlindAmount();
        assertTrue(sbCost > 0);
        assertEquals(balanceBefore - sbCost, p[2].getBalance());

        // The small blind should be dead meaning p2 has to call a full bb.
        assertEquals(context.getSettings().getBigBlindAmount(), p[2].getActionRequest().getOption(CALL).getMinAmount());
        act(p[2], CALL);

        act(p[3], RAISE, 40);
        act(p[0], FOLD);
        act(p[1], FOLD);
        act(p[2], CALL);

        // Now he should have no missed blinds.
        assertEquals(MissedBlindsStatus.NO_MISSED_BLINDS, p[3].getMissedBlindsStatus());

        // Pot should be 2 * 40 + blinds + dead small blind = 2 * 40 + 20 + 10 + 10 = 120.
        assertEquals(120, context.getTotalPotSize());
    }


    /**
     * If you miss the big blind, you need to pay bb+dead sb to re-enter the game.
     *
     * However, if you wait until the bb comes around to you, you only need to pay the bb.
     */
    @Test
    public void testPlayerWhoMissesBigAndComesBackOnBigDoesNotPayDeadSmall() {
        PokerContext context = prepareContext(4);
        startHand(context);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        act(p[3], CALL);
        act(p[0], RAISE, 40);

        act(p[1], FOLD);
        act(p[2], FOLD);
        act(p[3], FOLD);

        verify(listener, times(1)).handFinished(Matchers.<HandResult>any(), Matchers.<HandEndStatus>any());

        // Then the big blind rejects
        startHand(context);
        act(p[2], SMALL_BLIND);
        act(p[3], PokerActionType.DECLINE_ENTRY_BET);
        act(p[0], BIG_BLIND);

        act(p[1], RAISE, 40);
        act(p[2], FOLD);
        act(p[0], FOLD);

        verify(listener, times(2)).handFinished(Matchers.<HandResult>any(), Matchers.<HandEndStatus>any());

        // Player 3 sits in again.
        p[3].sitIn();
        p[3].setSitOutNextRound(false);

        // But can't play this hand.
        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);

        act(p[2], RAISE, 40);
        act(p[0], FOLD);
        act(p[1], FOLD);

        verify(listener, times(3)).handFinished(Matchers.<HandResult>any(), Matchers.<HandEndStatus>any());

        // Now p3 is asked to post big + dead sb but rejects.
        startHand(context);
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        assertTrue(p[3].getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND));
        act(p[3], DECLINE_ENTRY_BET);
        act(p[0], RAISE, 40);
        act(p[1], FOLD);
        act(p[2], FOLD);

        verify(listener, times(4)).handFinished(Matchers.<HandResult>any(), Matchers.<HandEndStatus>any());

        // In the next hand, player 3 is BB. He shouldn't have to post a dead sb.
        p[3].sitIn();
        p[3].setSitOutNextRound(false);
        startHand(context);
        act(p[2], SMALL_BLIND);
        assertTrue(p[3].getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND));
        act(p[3], BIG_BLIND);

        act(p[0], RAISE, 40);
        act(p[1], FOLD);
        act(p[2], FOLD);
        act(p[3], FOLD);
        verify(listener, times(5)).handFinished(Matchers.<HandResult>any(), Matchers.<HandEndStatus>any());
    }

    @Test
    public void testHandResultForFlushWithKicker() {
        createPot();
        prepareContext(player1, player2);
        // This is the scenario we want to set up, there are 4 clubs on the board, and the two players have one low club on their hand each.

        // So, given:
        when(context.getCommunityCards()).thenReturn(new Hand("8C 6D 9C AC 5C").getCards());
        when(context.getPlayer(101)).thenReturn(player1);
        when(context.getPlayer(102)).thenReturn(player2);
        player1.setPocketCards(new Hand("QS 3C"));
        player2.setPocketCards(new Hand("6C 9D"));

        // When:
        texas.handleFinishedHand();
        ArgumentCaptor<HandResult> captor = ArgumentCaptor.forClass(HandResult.class);
        verify(listener).handFinished(captor.capture(), eq(HandEndStatus.NORMAL));

        // Then: player2 should win, because his 6 of clubs is higher than player1's 3 of clubs in the flushes.
        HandResult handResult = captor.getValue();
        Result result = handResult.getResults().get(player2);
        RatedPlayerHand ratedPlayerHand = handResult.getPlayerHands().get(0);

        assertEquals(1200L, result.getWinningsIncludingOwnBets());
        assertEquals(Integer.valueOf(102), ratedPlayerHand.getPlayerId());
        assertThat(ratedPlayerHand.getBestHandCards(), is(new Hand("AC 9C 8C 6C 5C").getCards()));
        assertEquals(HandType.FLUSH, ratedPlayerHand.getBestHandType());
    }

    @Test
    public void testAllInOnSmallBlind() {
        PokerContext context = prepareContext(4);
        startHand(context);

        // The small blind costs 5, so let the small blind only have 3 chips left.
        p[1].setBalance(3);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        assertTrue(p[1].isAllIn());
    }

    @Test
    public void testShowdown() {
        PokerContext context = prepareContext(3);
        texas.setRevealOrderCalculator(revealOrderCalculator);
        when(revealOrderCalculator.calculateRevealOrder(Matchers.<SortedMap<Integer, PokerPlayer>>any(),Matchers.<PokerPlayer>any(),Matchers.<PokerPlayer>any(), anyInt())).
                thenReturn(Lists.newArrayList(102, 100, 101));

        startHand(context);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        // pre
        act(p[0], CALL);
        act(p[1], CALL);
        act(p[2], CHECK);

        texas.timeout();

        // flop
        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        texas.timeout();

        // turn
        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        texas.timeout();

        // river
        act(p[1], CHECK);
        act(p[2], BET, 40);
        act(p[0], CALL);
        act(p[1], CALL);

        ArgumentCaptor<ExposeCardsHolder> captor = ArgumentCaptor.forClass(ExposeCardsHolder.class);
        verify(serverAdapter).exposePrivateCards(captor.capture());
        ExposeCardsHolder holder = captor.getValue();
        assertEquals(102, holder.getExposedCards().get(0).getPlayerId());
    }

    @Test
    public void testAllInOnBigBlind() {
        PokerContext context = prepareContext(4);
        startHand(context);

        // The big blind costs 10, so let the small blind only have 7 chips left.
        p[2].setBalance(7);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        assertTrue(p[2].isAllIn());
    }

    @Test
    public void testPlayerAfterAllInBigBlindStillCallsFullBigBlind() {
        PokerContext context = prepareContext(4);
        startHand(context);

        p[2].setBalance(7);

        // First play a normal hand.
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        assertEquals(20, p[3].getActionRequest().getOption(CALL).getMinAmount());
    }

    private void createPot() {
        potHolder.getActivePot().bet(player1, 600L);
        potHolder.getActivePot().bet(player2, 600L);
    }

    private void startHand(PokerContext context) {
        context.prepareHand(readyPlayersFilter);
        texas.startHand();
    }

    private void act(MockPlayer player, PokerActionType actionType) {
        texas.act(new PokerAction(player.getId(), actionType));
    }

    private void act(MockPlayer player, PokerActionType actionType, int value) {
        texas.act(new PokerAction(player.getId(), actionType, value));
    }

    private PokerContext prepareContext(int numberOfPlayers) {
        PokerSettings settings = new PokerSettings(10, 10, 20, 100, 5000, new DefaultTimingProfile(), 6, BetStrategyName.NO_LIMIT, rakeSettings, null);
        PokerContext context = new PokerContext(settings);
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        p = TestUtils.createMockPlayers(numberOfPlayers);
        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(true);
            context.addPlayer(player);
        }
        return context;
    }

}
