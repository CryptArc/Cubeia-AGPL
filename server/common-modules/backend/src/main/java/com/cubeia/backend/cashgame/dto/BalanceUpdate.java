package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BalanceUpdate implements Serializable {

    private final PlayerSessionId playerSessionId;
    private final Money balance;
    private final long balanceVersionNumber;

    public BalanceUpdate(PlayerSessionId playerSessionId, Money balance, long balanceVersionNumber) {
        this.playerSessionId = playerSessionId;
        this.balance = balance;
        this.balanceVersionNumber = balanceVersionNumber;
    }

    @Override
    public String toString() {
        return "BalanceUpdate [playerSessionId=" + getPlayerSessionId() + ", balance=" + getBalance() + ", balanceVersionNumber="
                + getBalanceVersionNumber() + "]";
    }

    public PlayerSessionId getPlayerSessionId() {
        return playerSessionId;
    }

    public Money getBalance() {
        return balance;
    }

    public long getBalanceVersionNumber() {
        return balanceVersionNumber;
    }


}
