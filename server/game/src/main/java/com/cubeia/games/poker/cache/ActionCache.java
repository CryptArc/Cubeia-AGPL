package com.cubeia.games.poker.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cubeia.firebase.api.action.GameAction;

/**
 * A simple cache for holding actions that composes the 
 * game state of the current round.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class ActionCache {

	/**
	 * Maps actions to tables.
	 * It is important that this cache is properly cleared since there is
	 * no inherent house-keeping in this implementation.
	 * 
	 * I.e., we rely on YOU to clean up after yourself.
	 * 
	 * This cache is not replicated!
	 */
	protected ConcurrentMap<Integer, List<GameAction>> cache = new ConcurrentHashMap<Integer, List<GameAction>>();

	/**
	 * Add action to a table state cache.
	 * 
	 * @param tableId
	 * @param action
	 */
	public void addAction(int tableId, GameAction action) {
		List<GameAction> list = cache.get(tableId);
		// Since we are guaranteed one event at a time per table
		// We can safely inspect the list and recreate if necessary
		if (list == null) {
			list = new LinkedList<GameAction>();
			cache.put(tableId, list);
		}
		list.add(action);
	}

	/**
	 * Retreive state from a table.
	 * 
	 * @param i
	 * @return
	 */
	public List<GameAction> getActions(int tableId) {
		List<GameAction> list = cache.get(tableId);
		if (list == null) {
			list = new LinkedList<GameAction>();
		}
		return list;
	}

	public void clear(int tableId) {
		cache.remove(tableId);
	}
	
	public String printDetails() {
		String details = "Action Cache: \n";
		for (Integer id : cache.keySet()) {
			details += id+" {\n";
			for (GameAction action : cache.get(id)) {
				details += "\t"+action;
			}
			details += id+"}\n\n";
		}
		return details;
	}
}
