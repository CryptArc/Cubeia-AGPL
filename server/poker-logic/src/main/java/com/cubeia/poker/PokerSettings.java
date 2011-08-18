package com.cubeia.poker;

import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;

public class PokerSettings {
	public int anteLevel = -1;
	public TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();
}
