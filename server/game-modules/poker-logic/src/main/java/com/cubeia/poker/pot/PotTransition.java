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

/**
 * Transition of money from a player to a pot.
 * If the amount is negative the direction is from pot to player.
 *
 * @author w
 */
@SuppressWarnings("serial")
public class PotTransition implements Serializable {

    private final PokerPlayer player;
    private final Pot pot;
    private final long amount;
    private final boolean fromChipStackToPlayer;

    /**
     * Needed by JBoss Serialization
     */
    @SuppressWarnings("unused")
    private PotTransition() {
        player = null;
        pot = null;
        amount = -1;
        fromChipStackToPlayer = false;
    }

    public PotTransition(PokerPlayer player, Pot pot, long amount) {
        this.player = player;
        this.pot = pot;
        this.amount = amount;
        fromChipStackToPlayer = false;
    }

    private PotTransition(PokerPlayer player, long amount) {
        fromChipStackToPlayer = true;
        this.amount = amount;
        this.player = player;
        this.pot = null;
    }

    public boolean isFromPlayerToPot() {
        return getAmount() > 0;
    }

    public boolean isFromBetStackToPlayer() {
        return fromChipStackToPlayer;
    }

    public PokerPlayer getPlayer() {
        return player;
    }

    public Pot getPot() {
        return pot;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (amount ^ (amount >>> 32));
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        result = prime * result + ((pot == null) ? 0 : pot.hashCode());
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
        PotTransition other = (PotTransition) obj;
        if (amount != other.amount)
            return false;
        if (player == null) {
            if (other.player != null)
                return false;
        } else if (!player.equals(other.player))
            return false;
        if (pot == null) {
            if (other.pot != null)
                return false;
        } else if (!pot.equals(other.pot))
            return false;
        return true;
    }


    @Override
    public String toString() {


        if (fromChipStackToPlayer) {
            return "pot transition player " + player.getId() + ": amount " + amount + " -> pot: <null> fromChipStackToPlayer: true";
        } else {
            return "pot transition player " + player.getId() + ": amount " + amount + " -> pot: " + pot.getId() + " fromChipStackToPlayer: false";
        }

    }

    public static PotTransition createTransitionFromBetStackToPlayer(PokerPlayer player, long amount) {
        return new PotTransition(player, amount);
    }
}
