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

package com.cubeia.games.poker.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.TableIdImpl;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.games.poker.PokerConfigServiceMock;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class HandResultBatchFactoryTest {

    @Test
    public void testCreateBatchHandRequest() {
        HandResultBatchFactory handResultFactory = new HandResultBatchFactory();
        handResultFactory.configService = new PokerConfigServiceMock();
        String handId = "55555";

        int playerId1 = 22;
        PlayerSessionId playerSessionId1 = new PlayerSessionIdImpl(playerId1);
        PokerPlayerImpl pokerPlayer1 = mock(PokerPlayerImpl.class);
        when(pokerPlayer1.getId()).thenReturn(playerId1);
        when(pokerPlayer1.getPlayerSessionId()).thenReturn(playerSessionId1);

        int playerId2 = 33; 
        PlayerSessionId playerSessionId2 = new PlayerSessionIdImpl(playerId2);
        PokerPlayerImpl pokerPlayer2 = mock(PokerPlayerImpl.class);
        when(pokerPlayer2.getId()).thenReturn(playerId2); 
        when(pokerPlayer2.getPlayerSessionId()).thenReturn(playerSessionId2);

        TableId tableId = new TableIdImpl();

        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        Result result1 = new Result(980, 1000, Collections.<Pot, Long>emptyMap());
        Result result2 = new Result(-1000, 1000, Collections.<Pot, Long>emptyMap());
        results.put(pokerPlayer1, result1);
        results.put(pokerPlayer2, result2);

        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(1000 * 2, (1000 * 2) / 100, new HashMap<Pot, Long>());
        HandResult handResult = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());

        BatchHandRequest batchHandRequest = handResultFactory.createAndValidateBatchHandRequest(handResult, handId, tableId);

        assertThat(batchHandRequest, notNullValue());
        assertThat(batchHandRequest.getHandId(), is(handId));
        assertThat(batchHandRequest.getTableId(), is(tableId));
        assertThat(batchHandRequest.getHandResults().size(), is(2));

        com.cubeia.backend.cashgame.dto.HandResult hr1 = findByPlayerSessionId(playerSessionId1, batchHandRequest.getHandResults());
        assertThat(hr1.getAggregatedBet().getAmount(), is(result1.getWinningsIncludingOwnBets() - result1.getNetResult()));
        assertThat(hr1.getAggregatedBet().getCurrencyCode(), is("EUR"));
        assertThat(hr1.getAggregatedBet().getFractionalDigits(), is(2));
        assertThat(hr1.getWin().getAmount(), is(result1.getWinningsIncludingOwnBets()));
        assertThat(hr1.getRake().getAmount(), is(1000L / 100));
        assertThat(hr1.getPlayerSession(), is(playerSessionId1));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateUnbalancedBatchHandRequest() {
        HandResultBatchFactory handResultFactory = new HandResultBatchFactory();
        handResultFactory.configService = new PokerConfigServiceMock();
        String handId = "55555";

        int playerId1 = 22;
        PlayerSessionId playerSessionId1 = new PlayerSessionIdImpl(playerId1);
        PokerPlayerImpl pokerPlayer1 = mock(PokerPlayerImpl.class);
        when(pokerPlayer1.getId()).thenReturn(playerId1);
        when(pokerPlayer1.getPlayerSessionId()).thenReturn(playerSessionId1);

        int playerId2 = 33;
        PlayerSessionId playerSessionId2 = new PlayerSessionIdImpl(playerId2);
        PokerPlayerImpl pokerPlayer2 = mock(PokerPlayerImpl.class);
        when(pokerPlayer2.getId()).thenReturn(playerId2);
        when(pokerPlayer2.getPlayerSessionId()).thenReturn(playerSessionId2);

        TableId tableId = new TableIdImpl();

        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        Result result1 = new Result(981, 1000, Collections.<Pot, Long>emptyMap());
        Result result2 = new Result(-1000, 1000, Collections.<Pot, Long>emptyMap());
        results.put(pokerPlayer1, result1);
        results.put(pokerPlayer2, result2);

        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(1000 * 2, (1000 * 2) / 100, new HashMap<Pot, Long>());
        HandResult handResult = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());

        handResultFactory.createAndValidateBatchHandRequest(handResult, handId, tableId);
    }


    @Test
    public void testCreateHandBatch() {
        HandResultBatchFactory handResultFactory = new HandResultBatchFactory();
        handResultFactory.configService = new PokerConfigServiceMock();
        String handId = "12345";

        PokerPlayerImpl pokerPlayer8 = createMockPlayer(8);
        PokerPlayerImpl pokerPlayer2 = createMockPlayer(2);
        PokerPlayerImpl pokerPlayer0 = createMockPlayer(0);
        PokerPlayerImpl pokerPlayer5 = createMockPlayer(5);
        PokerPlayerImpl pokerPlayer9 = createMockPlayer(9);

        TableId tableId = new TableIdImpl();

        Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
        Result result8 = new Result(-26, 26, Collections.<Pot, Long>emptyMap());

        Pot pot = new Pot(0);
        pot.bet(pokerPlayer8, 26L);
        pot.bet(pokerPlayer2, 146L);
        pot.bet(pokerPlayer0, 14L);
        pot.bet(pokerPlayer5, 2L);
        pot.bet(pokerPlayer9, 331L);
        Map<Pot, Long> pots = new HashMap<Pot, Long>();
        pots.put(pot, 331L);
        Result result2 = new Result(185, 146, pots);

        Result result0 = new Result(-14, 14, Collections.<Pot, Long>emptyMap());
        Result result5 = new Result(-2, 2, Collections.<Pot, Long>emptyMap());
        Result result9 = new Result(-146, 146, Collections.<Pot, Long>emptyMap());

        results.put(pokerPlayer8, result8);
        results.put(pokerPlayer2, result2);
        results.put(pokerPlayer0, result0);
        results.put(pokerPlayer5, result5);
        results.put(pokerPlayer9, result9);

        HashMap<Pot, Long> potRakes = new HashMap<Pot, Long>();
        potRakes.put(pot, 3L);
        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(334, 3, potRakes);
        HandResult handResult = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>());

        handResultFactory.createAndValidateBatchHandRequest(handResult, handId, tableId);
    }

    private PokerPlayerImpl createMockPlayer(int playerId) {
        PlayerSessionId playerSessionId = new PlayerSessionIdImpl(playerId);
        PokerPlayerImpl pokerPlayer1 = mock(PokerPlayerImpl.class);
        when(pokerPlayer1.getId()).thenReturn(playerId);
        when(pokerPlayer1.getPlayerSessionId()).thenReturn(playerSessionId);
        return pokerPlayer1;
    }


    private com.cubeia.backend.cashgame.dto.HandResult findByPlayerSessionId(PlayerSessionId playerSessionId,
                                                                             List<com.cubeia.backend.cashgame.dto.HandResult> handResults) {
        for (com.cubeia.backend.cashgame.dto.HandResult hr : handResults) {
            if (hr.getPlayerSession().equals(playerSessionId)) {
                return hr;
            }
        }
        return null;
    }

}
