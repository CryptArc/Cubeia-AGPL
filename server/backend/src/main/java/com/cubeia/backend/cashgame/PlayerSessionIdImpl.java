package com.cubeia.backend.cashgame;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;


@SuppressWarnings("serial")
public class PlayerSessionIdImpl implements PlayerSessionId, Serializable {

	private static final AtomicLong idGenerator = new AtomicLong(0);
	
	private final int playerId;
	private final long id;
	
	public PlayerSessionIdImpl(int playerId) {
		this.playerId = playerId;
		this.id = idGenerator.incrementAndGet();
	}
	
	public PlayerSessionIdImpl(int playerId, long sessionId) {
        this.playerId = playerId;
        this.id = sessionId;
	}
	
	public long getSessionId() {
	    return id;
	}
	
	@Override
	public int getPlayerId() {
		return playerId;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (! (obj instanceof PlayerSessionIdImpl)) {
			return false;
		}
		
		return id == ((PlayerSessionIdImpl) obj).id;
	}
	
	@Override
	public String toString() {
		return "PlayerSessionId(" + id + ")";
	}
}
