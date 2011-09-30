package com.cubeia.backend.cashgame;

import java.util.concurrent.atomic.AtomicLong;


public class PlayerSessionIdImpl implements PlayerSessionId {

	private static final AtomicLong idGenerator = new AtomicLong(0);
	
	private final int playerId;
	private final long id;
	
	public PlayerSessionIdImpl(int playerId) {
		this.playerId = playerId;
		this.id = idGenerator.incrementAndGet();
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
