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

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TournamentSessionId;
import com.cubeia.games.poker.common.Money;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.configuration.lifecycle.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.Payouts;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;
import static java.math.BigDecimal.valueOf;

public class PokerTournamentState implements Serializable {

    private static final long serialVersionUID = 1L;

    private static transient Logger log = Logger.getLogger(PokerTournamentState.class);

    public static final Long STARTING_CHIPS = 100000L;

    private TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();

    private int tablesToCreate;

    private PokerTournamentStatus status = PokerTournamentStatus.ANNOUNCED;

    // Timestamps for profiling
    private long firstRegisteredTime = 0;

    private long lastRegisteredTime = 0;

    /**
     * Maps playerId to balance
     */
    private Map<Integer, Long> balances = new HashMap<Integer, Long>();

    private Set<Integer> pendingRegistrations = newHashSet();

    private BlindsStructure blindsStructure;

    private int currentBlindsLevelNr;

    private Level currentBlindsLevel;

    /**  This id is used in the tournament history for identifying this tournament instance uniquely. */
    private String historicId;

    private BigDecimal buyIn;

    private BigDecimal fee;

    private String currencyCode = "EUR"; // TODO: Make configurable.

    /**  Maps playerId to PlayerSessionId */
    private Map<Integer, PlayerSessionId> playerSessions = newHashMap();

    private boolean onBreak = false;

    private Set<Integer> tablesReadyForBreak = newHashSet();

    private PayoutStructure payoutStructure;

    private Payouts payouts;

    private BigDecimal prizePool = BigDecimal.ZERO;

    // This is a session for the actual tournament. (Please ignore the bad type of it).
    private TournamentSessionId tournamentSessionId;

    private TournamentLifeCycle tournamentLifeCycle;

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

    public TimingProfile getTiming() {
        return timing;
    }

    public void setTiming(TimingProfile timing) {
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
        return getCurrentBlindsLevel().getSmallBlindAmount();
    }

    public int getBigBlindAmount() {
        return getCurrentBlindsLevel().getBigBlindAmount();
    }

    public void setBlindsStructure(BlindsStructure blindsStructure) {
        this.blindsStructure = blindsStructure;
        currentBlindsLevelNr = 0;
        currentBlindsLevel = blindsStructure.getFirstBlindsLevel();
    }

    public BlindsStructure getBlindsStructure() {
        return blindsStructure;
    }

    public Level getCurrentBlindsLevel() {
        return currentBlindsLevel;
    }

    public Level increaseBlindsLevel() {
        log.debug("Increasing blinds level.");
        currentBlindsLevel = blindsStructure.getBlindsLevel(++currentBlindsLevelNr);
        log.debug("Blinds level is now: " + currentBlindsLevelNr + ": " + currentBlindsLevel);
        if (currentBlindsLevel.isBreak()) {
            onBreak = true;
        }
        return currentBlindsLevel;
    }

    public void setHistoricId(String id) {
        this.historicId = id;
    }

    public String getHistoricId() {
        return historicId;
    }

    public Money getBuyInAsMoney() {
        return convertToMoney(buyIn);
    }

    public Money getFeeAsMoney() {
        return convertToMoney(fee);
    }

    public Money getBuyInPlusFeeAsMoney() {
        return convertToMoney(buyIn.add(fee));
    }

    private Money convertToMoney(BigDecimal moneyInDecimalForm) {
        return new Money(moneyInDecimalForm.multiply(valueOf(100)).longValue(), currencyCode, 2);
    }

    public Money convertToMoney(Long moneyInCents) {
        return new Money(moneyInCents, currencyCode, 2);
    }

