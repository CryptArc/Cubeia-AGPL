/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.tournament.payouts;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.cubeia.games.poker.tournament.configuration.payouts.Payouts;

public class PayoutHandler implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(PayoutHandler.class);

    private Payouts payouts;

    public PayoutHandler(Payouts payouts) {
        this.payouts = payouts;
    }

    /**
     * Takes a set of players who are out and returns a map of playerId->payout (in cents).
     *
     * If more than one player is out, the player with the most chips at the beginning of the hand will get the higher prize.
     *
     * If two or more players had the same balance at the start of the hand, they will share the same
     * position and share those prizes.
     *
     *
     * @param playerIds
     * @param playersLeft
     * @return a map mapping playerIds to the number of cents that player should get
     */
    public List<ConcretePayout> calculatePayouts(Set<Integer> playerIds, Map<Integer, Long> balancesAtStartOfHand, int playersLeft) {
        log.debug("Players out: " + playerIds + ". Players left: " + playersLeft + ". Balances at start of hand: " + balancesAtStartOfHand);
        List<ConcretePayout> concretePayouts = newArrayList();

        SortedMap<Long, List<Integer>> groupedByStartingChips = groupPlayersByChipsAtStartOfHand(balancesAtStartOfHand);
        log.debug("Grouped by starting chips: " + groupedByStartingChips);
        for (Map.Entry<Long, List<Integer>> playersWithBalance : groupedByStartingChips.entrySet()) {
            List<Integer> players = playersWithBalance.getValue();

            if (players.size() == 1) {
                int payoutsForPosition = payouts.getPayoutsForPosition(playersLeft);
                Integer playerId = players.iterator().next();
                log.debug("Player " + playerId + " finished in position " + playersLeft + " and won " + payoutsForPosition);
                concretePayouts.add(new ConcretePayout(playerId, playersLeft, payoutsForPosition));
                playersLeft--;
            } else {
                concretePayouts.addAll(splitPrizesBetween(players, playersLeft));
                playersLeft -= players.size();
            }
        }
        return concretePayouts;
    }

    private List<ConcretePayout> splitPrizesBetween(List<Integer> players, int playersLeft) {
        List<ConcretePayout> playerIdToPrize = newArrayList();
        int totalPrizeToShare = 0;
        for (int i = 0; i < players.size(); i++) {
            totalPrizeToShare += payouts.getPayoutsForPosition(playersLeft--);
        }
        log.debug("Total prize to share: " + totalPrizeToShare);
        int prizePerPlayer = totalPrizeToShare / players.size();
        log.debug("Prize per player: " + prizePerPlayer);
        int sharedPosition = playersLeft + 1;
        for (Integer playerId : players) {
            log.debug("Player " + playerId + " finished in split position " + sharedPosition + " and won " + prizePerPlayer);
            playerIdToPrize.add(new ConcretePayout(playerId, sharedPosition, prizePerPlayer));
        }
        long remainder = totalPrizeToShare - prizePerPlayer * players.size();
        distributeRemainder(remainder, playerIdToPrize);
        return playerIdToPrize;
    }

    private void distributeRemainder(long remainder, List<ConcretePayout> payouts) {
        while (remainder > 0) {
            int index = (int) remainder % payouts.size();
            log.debug("Player index to get part of the remainder: " + index);
            ConcretePayout payout = payouts.get(index);
            payout.setPayoutInCents(payout.getPayoutInCents() + 1);
            remainder--;
        }
    }

    private SortedMap<Long, List<Integer>> groupPlayersByChipsAtStartOfHand(Map<Integer, Long> balancesAtStartOfHand) {
        SortedMap<Long, List<Integer>> chipsAtStartOfHandToListOfPlayers = newTreeMap();
        for (Map.Entry<Integer, Long> balance : balancesAtStartOfHand.entrySet()) {
            List<Integer> playersWithThisBalance = chipsAtStartOfHandToListOfPlayers.get(balance.getValue());
            if (playersWithThisBalance == null) {
                playersWithThisBalance = newArrayList();
            }
            playersWithThisBalance.add(balance.getKey());
            chipsAtStartOfHandToListOfPlayers.put(balance.getValue(), playersWithThisBalance);
        }
        return chipsAtStartOfHandToListOfPlayers;
    }
}
