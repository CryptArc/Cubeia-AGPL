package com.cubeia.games.poker.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class HandResultBatchFactoryTest {

    @Test
    public void testCreateBatchHandRequest() {
        HandResultBatchFactory handResultFactory = new HandResultBatchFactory();
        long handId = 55555;
        
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
        Result result1 = new Result(2000, 1000, Collections.<Pot, Long>emptyMap());
        Result result2 = new Result(-1000, 1000, Collections.<Pot, Long>emptyMap());
        results.put(pokerPlayer1, result1);
        results.put(pokerPlayer2, result2);
        
        RakeInfoContainer rakeInfoContainer = new RakeInfoContainer(1000 * 2, (1000 * 2) / 100, new HashMap<Pot, BigDecimal>());
        HandResult handResult = new HandResult(results, Collections.<RatedPlayerHand>emptyList(), Collections.<PotTransition>emptyList(), rakeInfoContainer, new ArrayList<Integer>() );
        
        BatchHandRequest batchHandRequest = handResultFactory.createBatchHandRequest(handResult, handId, tableId);
        
        assertThat(batchHandRequest, notNullValue());
        assertThat(batchHandRequest.handId, is(handId));        
        assertThat(batchHandRequest.tableId, is(tableId));   
        assertThat(batchHandRequest.handResults.size(), is(2));
        
        com.cubeia.backend.cashgame.dto.HandResult hr1 = findByPlayerSessionId(playerSessionId1, batchHandRequest.handResults);
        assertThat(hr1.aggregatedBet, is(result1.getWinningsIncludingOwnBets() - result1.getNetResult()));
        assertThat(hr1.win, is(result1.getWinningsIncludingOwnBets()));
        assertThat(hr1.rake, is(1000L / 100));
        assertThat(hr1.playerSession, is(playerSessionId1));
    }

    private com.cubeia.backend.cashgame.dto.HandResult findByPlayerSessionId(PlayerSessionId playerSessionId,
        List<com.cubeia.backend.cashgame.dto.HandResult> handResults) {
        for (com.cubeia.backend.cashgame.dto.HandResult hr : handResults) {
            if (hr.playerSession.equals(playerSessionId)) {
                return hr;
            }
        }
        return null;
    }

}
