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

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.Money;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.cubeia.games.poker.handler.BackendPlayerSessionHandler.DEFAULT_ZERO_MONEY;

public class HandResultBatchFactory {

    private final Logger log = Logger.getLogger(getClass());

    public BatchHandRequest createAndValidateBatchHandRequest(HandResult handResult, String handId, TableId tableId) {
        long totalBet = 0;
        long totalNet = 0;
        long totalRake = 0;
        BatchHandRequest bhr = new BatchHandRequest(handId, tableId,
                DEFAULT_ZERO_MONEY.add(handResult.getTotalRake()));
        for (Map.Entry<PokerPlayer, Result> resultEntry : handResult.getResults().entrySet()) {
            PokerPlayerImpl player = (PokerPlayerImpl) resultEntry.getKey();
            Result result = resultEntry.getValue();
            Money bets = DEFAULT_ZERO_MONEY.add(result.getWinningsIncludingOwnBets() - result.getNetResult());
            Money wins = DEFAULT_ZERO_MONEY.add(result.getWinningsIncludingOwnBets());
            Money rake = DEFAULT_ZERO_MONEY.add(handResult.getRakeContributionByPlayer(player));
            Money net = DEFAULT_ZERO_MONEY.add(result.getNetResult());
            Money startingBalanceMoney = DEFAULT_ZERO_MONEY.add(player.getStartingBalance());
            log.debug("Result for player " + player.getId() + " -> Bets: " + bets + "; Wins: " + wins + "; Rake: " + rake + "; Net: " + net);
            com.cubeia.backend.cashgame.dto.HandResult hr = new com.cubeia.backend.cashgame.dto.HandResult(
                    player.getPlayerSessionId(), bets, wins, rake, player.getSeatId(), startingBalanceMoney); // TODO Add initial balance?
            bhr.addHandResult(hr);
            totalBet += bets.getAmount();
            totalNet += net.getAmount();
            totalRake += rake.getAmount();
        }
        if ((totalNet + totalRake) != 0) {
            throw new IllegalStateException("Unbalanced hand result ((" + totalNet + " + " + totalRake + ") != 0); Total bet: " + totalBet + "; " + handResult);
        }
        return bhr;
    }

}
