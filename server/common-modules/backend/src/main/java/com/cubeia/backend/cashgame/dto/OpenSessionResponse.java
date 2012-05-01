package com.cubeia.backend.cashgame.dto;

import com.cubeia.backend.cashgame.PlayerSessionId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class OpenSessionResponse implements Serializable {
    private final PlayerSessionId sessionId;
    private final Map<String, String> sessionProperties;

    public OpenSessionResponse(PlayerSessionId sessionId, Map<String, String> sessionProperties) {

        this.sessionId = sessionId;
        this.sessionProperties = sessionProperties;
    }

    public String getProperty(String key) {
        return getSessionProperties().get(key);
    }

    public void setProperty(String key, String value) {
        sessionProperties.put(key, value);
    }

    public PlayerSessionId getSessionId() {
        return sessionId;
    }

    public Map<String, String> getSessionProperties() {
        return new HashMap<String, String>(sessionProperties);
    }
}
