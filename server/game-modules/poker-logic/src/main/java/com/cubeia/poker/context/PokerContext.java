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

package com.cubeia.poker.context;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.model.BlindsInfo;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.timing.TimingProfile;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class contains all the game data for a poker game, including players, cards and pots.
 *
 * It could be broken down further, so that a specialized class holds the players, for example.
 *
 */
public class PokerContext implements Serializable {

    /**
     * Will be set to true if this is a tournament table.
     */
    private boolean tournamentTable = false;

    /**
     * Will be set if this is a tournament table.
     */
    private int tournamentId = -1;

    /**
     * Maps playerId to player
     */
    @VisibleForTesting
    public Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();

    /**
     * Maps playerId to player during the current hand
     */
    @VisibleForTesting
    public Map<Integer, PokerPlayer> currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();

    /**
     * Maps seatId to player
     */
    @VisibleForTesting
    public
    SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();

    /**
     * Maps seat id to players, but only contains players who participate in the current hand.
     */
    private SortedMap<Integer, PokerPlayer> currentHandSeatingMap = new TreeMap<Integer, PokerPlayer>();

    /**
     * We need to keep track of watchers outside of the Firebase kept state
     */
    private Set<Integer> watchers = new HashSet<Integer>();

    private boolean handFinished = false;

    @VisibleForTesting
    public PotHolder potHolder;
    
    private List<Card> communityCards = new ArrayList<Card>();

    @VisibleForTesting
    public PokerSettings settings;
    
    private PokerPlayer lastPlayerToBeCalled;
    
    private long startTime;

    private int tableId;

    private BlindsInfo blindsInfo = new BlindsInfo();

    private static final Logger log = LoggerFactory.getLogger(PokerContext.class);

    public PokerContext(PokerSettings settings) {
        this.settings = settings;
    }

