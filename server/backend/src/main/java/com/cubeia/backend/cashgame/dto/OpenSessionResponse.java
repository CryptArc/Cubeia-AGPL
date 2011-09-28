package com.cubeia.backend.cashgame.dto;

import java.util.Map;

import com.cubeia.backend.cashgame.PlayerSessionId;

public class OpenSessionResponse {

	public final PlayerSessionId sessionId;
	public final Map<String, String> sessionProperties;
	
	public OpenSessionResponse(PlayerSessionId sessionId, Map<String, String> sessionProperties) {

		this.sessionId = sessionId;
		this.sessionProperties = sessionProperties;
	}

	public String getProperty(String key) {
		return sessionProperties.get(key);
	}

	public void setProperty(String key, String value) {
		sessionProperties.put(key, value);
	}
}
