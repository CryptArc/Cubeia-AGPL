package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class BatchHandRequest implements Serializable {

	private final String handId;
	private final TableId tableId;
	private final List<HandResult> handResults;
	private final Money totalRake;
	
	private long startTime;
	private long endTime;

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

    public String getHandId() {
        return handId;
    }

    public TableId getTableId() {
        return tableId;
    }

    public List<HandResult> getHandResults() {
        return new ArrayList<HandResult>(handResults);
    }

    public Money getTotalRake() {
        return totalRake;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
