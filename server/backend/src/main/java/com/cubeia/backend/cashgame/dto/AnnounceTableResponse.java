package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class AnnounceTableResponse implements Serializable {

	public final Map<String, String> tableProperties;
	public final TableId tableId;

	public AnnounceTableResponse(TableId tableId) {
		this.tableId = tableId;

		tableProperties = new HashMap<String, String>();
	}

	public String getProperty(String key) {
		return tableProperties.get(key);
	}

	public void setProperty(String key, String value) {
		tableProperties.put(key, value);
	}
}
