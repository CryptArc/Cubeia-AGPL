package com.cubeia.games.poker.tournament.configuration.blinds;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Level implements Serializable {

    private int smallBlindAmount;

    private int bigBlindAmount;

    private int anteAmount;

    private int durationInMinutes;

    private boolean isBreak;

    @Id
    @GeneratedValue
    private int id;

    public Level() {
    }

    public Level(int smallBlindAmount, int bigBlindAmount, int anteAmount, int durationInMinutes, boolean isBreak) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.anteAmount = anteAmount;
        this.durationInMinutes = durationInMinutes;
        this.isBreak = isBreak;
    }

    public Level(int smallBlindAmount, int bigBlindAmount, int anteAmount) {
        this(smallBlindAmount, bigBlindAmount, anteAmount, 0, false);
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

    public boolean isBreak() {
        return isBreak;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setSmallBlindAmount(int smallBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
    }

    public void setBigBlindAmount(int bigBlindAmount) {
        this.bigBlindAmount = bigBlindAmount;
    }

    public void setAnteAmount(int anteAmount) {
        this.anteAmount = anteAmount;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Level{" +
                "smallBlindAmount=" + smallBlindAmount +
                ", bigBlindAmount=" + bigBlindAmount +
                ", anteAmount=" + anteAmount +
                ", durationInMinutes=" + durationInMinutes +
                ", isBreak=" + isBreak +
                ", id=" + id +
                '}';
    }

}
