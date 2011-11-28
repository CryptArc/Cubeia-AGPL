package com.cubeia.poker.sitout;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;

public class SitoutCalculator {
	
	Logger log = Logger.getLogger(this.getClass());
	
	public Collection<PokerPlayer> checkTimeoutPlayers(PokerState state) {
		Collection<PokerPlayer> result = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : state.getSeatedPlayers()) {
        	if (player.isSittingOut()) {
        		Long timestamp = player.getSitOutTimestamp();
        		if (timestamp.longValue() + state.getSettings().getSitoutTimeLimitMilliseconds() < System.currentTimeMillis()) {
        			log.debug("Sitout timeout (will be forcibly removed) for player: "+player);
        			result.add(player);
        		}
        	}
        }
        return result;
	}
	
}
