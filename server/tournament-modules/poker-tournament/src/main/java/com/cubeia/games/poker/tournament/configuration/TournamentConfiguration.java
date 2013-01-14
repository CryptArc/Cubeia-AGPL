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

package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import org.apache.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.cubeia.poker.betting.BetStrategyType.NO_LIMIT;

/**
 * This class represents the configuration of a tournament.
 *
 */
@Entity
public class TournamentConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(TournamentConfiguration.class);

    @Id @GeneratedValue
    private int id;

    private String name;

    private int seatsPerTable = 10;

    @ManyToOne
    private TimingProfile timingType;

    private int minPlayers = 0;

    private int maxPlayers = 0;

    @ManyToOne
    private BlindsStructure blindsStructure;

    private BigDecimal buyIn;

    private BigDecimal fee;

    private BetStrategyType betStrategy = NO_LIMIT;

    @ManyToOne(fetch = FetchType.LAZY)
    private PayoutStructure payoutStructure;

    public TournamentConfiguration() {
    }

    public String toString() {
        return "id[" + id + "] name[" + name + "] seats[" + seatsPerTable + "] timing[" + timingType + "] min[" + minPlayers + "] max[" + maxPlayers + "] ";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeatsPerTable() {
        return seatsPerTable;
    }

    public void setSeatsPerTable(int seatsPerTable) {
        this.seatsPerTable = seatsPerTable;
    }

    public TimingProfile getTimingType() {
        return timingType;
    }

    public void setTimingType(TimingProfile timingType) {
        this.timingType = timingType;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayer) {
        this.maxPlayers = maxPlayer;
    }

    public BlindsStructure getBlindsStructure() {
        // NOTE: If you don't copy this instance, all tournaments of this configuration will share the same instance of the blinds => bad.
        log.debug("Returning copy of blinds structure: " + blindsStructure);
        if (blindsStructure == null) {
            log.warn("No blinds structure defined, using default structure.");
            blindsStructure = BlindsStructureFactory.createDefaultBlindsStructure();
        }
        return blindsStructure;
    }

    public void setBlindsStructure(BlindsStructure blindsStructure) {
        this.blindsStructure = blindsStructure;
    }

    public void setBuyIn(BigDecimal buyIn) {
        this.buyIn = buyIn;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getBuyIn() {
        return buyIn;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public PayoutStructure getPayoutStructure() {
        return payoutStructure;
    }

    public void setPayoutStructure(PayoutStructure payoutStructure) {
        this.payoutStructure = payoutStructure;
    }

    public BetStrategyType getBetStrategy() {
        return betStrategy;
    }

    public void setBetStrategy(BetStrategyType betStrategy) {
        this.betStrategy = betStrategy;
    }
}
