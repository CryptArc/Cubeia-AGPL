package com.cubeia.poker;

import static com.cubeia.poker.gametypes.PokerVariant.TELESINA;

import com.cubeia.poker.gametypes.PokerVariant;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;

public class PokerSettings {
	private final int anteLevel;
	private final TimingProfile timing;
	private final PokerVariant variant;
    private final int tableSize;
	
//	private PokerSettings() {
//	    this(-1, TimingFactory.getRegistry().getDefaultTimingProfile(), PokerVariant.TEXAS_HOLDEM, 6);
//	}
	
	public PokerSettings(int anteLevel, TimingProfile timing, PokerVariant variant, int tableSize) {
		this.anteLevel = anteLevel;
		this.timing = timing;
		this.variant = variant;
        this.tableSize = tableSize;
	}
	
//	public static PokerSettings createDefaultTexasHoldemSettings() {
//		return new PokerSettings();
//	}
	
//	public static PokerSettings createDefaultTelesinaSettings() {
//		return new PokerSettings(-1, TimingFactory.getRegistry().getDefaultTimingProfile(), TELESINA);
//	}

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
