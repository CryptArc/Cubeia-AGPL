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

package com.cubeia.poker.pot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rake.RakeCalculator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * holds all the active pots for a table  
 */
public class PotHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(PotHolder.class);
	/**
	 * Holds all pots.
	 */
	private List<Pot> pots = new ArrayList<Pot>();

	private Set<Integer> allInPlayers = new HashSet<Integer>();

    private final RakeCalculator rakeCalculator;

	public PotHolder(RakeCalculator rakeCalculator) {
        this.rakeCalculator = rakeCalculator;
    }
	
	/**
	 * Moves chips to the pot.
	 * split the chips into side pots if we have all-ins
	 * return unmatched chips to the player.
	 *
	 * @param players a collection of players
	 */
	public Collection<PotTransition> moveChipsToPot(Collection<PokerPlayer> players) {
	    
	    Collection<PotTransition> potTransitions = new ArrayList<PotTransition>();
	    
		// Maps player's to the bet they made.
		Map<PokerPlayer, Long> playerToBetMap = new HashMap<PokerPlayer, Long>();

		// Tree set of all-in levels.
		SortedSet<Long> allInLevels = new TreeSet<Long>();

		// First, return any uncalled chips.
		returnUnCalledChips(players);

		// Add all bets to the map and check if we have all-ins.
		for (PokerPlayer player : players) {
			
            // Exclude players who are already all-in.
            if (player.isAllIn() && !allInPlayers.contains(player.getId())) {
            	allInLevels.add(player.getBetStack());
            	allInPlayers.add(player.getId());
            }
            
            // Exclude players who did not bet.
            if (player.getBetStack() > 0) {
            	playerToBetMap.put(player, player.getBetStack());
            }
			
		}

		if (!allInLevels.isEmpty()) {
			// There are all-ins, split them up into side pots.
			handleAllIns(playerToBetMap, allInLevels, potTransitions);
		}

		// The remaining chips are placed in the active pot.
		for (PokerPlayer player : playerToBetMap.keySet()) {
			long stack = playerToBetMap.get(player);
			if (stack > 0) {
			    potTransitions.add(new PotTransition(player, getActivePot(), stack));
				getActivePot().bet(player, stack);
			}
		}
		
		calculateAndTakeRake(potTransitions);
		
		printDiagnostics();
		
		return potTransitions;
	}

    private void calculateAndTakeRake(Collection<PotTransition> potTransitions) {
        BigDecimal rakeBefore = getTotalRake();
        Map<Pot, BigDecimal> potRakes = rakeCalculator.calculateRakeAddition(rakeBefore, potTransitions);
		
		for (Map.Entry<Pot, BigDecimal> entry : potRakes.entrySet()) {
		    BigDecimal rake = entry.getValue();
            Pot pot = entry.getKey();
            pot.addRake(rake);
		}
    }

	private void printDiagnostics() {
        log.debug("pots: ");
        for (Pot p : pots) {
            Collection<Integer> playerIds = Collections2.transform(p.getPotContributors().keySet(), new Function<PokerPlayer, Integer>() {
                public Integer apply(PokerPlayer pp) { return pp.getId(); };
            });
            log.debug("  pot {}: bets = {}, rake = {}, open = {}, players: {}", 
                new Object[] {p.getId(), p.getPotSize(), p.getRake(), p.isOpen(), playerIds});
        }
        log.debug("{}, total pot size = {}, total rake = {}", 
            new Object[] {rakeCalculator, getTotalPotSize(), getTotalRake()});
    }

    /**
	 * Returns uncalled chips.
	 *
	 * @param betters
	 */
	public void returnUnCalledChips(Iterable<PokerPlayer> players) {
		PokerPlayer biggestBetter = getBiggestBetter(players);
		PokerPlayer secondBiggestBetter = getBiggestBetter(players, biggestBetter);

		try {
			if (biggestBetter.getBetStack() > secondBiggestBetter.getBetStack()) {
				long returnedChips = biggestBetter.getBetStack() - secondBiggestBetter.getBetStack();
				biggestBetter.addReturnedChips(returnedChips);
				
				log.debug("returning " + returnedChips + " uncalled chips to " + biggestBetter);
			}
		} catch (NullPointerException ne) {
			// FIXME: Tournaments get this exception
			log.warn("FIXME: Should not be nullpointer here! -> PotHolder.returnUnCalledChips()");
		}
	}

	/**
	 * Gets the biggest better in this round, possibly excluding one player.
	 *
	 * @param betters
	 * @param excludedPlayer the player to exclude, may be null
	 * @return the biggest better
	 */
	private PokerPlayer getBiggestBetter(Iterable<PokerPlayer> players, PokerPlayer excludedPlayer) {
		long biggestBet = -1;
		PokerPlayer biggestBetter = null;

		for (PokerPlayer player : players) {
			if (player.getBetStack() > biggestBet) {
				if (!player.equals(excludedPlayer)) {
					biggestBet = player.getBetStack();
					biggestBetter = player;
				}
			}
		}
		return biggestBetter;
	}

	/**
	 * Gets the biggest better in this round.
	 *
	 * @param betters
	 * @return
	 */
	private PokerPlayer getBiggestBetter(Iterable<PokerPlayer> players) {
		return getBiggestBetter(players, null);
	}

	/**
	 * Handles all-in bets by splitting them up into side pots.
	 *
	 * @param betMap	  a map of all bets, mapping the player to the amount bet
	 * @param allInLevels sorted set of the different levels where players went all-in
	 * @param potTransitions 
	 */
	private void handleAllIns(Map<PokerPlayer, Long> betMap, SortedSet<Long> allInLevels, Collection<PotTransition> potTransitions) {
		long currentLevel = 0;

		/*
		 * Go through each all-in level and add chips from all players who still
		 * have chips.
		 */
		for (Long allInLevel : allInLevels) {
			long diff = allInLevel - currentLevel;

			Pot activePot = getActivePot();
            for (PokerPlayer player : betMap.keySet()) {
				long stack = betMap.get(player);
				if (stack >= diff) {
				    potTransitions.add(new PotTransition(player, activePot, diff));
					activePot.bet(player, diff);
					betMap.put(player, (stack - diff));
				} else if (stack > 0) {
					/* 
					 * If a player has folded, he might not have enough chips, 
					 * add the remaining chips in this pot.
					 */
                    potTransitions.add(new PotTransition(player, activePot, stack));
					activePot.bet(player, stack);
					betMap.put(player, new Long(0));
				}
			}
			// Close the pot, so no more bets can be placed in the pot.
			activePot.close();

			// Update the current level.
			currentLevel = allInLevel;
		}
	}

	/**
	 * Gets the active pot.
	 * <p/>
	 * Creates a new pot if there are no pots.
	 *
	 * @return the active pot, or a newly created pot if there were no pots
	 */
	public Pot getActivePot() {
		if (pots.size() == 0 || !pots.get(pots.size() - 1).isOpen() ) {
			Pot pot = new Pot(pots.size());
			pots.add(pot);
		}
		return (pots.get(pots.size() - 1));
	}

	/**
	 * Gets the number of pots.
	 *
	 * @return the number of pots
	 */
	public int getNumberOfPots() {
		return pots.size();
	}

	/**
	 * Gets the total pot size. That is the sum of the pot size
	 * in all pots plus the rake.
	 *
	 * @return the total pot size
	 */
	public long getTotalPotSize() {
		long total = 0;
		for (Pot pot : pots) {
			total += pot.getPotSize();
		}

		return total;
	}

	/**
	 * Gets the pot size of a specific pot.
	 *
	 * @param i the number of the pot
	 * @return the pot size in the i:th pot
	 */
	public long getPotSize(int i) {
		return pots.get(i).getPotSize();
	}

	/**
	 * Gets the i:th pot.
	 *
	 * @param i the number of the pot
	 * @return the i:th pot
	 */
	public Pot getPot(int i) {
		if ( pots.size() > i )
			return pots.get(i);
		else 
			return new Pot(i);
	}

	/**
	 * Iterates over the pots.
	 *
	 * @return
	 */
	public Collection<Pot> getPots() {
		return pots;
	}

	/**
	 * Adds a pot of size potSize.
	 *
	 * @param potSize
	 */
	@VisibleForTesting
	protected void addPot(Pot pot) {
		pots.add(pot);
	}

	/**
	 * Gets the amount raked in this hand.
	 *
	 * @return
	 */
	public BigDecimal getTotalRake() {
	    BigDecimal totalRake = BigDecimal.ZERO;
	    for (Pot p : pots) {
	        totalRake = totalRake.add(p.getRake());
	    }
		return totalRake;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		int potIndex = 0;
		for (Pot pot : pots) {
			b.append("Pot: " + ++potIndex + "=" + pot.getPotSize() + " ");
		}
		return b.toString();
	}

	public void clearPots() {
		pots.clear();
	}

}
