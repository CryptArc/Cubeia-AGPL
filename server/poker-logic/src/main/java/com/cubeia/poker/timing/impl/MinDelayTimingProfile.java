package com.cubeia.poker.timing.impl;

import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;

/**
 * Minimum delay. To be used with unit tests and automated stuff that does not 
 * want to wait the proper timeouts.
 * 
 * All timeouts are set to 100ms
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class MinDelayTimingProfile implements TimingProfile {

	private static final long serialVersionUID = 5616827479149407827L;

	public String toString() {
        return "MinDelayTimingProfile";
    }
    
	public long getTime(Periods period) {
		switch (period) {
			default:
				return 100;
		}
	}

}
