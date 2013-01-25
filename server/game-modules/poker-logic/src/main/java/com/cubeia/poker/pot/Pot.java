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

import com.cubeia.poker.player.PokerPlayer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Pot
 *
 * @author peter
 */
public class Pot implements Serializable {

    private static final long serialVersionUID = 1600275759404214507L;

    /**
     * pot types
     */
    public enum PotType {
        MAIN, SIDE
    }

    /**
     * pot id
     */
    private final int potId;

    /**
     * type
     */
    private final PotType type;

    /**
     * pot size
     */
    private long potSize;

    /**
     * map of players who has contributed to this pot
     */
    Map<PokerPlayer, Long> playerToBetMap = new HashMap<PokerPlayer, Long>();

    /**
     * is pot open?
     */
    private Boolean isOpen = true;

    public Pot(int potId) {
        this.potId = potId;
        if (potId == 0) {
            this.type = PotType.MAIN;
        } else {
            this.type = PotType.SIDE;
        }
    }


    @Override
    public String toString() {
        return "Pot [potId=" + potId + ", type=" + type + ", isOpen=" + isOpen + ", potSize=" + potSize +
                ", playerToBetMap=" + playerToBetMap + "]";
    }


    public PotType getType() {
        return type;
    }

    /**
     * Add amount to the pot, add the player to the set of contributors.
     *
     * @param player the player who put the bet
     * @param amount the betting amount
     */
    public void bet(PokerPlayer player, Long amount) {
        if (amount == 0) {
            return;
        }
        if (!isOpen) {
            throw new IllegalStateException("Pot is not open");
        }

        potSize += amount;

        Long current = playerToBetMap.get(player);
        if (current == null) {
            playerToBetMap.put(player, amount);
        } else {
            playerToBetMap.put(player, current + amount);
        }


    }

    /**
     * Gets the size of this pot. Note that the returned size includes the
     * rake that should be taken.
     *
     * @return the size of this pot (including rake)
     */
    public long getPotSize() {
        return potSize;
    }

    /**
     * Closes this pot.
     */
    public void close() {
        isOpen = false;
    }

    /**
     * Get the players involved in this pot
     */
    public Map<PokerPlayer, Long> getPotContributors() {
        return playerToBetMap;
    }

    /**
     * is pot open?
     *
     * @return true if the pot is open
     */
    public Boolean isOpen() {
        return isOpen;
    }

    /**
     * return the id of this pot
     */
    public int getId() {
        return potId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + potId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pot other = (Pot) obj;
        if (potId != other.potId)
            return false;
        return true;
    }
}
