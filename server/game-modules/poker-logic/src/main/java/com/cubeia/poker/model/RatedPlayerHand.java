package com.cubeia.poker.model;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;

import java.io.Serializable;
import java.util.List;

public class RatedPlayerHand implements Serializable {

    private static final long serialVersionUID = 1L;

    private PlayerHand playerHand;
    private HandType bestHandType;
    private List<Card> bestHandCards;

    public RatedPlayerHand(PlayerHand playerHand, HandType bestHandType, List<Card> bestHandCards) {
        this.playerHand = playerHand;
        this.bestHandType = bestHandType;
        this.bestHandCards = bestHandCards;
    }

    public PlayerHand getPlayerHand() {
        return playerHand;
    }

    public HandType getBestHandType() {
        return bestHandType;
    }

    public Integer getPlayerId() {
        return playerHand.getPlayerId();
    }

    public Hand getHand() {
        return playerHand.getHand();
    }

    public List<Card> getBestHandCards() {
        return bestHandCards;
    }

    @Override
    public String toString() {
        return "RatedPlayerHand playerHand[" + playerHand + "] bestHandType[" + bestHandType + "] bestHandCards[" + bestHandCards + "]";
    }
}
