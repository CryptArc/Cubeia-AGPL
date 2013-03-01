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

package com.cubeia.games.poker.tournament.state;

import com.cubeia.games.poker.common.money.MoneyFormatter;
import com.cubeia.games.poker.io.protocol.RebuyOffer;
import com.cubeia.games.poker.tournament.util.PacketSender;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptySet;

/**
 * This class holds all rebuy related data for a tournament.
 * <p/>
 * Here are some basic properties of rebuy tournaments.
 * <p/>
 * 1. If a tournament is a rebuy tournament, it will allow rebuys during a rebuy period.
 * 2. This period is defined by the number of blinds level during which rebuys are available.
 * 3. Rebuys become available once we reach the first blinds level greater than the defined number of levels with rebuys
 * OR WHEN THE TOURNAMENT REACHES THE MONEY.
 * 4. That is, as soon as a player is out and gets money, no more rebuys are allowed.
 * 5. This is because a new rebuy would change the payouts, but we have already paid out money to at least one player.
 * 6. A rebuy tournament may have an add-on period after the rebuy period is finished.
 * 7. This period should be a break. So if you want rebuys during 60 minutes, followed by add-ons for 5 minutes,
 * you need to define for example 6 blinds levels of 10 minutes and the 7th level AS A BREAK of 5 minutes.
 * 8. If the tournament is configured incorrectly, for example if the 7th level is not a break, there'll be no add-ons.
 * 9. If we are already in the money when the add-on period starts, there'll be no add-ons, for the same reasons as in point 5.
 */
public class RebuySupport implements Serializable {

    public static final RebuySupport NO_REBUYS = new RebuySupport(false, 0, 0, 0, false, 0, ZERO, ZERO);

    /**
     * The amount of chips you get when doing a rebuy.
     */
    private long rebuyChipsAmount;

    /**
     * The amount of chips you get when doing a add-on.
     */
    private long addOnChipsAmount;

    /**
     * The number of rebuys allowed in this tournament. 0 means no rebuys. MAX_INT means unlimited (go ahead and prove me wrong).
     */
    private int maxRebuys = 0;

    private boolean rebuysAvailable = false;

    private boolean addOnsEnabled = false;

    private int numberOfLevelsWithRebuys = 0;

    private BigDecimal rebuyCost;

    private BigDecimal addOnCost;

    /**
     * Maps playerId to the number of rebuys performed by that player.
     */
    private Map<Integer, Integer> numberOfRebuysPerformed = newHashMap();

    /**
     * Maps a tableId to the players at that table who have been asked to perform a rebuy.
     */
    private Map<Integer, Set<Integer>> rebuyRequestsPerTable = newHashMap();

    private boolean inTheMoney;

    public RebuySupport(boolean rebuysAvailable, long rebuyChipsAmount, long addOnChipsAmount, int maxRebuys, boolean addOnsEnabled,
                        int numberOfLevelsWithRebuys, BigDecimal rebuyCost, BigDecimal addOnCost) {
        this.rebuysAvailable = rebuysAvailable;
        this.rebuyChipsAmount = rebuyChipsAmount;
        this.addOnChipsAmount = addOnChipsAmount;
        this.maxRebuys = maxRebuys;
        this.addOnsEnabled = addOnsEnabled;
        this.numberOfLevelsWithRebuys = numberOfLevelsWithRebuys;
        this.rebuyCost = rebuyCost;
        this.addOnCost = addOnCost;
    }

    private Predicate<Integer> rebuyAllowed = new Predicate<Integer>() {
        @Override
        public boolean apply(@Nullable Integer playerId) {
            if (inTheMoney || !rebuysAvailable) {
                return false;
            } else {
                return numberOfRebuysPerformedBy(playerId) < maxRebuys;
            }
        }
    };

    /**
     * Checks if add-ons are available during this break.
     * <p/>
     * If a tournament has add-ons available, they will be so during the break which occurs just after
     * the rebuys have finished. That is, if we have rebuys during the first 6 blinds levels, then the 7th level
     * should be a break and during this break add-ons will be available.
     */
    public boolean addOnsAvailableDuringBreak(int currentBlindsLevelNr) {
        return !inTheMoney && addOnsEnabled && currentBlindsLevelNr == numberOfLevelsWithRebuys + 1;
    }

    public void addRebuyRequestsForTable(int tableId, Set<Integer> playersWithRebuyOption) {
        if (!playersWithRebuyOption.isEmpty()) {
            rebuyRequestsPerTable.put(tableId, playersWithRebuyOption);
        }
    }

    public boolean isPlayerAllowedToRebuy(int playerId) {
        return rebuyAllowed.apply(playerId);
    }

    public void removeRebuyRequestForTable(int tableId) {
        rebuyRequestsPerTable.remove(tableId);
    }

    public long getAddOnChipsAmount() {
        return addOnChipsAmount;
    }

    public void notifyInTheMoney() {
        inTheMoney = true;
        rebuysAvailable = false;
    }

    public void notifyNewLevelStarted(int currentBlindsLevelNr) {
        if (currentBlindsLevelNr > numberOfLevelsWithRebuys) {
            rebuysAvailable = false;
        }
    }

    private int numberOfRebuysPerformedBy(Integer playerId) {
        if (numberOfRebuysPerformed.containsKey(playerId)) {
            return numberOfRebuysPerformed.get(playerId);
        } else {
            return 0;
        }
    }

    public long getRebuyChipsAmount() {
        return rebuyChipsAmount;
    }

    public Set<Integer> getRebuyRequests(Integer tableId) {
        if (rebuyRequestsPerTable.containsKey(tableId)) {
            return rebuyRequestsPerTable.get(tableId);
        } else {
            return emptySet();
        }
    }

    public BigDecimal getAddOnCost() {
        return addOnCost;
    }

    public Set<Integer> requestRebuys(int tableId, Set<Integer> playersOut, PacketSender sender) {
        Set<Integer> playersWithRebuyOption = filter(playersOut, rebuyAllowed);
        for (Integer playerId : playersWithRebuyOption) {
            // Might be better to send this to the table, so the table can notify all other players as well.
            sender.sendPacketToPlayer(new RebuyOffer(MoneyFormatter.format(rebuyCost), MoneyFormatter.format(rebuyChipsAmount)), playerId);
        }
        addRebuyRequestsForTable(tableId, playersWithRebuyOption);
        return playersWithRebuyOption;
    }

}
