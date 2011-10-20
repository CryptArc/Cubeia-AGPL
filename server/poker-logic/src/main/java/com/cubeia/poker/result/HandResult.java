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

package com.cubeia.poker.result;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;

/**
 * The result of a hand. This class maps the player to the resulting win/lose amount of the hand.
 */
public class HandResult implements Serializable {

	private static final int SCALE = 10;

    private static final long serialVersionUID = -7802386310901901021L;

	private final Map<PokerPlayer, Result> results;

	private final List<RatedPlayerHand> playerHands;
	
	private final Collection<PotTransition> potTransitions;

    private Map<PokerPlayer, Long> rakeContributions;
    
    private final long totalRake;

	public HandResult() {
	    this(Collections.<PokerPlayer, Result>emptyMap(), Collections.<RatedPlayerHand>emptyList(), 
	        Collections.<PotTransition>emptyList(), null);
	}
	
	public HandResult(
	    Map<PokerPlayer, Result> results, 
	    List<RatedPlayerHand> playerHands, 
	    Collection<PotTransition> potTransitions, RakeInfoContainer rakeInfoContainer) {
	    
		this.totalRake = (rakeInfoContainer == null ? -1 : rakeInfoContainer.getTotalRake());
	    this.results = unmodifiableMap(results);
	    this.playerHands = unmodifiableList(playerHands);
        this.potTransitions = Collections.unmodifiableCollection(potTransitions);
        this.rakeContributions = rakeInfoContainer == null 
            ? Collections.<PokerPlayer, Long>emptyMap() 
            : calculateRakeContributions(rakeInfoContainer, results);
	}
	
	public List<RatedPlayerHand> getPlayerHands() {
	    return playerHands;
	}

    public Map<PokerPlayer, Result> getResults() {
		return results;
	}
    
    public long getTotalRake() {
		return totalRake;
	}
    
//    /**
//     * Fetch all pots in this hand by the result map.
//     * @return all pots in this hand
//     */
//    @VisibleForTesting
//    private Set<Pot> extractPots(Collection<Result> results) {
//        HashSet<Pot> pots = new HashSet<Pot>();
//        for (Result result : results) {
//            pots.addAll(result.getWinningsByPot().keySet());
//        }
//        return pots;
//    }
    
    /**
     * Calculate the rake contribution by player.
     * The sum of all rake contributions equals the total rake taken for this hand.
     * The rake contribution for a player is calculated as:
     * <code>
     *   contrib = total_rake * player_bets / total_bets
     * </code>
     * @return player to rake contribution map
     */
    private Map<PokerPlayer, Long> calculateRakeContributions(RakeInfoContainer rakeInfoContainer, Map<PokerPlayer, Result> results) {
        Map<PokerPlayer, Long> rakeContribs = new HashMap<PokerPlayer, Long>();
        
        BigDecimal totalBetsBD = new BigDecimal(rakeInfoContainer.getTotalPot());
        
        for (Map.Entry<PokerPlayer, Result> e : results.entrySet()) {
            PokerPlayer player = e.getKey();
            Result result = e.getValue();
            BigDecimal playerBets = new BigDecimal(result.getBets());
            BigDecimal rakeContrib = totalBetsBD.signum() == 0 
                ? BigDecimal.ZERO 
                : BigDecimal.valueOf(rakeInfoContainer.getTotalRake()).multiply(playerBets).divide(totalBetsBD, SCALE, HALF_UP);
            rakeContribs.put(player, rakeContrib.longValue());
        }
        
        return rakeContribs;
    }

    public long getRakeContributionByPlayer(PokerPlayer player) {
        return rakeContributions.get(player);
    }
    
    public Collection<PotTransition> getPotTransitions() {
        return potTransitions;
    }

	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    for (RatedPlayerHand rph : playerHands) {
	        sb.append("Player ");
	        sb.append(rph.getPlayerId());
	        sb.append(" best hand: ");
	        sb.append(cardsToString(rph.getBestHandCards()));
	        sb.append(". ");
	    }
		return "HandResult results["+results+"] Hands: " + sb.toString();
	}

    private String cardsToString(List<Card> bestHandCards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : bestHandCards) {
            sb.append(card.toString());
            sb.append(" ");
        }
        return sb.toString();
    }
}
