package com.cubeia.poker.hand;

import java.util.Collection;

public class ExposedCards {

    private int playerId;

    private Collection<Card> cards;

    public ExposedCards(int playerId, Collection<Card> cards) {
        this.playerId = playerId;
        this.cards = cards;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Collection<Card> getCards() {
        return cards;
    }

    public void setCards(Collection<Card> cards) {
        this.cards = cards;
    }
}
