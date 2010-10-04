package com.cubeia.poker.timing.impl;

import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;

public class SuperExpressTimingProfile implements TimingProfile {

	private static final long serialVersionUID = 5998534828390461865L;

	public String toString() {
        return "SuperExpressTimingProfile";
    }
    
    public long getTime(Periods period) {
        switch (period) {
            case POCKET_CARDS:
                return 500;
            case FLOP:
                return 500;
            case TURN:
                return 500;
            case RIVER:
                return 500;
            case START_NEW_HAND:
                return 2000;
            case ACTION_TIMEOUT:
                return 2000;
            case AUTO_POST_BLIND_DELAY:
                return 100;
            case LATENCY_GRACE_PERIOD:
				return 200;
            default:
                return 500;
        }
    }

}
