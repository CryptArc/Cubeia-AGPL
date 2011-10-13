package com.cubeia.poker.handhistory.api;

public abstract class HandHistoryEvent {
	
	private final String type;
	
	protected HandHistoryEvent() {
		this.type = getClass().getSimpleName();
	}
	
	public String getType() {
		return type;
	}
}
