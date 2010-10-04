package com.cubeia.poker.timing.impl;

import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;

public class ExpressTimingProfile implements TimingProfile {

	private static final long serialVersionUID = 4317305784161455439L;

	public String toString() {
        return "ExpressTimingProfile";
    }
    
    public long getTime(Periods period) {
        switch (period) {
            case POCKET_CARDS:
                return 2000;
            case FLOP:
                return 2000;
            case TURN:
                return 2000;
            case RIVER:
                return 2000;
            case START_NEW_HAND:
                return 4000;
            case ACTION_TIMEOUT:
                return 5000;
            case AUTO_POST_BLIND_DELAY:
                return 200;
            case LATENCY_GRACE_PERIOD:
				return 500;
            default:
                return 800;
        }
    }

}
