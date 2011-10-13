package com.cubeia.poker.handhistory.api;

import java.util.HashSet;
import java.util.Set;

public class GamePot {

	private final int potId;
	private final Set<Integer> players = new HashSet<Integer>();
	
	private long potSize;
	
	public GamePot(int podId) {
		potId = podId;
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
}
