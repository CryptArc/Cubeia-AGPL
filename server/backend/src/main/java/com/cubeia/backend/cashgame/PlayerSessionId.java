package com.cubeia.backend.cashgame;

public class PlayerSessionId {

	private final long sessionId;
	
	private final int playerId;

	public PlayerSessionId(long sessionId, int playerId) {
		this.sessionId = sessionId;
        this.playerId = playerId;
	}

	public long getSessionId() {
		return sessionId;
	}
	
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + playerId;
        result = prime * result + (int) (sessionId ^ (sessionId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerSessionId other = (PlayerSessionId) obj;
        if (playerId != other.playerId)
            return false;
        if (sessionId != other.sessionId)
            return false;
        return true;
    }

}
