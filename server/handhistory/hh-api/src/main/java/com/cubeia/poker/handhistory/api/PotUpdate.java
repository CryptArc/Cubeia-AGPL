package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PotUpdate extends HandHistoryEvent {

	private final List<GamePot> pots = new LinkedList<GamePot>();
	
	public PotUpdate() { }
	
	public List<GamePot> getPots() {
		return pots;
	}
}
