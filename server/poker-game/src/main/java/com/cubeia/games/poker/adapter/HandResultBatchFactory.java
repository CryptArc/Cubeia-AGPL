package com.cubeia.games.poker.adapter;

import java.util.Map;

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class HandResultBatchFactory {

    public BatchHandRequest createBatchHandRequest(HandResult handResult, long handId, TableId tableId) {
        BatchHandRequest bhr = new BatchHandRequest(handId, tableId);
        
        for (Map.Entry<PokerPlayer, Result> resultEntry : handResult.getResults().entrySet()) {
            PokerPlayerImpl player = (PokerPlayerImpl) resultEntry.getKey();
            Result result = resultEntry.getValue();
            
            long bets = result.getWinningsIncludingOwnBets() - result.getNetResult();
            long rake = 0; // TODO: unsupported, must add to HandResult
            com.cubeia.backend.cashgame.dto.HandResult hr = new com.cubeia.backend.cashgame.dto.HandResult(
                player.getPlayerSessionId(), bets, result.getNetResult(), rake);
            bhr.addHandResult(hr);
        }
        
        return bhr;
    }

}
