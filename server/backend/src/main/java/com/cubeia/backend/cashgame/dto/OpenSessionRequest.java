package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class OpenSessionRequest implements Serializable {

	public final int playerId;
	public final Money openingBalance;
	public final TableId tableId;
	public final int roundNumber; // holds a counter of number of played hands at table

	public OpenSessionRequest(int playerId, TableId tableId, Money openingBalance, int roundNumber) {
		this.playerId = playerId;
		this.tableId = tableId;
		this.roundNumber = roundNumber;
		this.openingBalance = openingBalance;
	}
}
