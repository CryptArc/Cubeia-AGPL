package com.cubeia.games.poker.tournament.configuration.blinds;

import java.io.Serializable;

public class BlindsLevel implements Serializable {

    private int smallBlindAmount;

    private int bigBlindAmount;

    private int anteAmount;

    public BlindsLevel(int smallBlindAmount, int bigBlindAmount, int anteAmount) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.anteAmount = anteAmount;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public int getBigBlindAmount() {
        return bigBlindAmount;
    }

    public int getAnteAmount() {
        return anteAmount;
    }

    @Override
    public String toString() {
        return "BlindsLevel{" + "smallBlindAmount=" + smallBlindAmount + ", bigBlindAmount=" + bigBlindAmount + ", anteAmount=" + anteAmount + '}';
    }
}
