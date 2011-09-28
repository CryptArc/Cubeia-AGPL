package com.cubeia.backend.cashgame.dto;

import java.util.LinkedList;
import java.util.List;

public class BatchHandResponse {

	public final List<BalanceUpdate> resultingBalances;

	public BatchHandResponse() {
		this (new LinkedList<BalanceUpdate>());
	}
	
	public BatchHandResponse(List<BalanceUpdate> resultingBalances) {
		this.resultingBalances = resultingBalances;
	}
	
	public void addResultEntry(BalanceUpdate balanceUpdate) {
		resultingBalances.add(balanceUpdate);
	}
}
