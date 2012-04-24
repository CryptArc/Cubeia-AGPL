package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AnnounceTableRequest implements Serializable {

	private final int platformTableId;

	public AnnounceTableRequest(int platformTableId) {
		this.platformTableId = platformTableId;
	}

    public int getPlatformTableId() {
        return platformTableId;
    }

}
