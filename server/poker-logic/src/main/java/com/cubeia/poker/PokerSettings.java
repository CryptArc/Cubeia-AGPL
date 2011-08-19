package com.cubeia.poker;

import static com.cubeia.poker.gametypes.PokerVariant.TELESINA;

import com.cubeia.poker.gametypes.PokerVariant;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;

public class PokerSettings {
	private int anteLevel = -1;
	private TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();
	private PokerVariant variant = PokerVariant.TEXAS_HOLDEM;
	
	private PokerSettings() {}
	
	public PokerSettings(int anteLevel, TimingProfile timing, PokerVariant variant) {
		this.anteLevel = anteLevel;
		this.timing = timing;
		this.variant = variant;
	}
	
	public static PokerSettings createDefaultTexasHoldemSettings() {
		return new PokerSettings();
	}
	
	public static PokerSettings createDefaultTelesinaSettings() {
		return new PokerSettings(-1, TimingFactory.getRegistry().getDefaultTimingProfile(), TELESINA);
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
}
