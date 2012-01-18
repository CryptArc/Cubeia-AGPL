package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.backend.cashgame.PlayerSessionId;

@SuppressWarnings("serial")
public class ReserveResponse implements Serializable {

	public final BalanceUpdate balanceUpdate;
	public final Money amountReserved;
    public final Map<String, String> reserveProperties;
    
	public ReserveResponse(BalanceUpdate balanceUpdate, Money amountReserved) {
		this.balanceUpdate = balanceUpdate;
		this.amountReserved = amountReserved;
		this.reserveProperties = new HashMap<String, String>();
	}
	
	public PlayerSessionId getPlayerSessionId() {
		return balanceUpdate.playerSessionId;
	}
	
	public void setProperty(String key, String value) {
	    reserveProperties.put(key, value);
	}

    @Override
    public String toString() {
        return "ReserveResponse [balanceUpdate=" + balanceUpdate + ", amountReserved=" + amountReserved
            + ", reserveProperties=" + reserveProperties + "]";
    }
	
	
}
