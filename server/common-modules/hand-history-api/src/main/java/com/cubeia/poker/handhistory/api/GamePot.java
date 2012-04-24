package com.cubeia.poker.handhistory.api;

import java.util.HashSet;
import java.util.Set;

public class GamePot {

	private final int potId;
	private final Set<Integer> players = new HashSet<Integer>();
	
	private long potSize;
	
	public GamePot(int potId) {
		this.potId = potId;
	}
	
	public GamePot(int potId, long potSize, Integer...plyrs) {
		this.potId = potId;
		this.potSize = potSize;
		for (int id : plyrs) {
			players.add(id);
		}
	}

	public int getPotId() {
		return potId;
	}
	
	public Set<Integer> getPlayers() {
		return players;
	}
	
	public long getPotSize() {
		return potSize;
	}
	
	public void setPotSize(long potSize) {
		this.potSize = potSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((players == null) ? 0 : players.hashCode());
		result = prime * result + potId;
		result = prime * result + (int) (potSize ^ (potSize >>> 32));
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
		GamePot other = (GamePot) obj;
		if (players == null) {
			if (other.players != null)
				return false;
		} else if (!players.equals(other.players))
			return false;
		if (potId != other.potId)
			return false;
		if (potSize != other.potSize)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GamePot [potId=" + potId + ", players=" + players + ", potSize=" + potSize + "]";
	}
}
