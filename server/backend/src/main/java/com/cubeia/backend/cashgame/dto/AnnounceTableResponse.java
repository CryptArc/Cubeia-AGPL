package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.backend.cashgame.TableId;

@SuppressWarnings("serial")
public class AnnounceTableResponse implements Serializable {

	private final Map<String, String> tableProperties;
	private final TableId tableId;

	public AnnounceTableResponse(TableId tableId) {
		this.tableId = tableId;
		tableProperties = new HashMap<String, String>();
	}

	public String getProperty(String key) {
		return getTableProperties().get(key);
	}

	public void setProperty(String key, String value) {
		tableProperties.put(key, value);
	}

	@Override
	public String toString() {
		return "AnnounceTableResponse [tableProperties=" + getTableProperties()
				+ ", tableId=" + getTableId() + "]";
	}

	/**
	 * Returns an copy of the response properties.
	 * @return copy of properties
	 */
    public Map<String, String> getTableProperties() {
        return new HashMap<String, String>(tableProperties);
    }

    public TableId getTableId() {
        return tableId;
    }
}