    public void setBuyIn(BigDecimal buyIn) {
        this.buyIn = buyIn;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void addPendingRegistration(int playerId) {
        pendingRegistrations.add(playerId);
    }

    public void removePendingRequest(int playerId) {
        pendingRegistrations.remove(playerId);
    }

    public boolean hasPendingRegistrations() {
        return !pendingRegistrations.isEmpty();
    }

    public void addPlayerSession(PlayerSessionId sessionId) {
        playerSessions.put(sessionId.playerId, sessionId);
    }

    public boolean isOnBreak() {
        return onBreak;
    }

    public void addTableReadyForBreak(int tableId) {
        tablesReadyForBreak.add(tableId);
    }

    /**
     * Checks if all tables are ready to start the break. A table is ready for break if it has finished
     * a hand after the break was supposed to start, or if it has only one player.
     *
     * @param totalTables the total number of tables in the tournament
     * @param tablesWithLonelyPlayer a set of all tables with only one player, cannot be null
     * @return true if all tables are ready to start the break, false otherwise
     */
    public boolean allTablesReadyForBreak(int totalTables, Set<Integer> tablesWithLonelyPlayer) {
        return union(tablesReadyForBreak, tablesWithLonelyPlayer).size() == totalTables;
    }

    public void breakFinished() {
        onBreak = false;
        tablesReadyForBreak.clear();
    }

    public void setPayoutStructure(PayoutStructure payoutStructure) {
        this.payoutStructure = payoutStructure;
    }

    /**
     * Sets the payouts to use given the number of players that participate in the tournament.
     *
     * @param registeredPlayersCount
     */
    public void setPayouts(int registeredPlayersCount) {
        long totalPrizePoolAsLong = prizePool.movePointRight(2).longValue();
        log.debug("Total prize pool as long: " + totalPrizePoolAsLong);
        this.payouts = payoutStructure.getPayoutsForEntrantsAndPrizePool(registeredPlayersCount, totalPrizePoolAsLong);
    }

    public void addBuyInToPrizePool() {
        log.debug("Adding " + buyIn + " to prize pool.");
        prizePool = prizePool.add(buyIn);
        log.debug("Prize pool is now: " + prizePool);
    }

    public Payouts getPayouts() {
        return payouts;
    }

    public void setTournamentSessionId(PlayerSessionId sessionId) {
        this.tournamentSessionId = new TournamentSessionId(sessionId);
    }

    public PlayerSessionId getPlayerSession(Integer playerId) {
        return playerSessions.get(playerId);
    }

    public TournamentSessionId getTournamentSession() {
        return tournamentSessionId;
    }

    public Money createZeroMoney() {
        return new Money(0, currencyCode, 2);
    }

    public void removePlayerSession(int playerId) {
        playerSessions.remove(playerId);
    }

    public Collection<PlayerSessionId> getPlayerSessions() {
        return playerSessions.values();
    }

    public void setLifecycle(TournamentLifeCycle tournamentLifeCycle) {
        this.tournamentLifeCycle = tournamentLifeCycle;
    }

    public boolean shouldScheduleTournamentStart(DateTime now) {
        return tournamentLifeCycle.shouldScheduleTournamentStart(getStatus(), now);
    }

    public long getTimeUntilTournamentStart(DateTime now) {
        return tournamentLifeCycle.getTimeToTournamentStart(now);
    }

    public long getTimeUntilRegistrationStart(DateTime now) {
        return tournamentLifeCycle.getTimeToRegistrationStart(now);
    }

    public boolean shouldTournamentStart(DateTime now, int registeredPlayers, int minPlayers) {
        boolean lifeCycleSaysStart = tournamentLifeCycle.shouldStartTournament(now, registeredPlayers, minPlayers);
        return lifeCycleSaysStart && !hasPendingRegistrations();
    }

    public boolean shouldCancelTournament(DateTime now, int registeredPlayersCount, int minPlayers) {
        return tournamentLifeCycle.shouldCancelTournament(now, registeredPlayersCount, minPlayers);
    }

    public boolean shouldOpenRegistration(DateTime now) {
        return tournamentLifeCycle.shouldOpenRegistration(now);
    }

    public boolean shouldScheduleRegistrationOpening(DateTime now) {
        return tournamentLifeCycle.shouldScheduleRegistrationOpening(getStatus(), now);
    }
}
