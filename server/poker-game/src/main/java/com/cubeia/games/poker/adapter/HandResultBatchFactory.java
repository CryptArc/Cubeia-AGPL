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
    	long totalBet = 0;
    	long totalWin = 0;
    	long totalRake = 0;
        BatchHandRequest bhr = new BatchHandRequest(handId, tableId, handResult.getTotalRake());
        for (Map.Entry<PokerPlayer, Result> resultEntry : handResult.getResults().entrySet()) {
            PokerPlayerImpl player = (PokerPlayerImpl) resultEntry.getKey();
            Result result = resultEntry.getValue();
            long bets = result.getWinningsIncludingOwnBets() - result.getNetResult();
            long wins = result.getWinningsIncludingOwnBets();
            long rake = handResult.getRakeContributionByPlayer(player);
            com.cubeia.backend.cashgame.dto.HandResult hr = new com.cubeia.backend.cashgame.dto.HandResult(player.getPlayerSessionId(), bets, wins, rake, player.getSeatId(), -1); // TODO Add initial balance?
            bhr.addHandResult(hr);
            totalBet += bets;
            long net = result.getNetResult();
            if(net >= 0) {
            	totalWin += net;
            }
            totalRake += rake;
        }
        if((totalBet - totalRake) != totalWin) {
        	throw new IllegalStateException("Unbalanced hand result ((" + totalBet + " - " + totalRake + ") != " + totalWin + "); " + handResult);
        }
        return bhr;
    }

}
