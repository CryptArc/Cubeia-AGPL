package com.cubeia.games.poker.adapter;

import java.util.Map;

import org.apache.log4j.Logger;

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class HandResultBatchFactory {
	
	private final Logger log = Logger.getLogger(getClass());

    public BatchHandRequest createAndValidateBatchHandRequest(HandResult handResult, long handId, TableId tableId) {
    	long totalBet = 0;
    	long totalNet = 0;
    	long totalRake = 0;
        BatchHandRequest bhr = new BatchHandRequest(handId, tableId, handResult.getTotalRake());
        for (Map.Entry<PokerPlayer, Result> resultEntry : handResult.getResults().entrySet()) {
            PokerPlayerImpl player = (PokerPlayerImpl) resultEntry.getKey();
            Result result = resultEntry.getValue();
            long bets = result.getWinningsIncludingOwnBets() - result.getNetResult();
            long wins = result.getWinningsIncludingOwnBets();
            long rake = handResult.getRakeContributionByPlayer(player);
            long net = result.getNetResult();
            log.debug("Result for player " + player.getId() + " -> Bets: " + bets + "; Wins: " + wins + "; Rake: " + rake + "; Net: " + net);
            com.cubeia.backend.cashgame.dto.HandResult hr = new com.cubeia.backend.cashgame.dto.HandResult(player.getPlayerSessionId(), bets, wins, rake, player.getSeatId(), player.getStartingBalance()); // TODO Add initial balance?
            bhr.addHandResult(hr);
            totalBet += bets;
            totalNet += net;
            totalRake += rake;
        }
        if((totalNet + totalRake) != 0) {
        	throw new IllegalStateException("Unbalanced hand result ((" + totalNet + " + " + totalRake + ") != 0); Total bet: " + totalBet+ "; " + handResult);
        }
        return bhr;
    }

}
