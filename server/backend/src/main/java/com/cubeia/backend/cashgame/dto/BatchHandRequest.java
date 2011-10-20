package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class BatchHandRequest implements Serializable {

	public final long handId;
	public final TableId tableId;
	public final List<HandResult> handResults;

	public BatchHandRequest(long handId, TableId tableId) {
		this (handId, tableId, new LinkedList<HandResult>());
	}
	
	public BatchHandRequest(long handId, TableId tableId,
			List<HandResult> handResults) {
		this.handId = handId;
		this.tableId = tableId;
		this.handResults = handResults;
	}


	public void addHandResult(HandResult handResult) {
		handResults.add(handResult);
	}
	

}