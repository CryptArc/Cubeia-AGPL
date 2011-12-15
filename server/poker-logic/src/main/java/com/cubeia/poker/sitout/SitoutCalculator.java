package com.cubeia.poker.sitout;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.cubeia.poker.player.PokerPlayer;

public class SitoutCalculator {
	
	Logger log = Logger.getLogger(this.getClass());
	
	public Collection<PokerPlayer> checkTimeoutPlayers(Collection<PokerPlayer> seatedPlayers, long sitoutTimeLimitMillis) {
		Collection<PokerPlayer> result = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : seatedPlayers) {
        	if (player.isSittingOut()) {
        		Long timestamp = player.getSitOutTimestamp();
                if (timestamp.longValue() + sitoutTimeLimitMillis < System.currentTimeMillis()) {
        			log.debug("Sitout timeout (will be forcibly removed) for player: "+player);
        			result.add(player);
        		}
        	}
        }
        return result;
	}
	
}
