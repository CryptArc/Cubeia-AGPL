package com.cubeia.backend.cashgame;

public class PlayerSessionId {

	public final long id;

	public PlayerSessionId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof PlayerSessionId)) {
			return false;
		}

		return id == ((PlayerSessionId) obj).id;
	}
}
