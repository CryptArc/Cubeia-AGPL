package com.cubeia.backend.cashgame;

public class TableId {
	public final int id;

	public TableId(int tableId) {
		this.id = tableId;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof TableId)) {
			return false;
		}

		return id == ((TableId) obj).id;
	}
}
