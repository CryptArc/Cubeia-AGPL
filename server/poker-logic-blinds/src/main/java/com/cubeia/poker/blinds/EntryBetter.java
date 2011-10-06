package com.cubeia.poker.blinds;

/**
 * Describes a player who should pay the entry bet.
 * 
 * @author viktor
 *
 */
public class EntryBetter {

	private final BlindsPlayer player;
	
	private final EntryBetType entryBetType;
	
	public EntryBetter(BlindsPlayer player, EntryBetType entryBetType) {
		super();
		this.player = player;
		this.entryBetType = entryBetType;
	}

	public BlindsPlayer getPlayer() {
		return player;
	}

	public EntryBetType getEntryBetType() {
		return entryBetType;
	}
	
	
}
