package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class CloseTableRequest implements Serializable {

	public final TableId tableId;

	public CloseTableRequest(TableId tableId) {
		this.tableId = tableId;
	}
}
