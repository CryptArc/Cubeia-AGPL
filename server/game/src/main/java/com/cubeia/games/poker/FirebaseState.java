package com.cubeia.games.poker;

import java.io.Serializable;

import com.cubeia.games.poker.persistence.history.model.PlayedHand;

public class FirebaseState implements Serializable {

    /** Version id */
    private static final long serialVersionUID = 1L;
    
    private int currentRequestSequence = -1;
    
    private int handCount = 0;
    
    /* FIXME: Why do we have an JPA Entity in this distributed stateful object? */
    private transient PlayedHand playerHand;

    public int getCurrentRequestSequence() {
        return currentRequestSequence;
    }

    public void setCurrentRequestSequence(int currentRequestSequence) {
        this.currentRequestSequence = currentRequestSequence;
    }

    public int getHandCount() {
        return handCount;
    }

    public void setHandCount(int handCount) {
        this.handCount = handCount;
    }

    public PlayedHand getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(PlayedHand playerHand) {
        this.playerHand = playerHand;
    }

}
