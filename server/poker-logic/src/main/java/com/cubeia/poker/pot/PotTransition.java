package com.cubeia.poker.pot;

import java.io.Serializable;

import com.cubeia.poker.player.PokerPlayer;

/**
 * Transition of money from a player to a pot.
 * If the amount is negative the direction is from pot to player.
 * @author w
 */
@SuppressWarnings("serial")
public class PotTransition implements Serializable {

    private final PokerPlayer player;
    private final Pot pot;
    private final long amount;

    /**
     * Needed by JBoss Serialization
     */
    @SuppressWarnings("unused")
    private PotTransition() {
        player = null;
        pot = null;
        amount = -1;
    }
    
    public PotTransition(PokerPlayer player, Pot pot, long amount) {
        this.player = player;
        this.pot = pot;
        this.amount = amount;
    }

    public boolean isFromPlayerToPot() {
        return getAmount() > 0;
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
        return "pot transition player " + player.getId() + ": amount " + amount + " -> pot " + pot.getId();
    }
}
