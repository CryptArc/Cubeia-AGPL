package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.TableId;

public class CloseTableRequest {

	public final TableId tableId;

	public CloseTableRequest(TableId tableId) {
		this.tableId = tableId;
	}
}
