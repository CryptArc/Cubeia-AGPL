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

import com.cubeia.poker.DummyRNGProvider;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.HandFinishedListener;
import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.cubeia.poker.action.PokerActionType.*;
import static com.cubeia.poker.action.PokerActionType.FOLD;
import static com.cubeia.poker.util.TestHelpers.assertSameListsDisregardingOrder;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TexasHoldemTest {

    private TexasHoldem texas;

    @Mock
    private PokerContext context;
    @Mock
    private RNGProvider rngProvider;
    @Mock
    private BettingRound bettingRound;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private HandFinishedListener listener;

    private MockPlayer player1;

    private MockPlayer player2;

    private PotHolder potHolder;

    private TreeMap<Integer, PokerPlayer> seatingMap;

    private Map<Integer, PokerPlayer> playerMap;

    private RakeSettings rakeSettings;

    private Predicate<PokerPlayer> readyPlayersFilter = new Predicate<PokerPlayer>() {
        @Override
        public boolean apply(@Nullable PokerPlayer pokerPlayer) {
            return true;
        }
    };
    private MockPlayer[] p;

    @Before
    public void setup() {
        initMocks(this);
        rakeSettings = new RakeSettings(new BigDecimal("0.06"), 500, 150);
        potHolder = new PotHolder(new LinearRakeWithLimitCalculator(rakeSettings));
        when(context.getPotHolder()).thenReturn(potHolder);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);


        texas = new TexasHoldem(new DummyRNGProvider());
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        texas.addHandFinishedListener(listener);

        player1 = new MockPlayer(1);
        player2 = new MockPlayer(2);

        createPot();
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
        act(p[0], RAISE, 20);

        act(p[1], FOLD);
        act(p[2], FOLD);
        act(p[3], FOLD);

        // Then the big blind rejects
        startHand(context);
        act(p[2], SMALL_BLIND);
        act(p[3], PokerActionType.DECLINE_ENTRY_BET);
        act(p[0], BIG_BLIND);

        act(p[1], RAISE, 20);
        act(p[2], FOLD);
        act(p[0], FOLD);

        assertEquals(SitOutStatus.MISSED_BIG_BLIND, p[3].getSitOutStatus());
        assertFalse(p[3].hasPostedEntryBet());

        // The guy who missed the big comes back. He can't play this round though, because he's between the dealer button and the small blind.
        p[3].sitIn();
        p[3].setSitOutNextRound(false);

        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);

        act(p[2], RAISE, 20);
        act(p[0], FOLD);
        act(p[1], FOLD);

        // Now, the dealer button will be on p0, the small blind will be on p1 and bb on p2. p3 can join if he posts bb+sb
        startHand(context);
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        // SHOULD BE BIG+SMALL
        act(p[3], BIG_PLUS_SMALL);
        assertEquals(SitOutStatus.NO_MISSED_BLINDS, p[3].getSitOutStatus());
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
        PokerSettings settings = new PokerSettings(10, 10, 100, 5000, new DefaultTimingProfile(), 6, BetStrategyName.NO_LIMIT, rakeSettings, null);
        PokerContext context = new PokerContext(settings);
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        p = TestUtils.createMockPlayers(numberOfPlayers);
        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(true);
            context.addPlayer(player);
        }
        return context;
    }

    @Test
    public void testHandResultForFlushWithKicker() {
        prepareContext(player1, player2);
        // This is the scenario we want to set up, there are 4 clubs on the board, and the two players have one low club on their hand each.

        // So, given:
        when(context.getCommunityCards()).thenReturn(new Hand("8C 6D 9C AC 5C").getCards());
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
        assertSameListsDisregardingOrder(new Hand("6C 8C 9C AC 5C").getCards(), ratedPlayerHand.getBestHandCards());
        assertEquals(HandType.FLUSH, ratedPlayerHand.getBestHandType());
    }

    private void createPot() {
        potHolder.getActivePot().bet(player1, 600L);
        potHolder.getActivePot().bet(player2, 600L);
    }

}
