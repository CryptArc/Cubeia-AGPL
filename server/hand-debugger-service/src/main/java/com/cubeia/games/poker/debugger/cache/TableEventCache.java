package com.cubeia.games.poker.debugger.cache;

import java.util.List;

import com.cubeia.firebase.api.action.GameAction;

public interface TableEventCache<T> {

	public abstract void addPublicAction(int tableId, GameAction action);

	public abstract void addPrivateAction(int tableId, int playerId,
			GameAction action);

	public abstract void clearTable(int tableId);

	public abstract List<T> getEvents(int tableId);

	public abstract List<T> getPreviousEvents(int tableId);

}