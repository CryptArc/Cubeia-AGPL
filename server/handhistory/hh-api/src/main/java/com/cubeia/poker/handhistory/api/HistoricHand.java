package com.cubeia.poker.handhistory.api;

import java.util.ArrayList;
import java.util.List;

public class HistoricHand {

	private int tableId;
	private String handId;
	
	private long startTime;
	private long endTime;
	
	private long totalRake = -1;
	
	private DeckInfo deckInfo;
	
	private final List<HandHistoryEvent> events = new ArrayList<HandHistoryEvent>();
	private final List<Player> seats = new ArrayList<Player>(6);
	
	private Results results;

	public HistoricHand(int tableId, String handId) {
		setHandId(handId);
		setTableId(tableId);
	}
	
	public HistoricHand() { }
	
	public long getTotalRake() {
		return totalRake;
	}
	
	public void setTotalRake(long totalRake) {
		this.totalRake = totalRake;
	}

	public Results getResults() {
		return results;
	}
	
	public void setResults(Results results) {
		this.results = results;
	}
	
	public void setDeckInfo(DeckInfo deckInfo) {
		this.deckInfo = deckInfo;
	}
	
	public DeckInfo getDeckInfo() {
		return deckInfo;
	}
	
	public void setHandId(String handId) {
		this.handId = handId;
	}
	
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	
	public int getTableId() {
		return tableId;
	}

	public String getHandId() {
		return handId;
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

	public List<HandHistoryEvent> getEvents() {
		return events;
	}

	public List<Player> getSeats() {
		return seats;
	}
}
