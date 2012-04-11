package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class OpenSessionRequest implements Serializable {

	private final int playerId;
	private final Money openingBalance;
	private final TableId tableId;
	private final int roundNumber; // holds a counter of number of played hands at table

	public OpenSessionRequest(int playerId, TableId tableId, Money openingBalance, int roundNumber) {
		this.playerId = playerId;
		this.tableId = tableId;
		this.roundNumber = roundNumber;
		this.openingBalance = openingBalance;
	}

    public int getPlayerId() {
        return playerId;
    }

    public Money getOpeningBalance() {
        return openingBalance;
    }

    public TableId getTableId() {
        return tableId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