    /**
     * Adds a player.
     * <p/>
     * TODO: Validation is required. Currently, a player can be seated in two seats. Possibly throw a checked exception.
     *
     * @param player
     */
    public void addPlayer(PokerPlayer player) {
        playerMap.put(player.getId(), player);
        seatingMap.put(player.getSeatId(), player);
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public boolean isFinished() {
        return handFinished;
    }

    public boolean isPlayerSeated(int playerId) {
        return playerMap.containsKey(playerId);
    }

    public Collection<PokerPlayer> getSeatedPlayers() {
        return playerMap.values();
    }

    public PokerPlayer getPlayerInCurrentHand(Integer playerId) {
        return getCurrentHandPlayerMap().get(playerId);
    }

    /**
     * Gets the player in the dealer seat. Throws an IllegalStateException if there is no currentHandSeatingMap.
     *
     * @return the player in the dealer seat, or null if there's no player there
     * @throws  IllegalStateException if we don't have a currentHandSeatingMap at the moment, or if it's empty
     */
    public PokerPlayer getPlayerInDealerSeat() {
        if (getCurrentHandSeatingMap() == null || getCurrentHandSeatingMap().isEmpty()) {
            throw new IllegalStateException("no current hand seating map when getting player at dealer button");
        }
        return currentHandSeatingMap.get(blindsInfo.getDealerButtonSeatId());
    }

    public void sitOutPlayersMarkedForSitOutNextRound() {
        for (PokerPlayer player : playerMap.values()) {
            if (player.getSitOutNextRound()) {
                player.setSitOutStatus(SitOutStatus.SITTING_OUT);
            }
        }
    }
    
    /**
     * Take a copy of the supplied map where all players that are not ready to start a hand excluded.
     *
     * @param map
     * @return
     */
    @VisibleForTesting
    protected SortedMap<Integer, PokerPlayer> createCopyWithNotReadyPlayersExcluded(Map<Integer, PokerPlayer> map, Predicate<PokerPlayer> readyPlayerFilter) {
        TreeMap<Integer, PokerPlayer> treeMap = new TreeMap<Integer, PokerPlayer>();
        for (Integer pid : map.keySet()) {
            PokerPlayer pokerPlayer = map.get(pid);
            if (readyPlayerFilter.apply(pokerPlayer)) {
                treeMap.put(pid, pokerPlayer);
            }
        }
        return treeMap;
    }

    /**
     * Returns the players that are sitting in and has no active buy in request to the backend.
     *
     * @return players ready to play
     */
    @VisibleForTesting
    public Collection<PokerPlayer> getPlayersReadyToStartHand(Predicate<PokerPlayer> readyPlayersFilter) {
        return createCopyWithNotReadyPlayersExcluded(playerMap, readyPlayersFilter).values();
    }

    public boolean setSitOutStatus(int playerId, SitOutStatus status) {
        if (isTournamentTable()) {
            log.debug("won't sit out tournament player");
            return false;
        }

        log.debug("player {} is sitting out", playerId);

        PokerPlayer player = playerMap.get(playerId);
        if (player == null || player.isSittingOut()) {
            return false;
        }

        player.setSitOutStatus(status);
        player.setSitOutNextRound(true);
        return true;
    }

    public int countNonFoldedPlayers() {
        int nonFolded = 0;
        for (PokerPlayer p : getCurrentHandPlayerMap().values()) {
            if (!p.hasFolded()) {
                nonFolded++;
            }
        }

        return nonFolded;
    }

    public long getStartTime() {
        return startTime;
    }

    private void saveStartingBalances() {
        for (PokerPlayer p : playerMap.values()) {
            p.setStartingBalance(p.getBalance());
        }
    }

    public void commitPendingBalances() {
        for (PokerPlayer player : playerMap.values()) {
            player.commitBalanceNotInHand(getMaxBuyIn());
        }
    }

    public void removePlayer(PokerPlayer player) {
        removePlayer(player.getId());
    }

    public void removePlayer(int playerId) {
        PokerPlayer removed = playerMap.remove(playerId);
        if (removed != null) {
            seatingMap.remove(removed.getSeatId());
        }
    }

    public PokerPlayer getPokerPlayer(int playerId) {
        return playerMap.get(playerId);
    }

    public TimingProfile getTimingProfile() {
        return settings.getTiming();
    }

    public int getTableSize() {
        return settings.getTableSize();
    }

    public void callOrRaise() {
        potHolder.callOrRaise();
    }

    public boolean hasAllPlayersExposedCards() {

        if (countNonFoldedPlayers() > 1) {
            for (PokerPlayer p : getCurrentHandSeatingMap().values()) {
                if (!p.hasFolded() && !p.isExposingPocketCards()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * takes all players bet stacks and sums it to the pot
     *
     * @return sum of the size of all pots commited to the main or side pots
     */
    @VisibleForTesting
    public long getTotalPotSize() {
        long totalPot = potHolder.getTotalPotSize();

        for (PokerPlayer player : getCurrentHandPlayerMap().values()) {
            totalPot += player.getBetStack();
        }

        return totalPot;
    }

    /**
     * Adds chips to a player. If the player is in a hand, the chips will be
     * added after the hand if finished.
     *
     * @param playerId
     * @param chips
     * @return <code>true</code> if the chips were added immediately,
     *         <code>false</code> if they will be added when the hand is
     *         finished.
     */
    public void addChips(int playerId, long chips) {
        if (!playerMap.containsKey(playerId)) {
            throw new IllegalArgumentException("Player " + playerId + " tried to add chips, but was not seated.");
        }

        if (isPlayerInHand(playerId)) {
            // TODO: Add pending chips request.
        } else {
            playerMap.get(playerId).addChips(chips);
        }
    }

    public int getBalance(int playerId) {
        return (int) playerMap.get(playerId).getBalance();
    }

    public PotHolder getPotHolder() {
        return potHolder;
    }

    public int getAnteLevel() {
        return settings.getAnteLevel();
    }

    public int getEntryBetLevel() {
        return settings.getEntryBetLevel();
    }

    public int getMinBuyIn() {
        return settings.getMinBuyIn();
    }

    public int getMaxBuyIn() {
        return settings.getMaxBuyIn();
    }

    public void checkWarnings() {
        if (playerMap.size() > 20) {
            log.warn("PLAYER MAP SIZE WARNING. Size=" + playerMap.size() + ", Values: " + playerMap);
        }
        if (seatingMap.size() > 20) {
            log.warn("SEATING MAP SIZE WARNING. Size=" + seatingMap.size() + ", Values: " + seatingMap);
        }
    }

    public boolean removeAsWatcher(int playerId) {
        return watchers.remove(playerId);
    }

    public void addWatcher(int playerId) {
        watchers.add(playerId);
    }

    public PokerSettings getSettings() {
        return settings;
    }

    public PokerPlayer getLastPlayerToBeCalled() {
        return lastPlayerToBeCalled;
    }

    int getNumberOfAllinPlayers() {

        int counter = 0;

        for (PokerPlayer pokerPlayer : getCurrentHandPlayerMap().values()) {
            if (pokerPlayer.isAllIn() || pokerPlayer.hasFolded()) {
                ++counter;
            }
        }
        return counter;
    }

    public boolean isAtLeastAllButOneAllIn() {
        return getNumberOfAllinPlayers() >= currentHandPlayerMap.size() - 1;
    }

    public long getPlayersTotalContributionToPot(PokerPlayer player) {
        if (potHolder != null) {
            return potHolder.calculatePlayersContributionToPotIncludingBetStacks(player);
        } else {
            return player.getBetStack();
        }
    }

    public Set<PokerPlayer> getMuckingPlayers() {
        HashSet<PokerPlayer> muckers = new HashSet<PokerPlayer>();

        boolean allButOneOrAllFolded = countNonFoldedPlayers() <= 1;
        if (allButOneOrAllFolded) {
            muckers.addAll(getCurrentHandPlayerMap().values());
        } else {
            for (PokerPlayer player : getCurrentHandPlayerMap().values()) {
                if (player.hasFolded()) {
                    muckers.add(player);
                }
            }
        }

        return muckers;
    }

    public boolean isEveryoneSittingOut() {
        boolean everyoneIsSittingOut = true;
        Map<Integer, PokerPlayer> allCurrentPlayers = getCurrentHandPlayerMap();
        for (PokerPlayer player : allCurrentPlayers.values()) {
            everyoneIsSittingOut &= player.isSittingOut();
        }
        return everyoneIsSittingOut;
    }

    @Override
    public String toString() {
        return "PokerContext";
    }

    public boolean isTournamentTable() {
        return tournamentTable;
    }

    public void setTournamentTable(boolean tournamentTable) {
        this.tournamentTable = tournamentTable;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getNumberOfPlayersSittingIn() {
        int result = 0;
        for (PokerPlayer player : playerMap.values()) {
            if (!player.isSittingOut()) {
                result++;
            }
        }
        return result;
    }

    public void setHandFinished(boolean finished) {
        handFinished = finished;
    }

    /**
     * Returns true if the player is in the set of players for the hand and
     * we are in a playing state (i.e. not playing or waiting to start will result
     * in false being returned).
     */
    public boolean isPlayerInHand(int playerId) {
        return getCurrentHandPlayerMap().containsKey(playerId);
    }

    public void setPotHolder(PotHolder potHolder) {
        this.potHolder = potHolder;
    }

    public Map<Integer, PokerPlayer> getPlayerMap() {
        return playerMap;
    }

    @VisibleForTesting
    public void prepareReadyPlayers(Predicate<PokerPlayer> readyPlayerFilter) {
        currentHandSeatingMap = createCopyWithNotReadyPlayersExcluded(seatingMap, readyPlayerFilter);
        currentHandPlayerMap = createCopyWithNotReadyPlayersExcluded(playerMap, readyPlayerFilter);
        log.debug("players ready for next hand: {}", currentHandPlayerMap.keySet());
    }

    @VisibleForTesting
    void resetValuesAtStartOfHand() {
        startTime = System.currentTimeMillis();
        for (PokerPlayer player : playerMap.values()) {
            player.resetBeforeNewHand();
        }
        setPotHolder(new PotHolder(new LinearRakeWithLimitCalculator(getSettings().getRakeSettings())));
    }

    public Collection<PokerPlayer> getPlayersInHand() {
        return currentHandSeatingMap.values();
    }

    public Map<Integer, PokerPlayer> getCurrentHandPlayerMap() {
        return currentHandPlayerMap;
    }

    public SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap() {
        return currentHandSeatingMap;
    }

    public void setLastPlayerToBeCalled(PokerPlayer lastPlayerToBeCalled) {
        this.lastPlayerToBeCalled = lastPlayerToBeCalled;
    }

    public RakeInfoContainer calculateRakeInfo() {
        return potHolder.calculateRakeIncludingBetStacks(currentHandSeatingMap.values());
    }

    public BlindsInfo getBlindsInfo() {
        return blindsInfo;
    }

    public boolean isTournamentBlinds() {
        return isTournamentTable();
    }

    public void setBlindsInfo(BlindsInfo blindsInfo) {
        this.blindsInfo = blindsInfo;
    }

    public PokerPlayer getPlayer(int playerId) {
        return playerMap.get(playerId);
    }

    public void prepareHand(Predicate<PokerPlayer> readyPlayersFilter) {
        resetValuesAtStartOfHand();
        saveStartingBalances();
        prepareReadyPlayers(readyPlayersFilter);
    }
}