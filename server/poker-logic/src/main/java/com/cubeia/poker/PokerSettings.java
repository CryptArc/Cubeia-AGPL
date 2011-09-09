package com.cubeia.poker;

import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

public class PokerSettings {
	private final int anteLevel;
	private final TimingProfile timing;
	private final PokerVariant variant;
    private final int tableSize;
	
	public PokerSettings(int anteLevel, TimingProfile timing, PokerVariant variant, int tableSize) {
		this.anteLevel = anteLevel;
		this.timing = timing;
		this.variant = variant;
        this.tableSize = tableSize;
	}
	
	public int getAnteLevel() {
		return anteLevel;
	}

	public TimingProfile getTiming() {
		return timing;
	}

	public PokerVariant getVariant() {
		return variant;
	}
	
	public int getTableSize() {
        return tableSize;
    }
}
