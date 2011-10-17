package com.cubeia.poker.handhistory.api;

public abstract class HandHistoryEvent {
	
	private final String type;
	private long time;
	
	protected HandHistoryEvent() {
		this.type = getClass().getSimpleName();
		this.time = System.currentTimeMillis();
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getType() {
		return type;
	}
}
