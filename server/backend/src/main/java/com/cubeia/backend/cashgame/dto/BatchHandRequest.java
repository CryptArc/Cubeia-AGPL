package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class BatchHandRequest implements Serializable {

	public final String handId;
	public final TableId tableId;
	public final List<HandResult> handResults;
	public final Money totalRake;
	
	public long startTime;
	public long endTime;

	public BatchHandRequest(String handId, TableId tableId, Money totalRake) {
		this(handId, tableId, new LinkedList<HandResult>(), totalRake);
	}
	
	public BatchHandRequest(String handId, TableId tableId, List<HandResult> handResults, Money totalRake) {
		this.handId = handId;
		this.tableId = tableId;
		this.handResults = handResults;
		this.totalRake = totalRake;
	}

	public void addHandResult(HandResult handResult) {
		handResults.add(handResult);
	}
}
