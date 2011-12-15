package com.cubeia.games.poker.debugger.cache;

import com.cubeia.games.poker.debugger.json.EventType;

public class Event {
	
	private EventType type = EventType.unknown;
	
	private String description;
	
	private String timestamp;

	public Event() {}
	
	public Event(EventType type, String description, String timestamp) {
		super();
		this.type = type;
		this.description = description;
		this.timestamp = timestamp;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return timestamp + " - " + description;
	}
	
}
