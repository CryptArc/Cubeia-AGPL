package com.cubeia.poker.model;

import com.cubeia.poker.hand.Hand;

import java.io.Serializable;

public class PlayerHand implements Serializable {
    private static final long serialVersionUID = 8327782333044163208L;

    private final Integer playerId;
    private final Hand hand;

    public PlayerHand(Integer playerId, Hand hand) {
        this.playerId = playerId;
        this.hand = hand;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Hand getHand() {
        return hand;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hand == null) ? 0 : hand.hashCode());
        result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
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
        PlayerHand other = (PlayerHand) obj;
        if (hand == null) {
            if (other.hand != null)
                return false;
        } else if (!hand.equals(other.hand))
            return false;
        if (playerId == null) {
            if (other.playerId != null)
                return false;
        } else if (!playerId.equals(other.playerId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PlayerHand - PlayerId[" + playerId + "] Hand[" + hand + "]";
    }
}
