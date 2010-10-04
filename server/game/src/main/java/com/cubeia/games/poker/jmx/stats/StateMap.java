package com.cubeia.games.poker.jmx.stats;

import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class StateMap {

	private static final Logger log = Logger.getLogger(StateMap.class);
	
	private ConcurrentHashMap<Integer, String> stateMap = new ConcurrentHashMap<Integer, String>();
	
	private ConcurrentHashMap<Integer, Date> dateMap = new ConcurrentHashMap<Integer, Date>();
	
	private int counter = 0;
	
	private final int CHECK_DEAD_TABLES_INTERVAL = 1000;
	
	private final int MILLIS_BEFORE_CONSIDERING_TABLE_DEAD = 60000; 
	
	public void setState(int tableId, String state) {
		stateMap.put(tableId, state);
		dateMap.put(tableId, new Date());
		
		if (counter++ > CHECK_DEAD_TABLES_INTERVAL) {
			counter = 0;
			checkDeadTables();
		}
	}
	
	private void checkDeadTables() {
		Date cutOff = new Date(System.currentTimeMillis() - MILLIS_BEFORE_CONSIDERING_TABLE_DEAD);
		for (Entry<Integer, Date> entry : dateMap.entrySet()) {
			if (entry.getValue().before(cutOff)) {
				log.warn("Table " + entry.getKey() + " has not changed since: " + entry.getValue());
			}
		}
	}

	public String getState(int tableId) {
		return stateMap.get(tableId);
	}

	public Date getLastChangeDate(int tableId) {
		return dateMap.get(tableId);
	}
}
