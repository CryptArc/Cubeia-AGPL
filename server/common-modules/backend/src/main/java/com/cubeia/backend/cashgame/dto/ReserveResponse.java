package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ReserveResponse implements Serializable {

    private final BalanceUpdate balanceUpdate;
    private final Money amountReserved;
    private final Map<String, String> reserveProperties;

    public ReserveResponse(BalanceUpdate balanceUpdate, Money amountReserved) {
        this.balanceUpdate = balanceUpdate;
        this.amountReserved = amountReserved;
        this.reserveProperties = new HashMap<String, String>();
    }

    public PlayerSessionId getPlayerSessionId() {
        return getBalanceUpdate().getPlayerSessionId();
    }

    public void setProperty(String key, String value) {
        reserveProperties.put(key, value);
    }

    @Override
    public String toString() {
        return "ReserveResponse [balanceUpdate=" + getBalanceUpdate() + ", amountReserved=" + getAmountReserved()
                + ", reserveProperties=" + getReserveProperties() + "]";
    }

    public BalanceUpdate getBalanceUpdate() {
        return balanceUpdate;
    }

    public Money getAmountReserved() {
        return amountReserved;
    }

    public Map<String, String> getReserveProperties() {
        return new HashMap<String, String>(reserveProperties);
    }


}
