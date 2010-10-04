package com.cubeia.poker.timing.impl;

import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.TimingRegistry;
import com.cubeia.poker.timing.Timings;

/**
 * Simple implementation of the TimingRegistry
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class TimingRegistryImpl implements TimingRegistry {

	private TimingProfile defaultProfile = new DefaultTimingProfile();
	
	public TimingProfile getDefaultTimingProfile() {
		return defaultProfile;
	}

	public TimingProfile getTimingProfile(Timings profile) {
		switch (profile) {
			case MINIMUM_DELAY:
				return new MinDelayTimingProfile();
				
			case EXPRESS:
                return new ExpressTimingProfile();
                
			case SUPER_EXPRESS:
                return new SuperExpressTimingProfile();
			
			default:
				return getDefaultTimingProfile();
		}
	}

}
