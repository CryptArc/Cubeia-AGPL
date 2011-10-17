package com.cubeia.poker.handhistory.api;

public class HandIdentification {

	private final int tableId;
	private final String tableIntegrationId;
	private final String handId;
	
	public HandIdentification(int tableId, String tableIntegrationId, String handId) {
		this.tableId = tableId;
		this.tableIntegrationId = tableIntegrationId;
		this.handId = handId;
	}
	
	public String getHandId() {
		return handId;
	}
	
	public int getTableId() {
		return tableId;
	}
	
	public String getTableIntegrationId() {
		return tableIntegrationId;
	}
}
