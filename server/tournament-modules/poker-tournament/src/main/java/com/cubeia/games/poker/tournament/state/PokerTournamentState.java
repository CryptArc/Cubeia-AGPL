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

package com.cubeia.games.poker.tournament.state;

import com.cubeia.games.poker.tournament.configuration.blinds.BlindsLevel;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.cubeia.poker.timing.Timings;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PokerTournamentState implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static transient Logger log = Logger.getLogger(PokerTournamentState.class);

    private Timings timing = Timings.DEFAULT;

    private int tablesToCreate;

    private PokerTournamentStatus status = PokerTournamentStatus.ANNOUNCED;

    // Timestamps for profiling
    private long firstRegisteredTime = 0;

    private long lastRegisteredTime = 0;

    /**
     * Maps playerId to balance
     */
    private Map<Integer, Long> balances = new HashMap<Integer, Long>();

    private BlindsStructure blindsStructure;

    private Iterator<BlindsLevel> blindsLevelIterator;

    private BlindsLevel currentLevel;

    public boolean allTablesHaveBeenCreated(int tablesCreated) {
        return tablesCreated >= tablesToCreate;
    }

    public void setTablesToCreate(int tablesToCreate) {
        this.tablesToCreate = tablesToCreate;
    }

    public Long getPlayerBalance(int playerId) {
        return balances.get(playerId);
    }

    public void setBalance(int playerId, long balance) {
        balances.put(playerId, balance);
    }

    public PokerTournamentStatus getStatus() {
        return status;
    }

    public void setStatus(PokerTournamentStatus status) {
        this.status = status;
    }

    public Timings getTiming() {
        return timing;
    }

    public void setTiming(Timings timing) {
        this.timing = timing;
    }

    public long getFirstRegisteredTime() {
        return firstRegisteredTime;
    }

    public void setFirstRegisteredTime(long firstRegisteredTime) {
        this.firstRegisteredTime = firstRegisteredTime;
    }

    public long getLastRegisteredTime() {
        return lastRegisteredTime;
    }

    public void setLastRegisteredTime(long lastRegisteredTime) {
        this.lastRegisteredTime = lastRegisteredTime;
    }

    public int getSmallBlindAmount() {
        return getCurrentLevel().getSmallBlindAmount();
    }

    public int getBigBlindAmount() {
        return getCurrentLevel().getBigBlindAmount();
    }

    public void setBlindsStructure(BlindsStructure blindsStructure) {
        this.blindsStructure = blindsStructure;
        blindsLevelIterator = blindsStructure.getLevelIterator();
        currentLevel = blindsLevelIterator.next();
    }

    public BlindsStructure getBlindsStructure() {
        return blindsStructure;
    }

    public BlindsLevel getCurrentBlindsLevel() {
        return currentLevel;
    }

    public BlindsLevel getCurrentLevel() {
        return currentLevel;
    }

    public void increaseBlindsLevel() {
        log.debug("Increasing blinds level.");
        if (blindsLevelIterator.hasNext()) {
            currentLevel = blindsLevelIterator.next();
            log.debug("Blinds level is now: " + currentLevel);
        } else {
            log.warn("No more blinds levels, staying on level " + currentLevel);
        }
    }
}
