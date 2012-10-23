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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.cubeia.poker.timing.Timings;
import com.cubeia.poker.variant.PokerVariant;

@Entity
public class TableConfigTemplate implements Serializable {

    private static final long serialVersionUID = -1593417411016642341L;

    public static final int DEF_MIN_BUY_IN_ANTE_MULTIPLIER = 10;
    public static final int DEF_MAX_BUY_IN_ANTE_MULTIPLIER = 100;

    public static final BigDecimal DEF_RAKE_FRACTION = new BigDecimal("0.01");

    public static final long DEF_RAKE_LIMIT = 500;
    public static final long DEF_RAKE_LIMIT_HEADS_UP = 150;

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private PokerVariant variant;

    @Column(nullable = false)
    private int ante;

    @Column(nullable = false)
    private int minBuyInMultiplier = DEF_MIN_BUY_IN_ANTE_MULTIPLIER;

    @Column(nullable = false)
    private int maxBuyInMultiplier = DEF_MAX_BUY_IN_ANTE_MULTIPLIER;

    @Column(nullable = false)
    private int minEmptyTables;

    @Column(nullable = false)
    private int minTables;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private Timings timing;

    @Column(nullable = false)
    private long rakeLimit = DEF_RAKE_LIMIT;

    @Column(nullable = false)
    private long rakeHeadsUpLimit = DEF_RAKE_LIMIT_HEADS_UP;

    @Column(nullable = false)
    private BigDecimal rakeFraction = DEF_RAKE_FRACTION;

    @Column(nullable = false)
    private long ttl;

    public long getTTL() {
        return ttl;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    public long getRakeLimit() {
        return rakeLimit;
    }

    public void setRakeLimit(long rakeLimit) {
        this.rakeLimit = rakeLimit;
    }

    public long getRakeHeadsUpLimit() {
        return rakeHeadsUpLimit;
    }

    public void setRakeHeadsUpLimit(long rakeHeadsUpLimit) {
        this.rakeHeadsUpLimit = rakeHeadsUpLimit;
    }

    public BigDecimal getRakeFraction() {
        return rakeFraction;
    }

    public void setRakeFraction(BigDecimal rakeFraction) {
        this.rakeFraction = rakeFraction;
    }

    public Timings getTiming() {
        return timing;
    }

    public void setTiming(Timings timing) {
        this.timing = timing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getAnte() {
        return ante;
    }

    public void setAnte(int ante) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ante;
        result = prime * result + id;
        result = prime * result + maxBuyInMultiplier;
        result = prime * result + minBuyInMultiplier;
        result = prime * result + minEmptyTables;
        result = prime * result + minTables;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                 + ((rakeFraction == null) ? 0 : rakeFraction.hashCode());
        result = prime * result
                 + (int) (rakeHeadsUpLimit ^ (rakeHeadsUpLimit >>> 32));
        result = prime * result + (int) (rakeLimit ^ (rakeLimit >>> 32));
        result = prime * result + seats;
        result = prime * result + ((timing == null) ? 0 : timing.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TableConfigTemplate other = (TableConfigTemplate) obj;
        if (ante != other.ante) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (maxBuyInMultiplier != other.maxBuyInMultiplier) {
            return false;
        }
        if (minBuyInMultiplier != other.minBuyInMultiplier) {
            return false;
        }
        if (minEmptyTables != other.minEmptyTables) {
            return false;
        }
        if (minTables != other.minTables) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (rakeFraction == null) {
            if (other.rakeFraction != null) {
                return false;
            }
        } else if (!rakeFraction.equals(other.rakeFraction)) {
            return false;
        }
        if (rakeHeadsUpLimit != other.rakeHeadsUpLimit) {
            return false;
        }
        if (rakeLimit != other.rakeLimit) {
            return false;
        }
        if (seats != other.seats) {
            return false;
        }
        if (timing != other.timing) {
            return false;
        }
        if (variant != other.variant) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TableConfigTemplate [id=" + id + ", name=" + name
               + ", variant=" + variant + ", ante=" + ante
               + ", minBuyInMultiplier=" + minBuyInMultiplier
               + ", maxBuyInMultiplier=" + maxBuyInMultiplier
               + ", minEmptyTables=" + minEmptyTables + ", minTables=" + minTables + ", seats="
               + seats + ", timing=" + timing + ", rakeLimit=" + rakeLimit
               + ", rakeHeadsUpLimit=" + rakeHeadsUpLimit + ", rakeFraction="
               + rakeFraction + "]";
    }
}
