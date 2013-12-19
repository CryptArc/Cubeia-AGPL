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

package com.cubeia.games.poker.entity;

import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class TableConfigTemplate implements Serializable {

    private static final long serialVersionUID = -2416951532409186240L;

    public static final int DEF_MIN_BUY_IN_ANTE_MULTIPLIER = 10;
    public static final int DEF_MAX_BUY_IN_ANTE_MULTIPLIER = 100;

    public enum TemplateStatus { REMOVED, DISABLED, ENABLED };
    
    @Id
    @GeneratedValue
    private int id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateStatus status = TemplateStatus.ENABLED;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private PokerVariant variant;

    @Column(nullable = false)
    private BigDecimal ante;

    @Column(nullable = false)
    private BigDecimal smallBlind;

    @Column(nullable = false)
    private BigDecimal bigBlind;

    @Column(nullable = false)
    private int minBuyInMultiplier = DEF_MIN_BUY_IN_ANTE_MULTIPLIER;

    @Column(nullable = false)
    private int maxBuyInMultiplier = DEF_MAX_BUY_IN_ANTE_MULTIPLIER;

    @Column(nullable = false)
    private BigDecimal minBuyIn;

    @Column(nullable = true)
    private BigDecimal maxBuyIn;

    @Column(nullable = false)
    private int minEmptyTables;

    @Column(nullable = false)
    private int minTables;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private BetStrategyType betStrategy;

    @ManyToOne()
    private TimingProfile timing;

    @ManyToOne()
    private RakeSettings rakeSettings;

    @Column(nullable = false)
    private long ttl;

    @Column
    private String currency;

    public long getTTL() {
        return ttl;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    public RakeSettings getRakeSettings() {
        return rakeSettings;
    }

    public void setRakeSettings(RakeSettings rakeSettings) {
        this.rakeSettings = rakeSettings;
    }

    public TimingProfile getTiming() {
        return timing;
    }

    public void setTiming(TimingProfile timing) {
        this.timing = timing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public TemplateStatus getStatus() {
        return status;
    }
    
    public void setStatus(TemplateStatus status) {
        this.status = status;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PokerVariant getVariant() {
        return variant;
    }

    public void setVariant(PokerVariant variant) {
        this.variant = variant;
    }

    public BigDecimal getAnte() {
        if (ante != null) {
        	return ante;
        } else {
        	return BigDecimal.ZERO;
        }
    }

    public void setAnte(BigDecimal ante) {
        this.ante = ante;
    }

    public int getMinBuyInMultiplier() {
        return minBuyInMultiplier;
    }

    public void setMinBuyInMultiplier(int minBuyInMultiplier) {
        this.minBuyInMultiplier = minBuyInMultiplier;
    }

    public int getMaxBuyInMultiplier() {
        return maxBuyInMultiplier;
    }

    public void setMaxBuyInMultiplier(int maxBuyInMultiplier) {
        this.maxBuyInMultiplier = maxBuyInMultiplier;
    }

    public int getMinEmptyTables() {
        return minEmptyTables;
    }

    public void setMinEmptyTables(int minEmptyTables) {
        this.minEmptyTables = minEmptyTables;
    }

    public int getMinTables() {
        return minTables;
    }

    public void setMinTables(int minTables) {
        this.minTables = minTables;
    }

    public BigDecimal getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(BigDecimal smallBlind) {
        this.smallBlind = smallBlind;
    }

    public BigDecimal getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(BigDecimal bigBlind) {
        this.bigBlind = bigBlind;
    }

    public BetStrategyType getBetStrategy() {
        return betStrategy;
    }

    public void setBetStrategy(BetStrategyType betStrategy) {
        this.betStrategy = betStrategy;
    }




    @Override
    public String toString() {
        return "TableConfigTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", variant=" + variant +
                ", ante=" + ante +
                ", smallBlind=" + smallBlind +
                ", bigBlind=" + bigBlind +
                ", minBuyInMultiplier=" + minBuyInMultiplier +
                ", maxBuyInMultiplier=" + maxBuyInMultiplier +
                ", minEmptyTables=" + minEmptyTables +
                ", minTables=" + minTables +
                ", seats=" + seats +
                ", betStrategy=" + betStrategy +
                ", timing=" + timing +
                ", rakeSettings=" + rakeSettings +
                ", ttl=" + ttl +
                '}';
    }

    public BigDecimal getMinBuyIn() {
        return minBuyIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableConfigTemplate other = (TableConfigTemplate) obj;
        if (ante == null) {
            if (other.ante != null)
                return false;
        } else if (!ante.equals(other.ante))
            return false;
        if (betStrategy != other.betStrategy)
            return false;
        if (bigBlind == null) {
            if (other.bigBlind != null)
                return false;
        } else if (!bigBlind.equals(other.bigBlind))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (id != other.id)
            return false;
        if (maxBuyIn == null) {
            if (other.maxBuyIn != null)
                return false;
        } else if (!maxBuyIn.equals(other.maxBuyIn))
            return false;
        if (maxBuyInMultiplier != other.maxBuyInMultiplier)
            return false;
        if (minBuyIn == null) {
            if (other.minBuyIn != null)
                return false;
        } else if (!minBuyIn.equals(other.minBuyIn))
            return false;
        if (minBuyInMultiplier != other.minBuyInMultiplier)
            return false;
        if (minEmptyTables != other.minEmptyTables)
            return false;
        if (minTables != other.minTables)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (rakeSettings == null) {
            if (other.rakeSettings != null)
                return false;
        } else if (!rakeSettings.equals(other.rakeSettings))
            return false;
        if (seats != other.seats)
            return false;
        if (smallBlind == null) {
            if (other.smallBlind != null)
                return false;
        } else if (!smallBlind.equals(other.smallBlind))
            return false;
        if (status != other.status)
            return false;
        if (timing == null) {
            if (other.timing != null)
                return false;
        } else if (!timing.equals(other.timing))
            return false;
        if (ttl != other.ttl)
            return false;
        if (variant != other.variant)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ante == null) ? 0 : ante.hashCode());
        result = prime * result + ((betStrategy == null) ? 0 : betStrategy.hashCode());
        result = prime * result + ((bigBlind == null) ? 0 : bigBlind.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + id;
        result = prime * result + ((maxBuyIn == null) ? 0 : maxBuyIn.hashCode());
        result = prime * result + maxBuyInMultiplier;
        result = prime * result + ((minBuyIn == null) ? 0 : minBuyIn.hashCode());
        result = prime * result + minBuyInMultiplier;
        result = prime * result + minEmptyTables;
        result = prime * result + minTables;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rakeSettings == null) ? 0 : rakeSettings.hashCode());
        result = prime * result + seats;
        result = prime * result + ((smallBlind == null) ? 0 : smallBlind.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((timing == null) ? 0 : timing.hashCode());
        result = prime * result + (int) (ttl ^ (ttl >>> 32));
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    public void setMinBuyIn(BigDecimal minBuyIn) {

        this.minBuyIn = minBuyIn;
    }

    public BigDecimal getMaxBuyIn() {
        return maxBuyIn;
    }

    public void setMaxBuyIn(BigDecimal maxBuyIn) {
        this.maxBuyIn = maxBuyIn;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
