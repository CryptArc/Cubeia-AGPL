package com.cubeia.poker.timing;

import java.io.Serializable;

public interface TimingProfile extends Serializable {
	
	public long getTime(Periods period);
	
}
