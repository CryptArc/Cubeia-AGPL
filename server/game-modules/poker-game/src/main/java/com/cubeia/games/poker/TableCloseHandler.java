package com.cubeia.games.poker;

import com.cubeia.firebase.api.action.AbstractGameAction;
import com.cubeia.firebase.api.game.table.Table;
import com.google.inject.ImplementedBy;

@ImplementedBy(TableCloseHandlerImpl.class)
public interface TableCloseHandler {

	/**
	 * @param table Table to close, must not be null
	 * @param force True to close even if players are sitting, false to abort if players are seated
	 */
	public abstract void closeTable(Table table, boolean force);

	public abstract void tableCrashed(Table table);

	public abstract void handleUnexpectedExceptionOnTable(
			AbstractGameAction action, Table table, Throwable throwable);

}