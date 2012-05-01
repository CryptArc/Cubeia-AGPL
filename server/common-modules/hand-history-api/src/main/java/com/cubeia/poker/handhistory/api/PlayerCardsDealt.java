package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PlayerCardsDealt extends HandHistoryEvent {

    private final int playerId;
    private final List<GameCard> cards = new LinkedList<GameCard>();
    private final boolean isExposed;

    public PlayerCardsDealt(int playerId, boolean isExposed) {
        this.playerId = playerId;
        this.isExposed = isExposed;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public int getPlayerId() {
        return playerId;
    }

    public List<GameCard> getCards() {
        return cards;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cards == null) ? 0 : cards.hashCode());
        result = prime * result + (isExposed ? 1231 : 1237);
        result = prime * result + (int) (playerId ^ (playerId >>> 32));
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
        PlayerCardsDealt other = (PlayerCardsDealt) obj;
        if (cards == null) {
            if (other.cards != null)
                return false;
        } else if (!cards.equals(other.cards))
            return false;
        if (isExposed != other.isExposed)
            return false;
        if (playerId != other.playerId)
            return false;
        return true;
    }
}
