package com.cubeia.games.poker.tournament;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Represents the result after a poker hand has been played in a tournament.
 *
 */
public class PokerTournamentRoundReport implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Map<Integer, Long> balanceMap = new HashMap<Integer, Long>();
	
	public PokerTournamentRoundReport() {}
	
	public PokerTournamentRoundReport(Map<Integer, Long> balanceMap) {
        this.balanceMap = balanceMap;
    }

    public void setBalance(int playerId, long balance) {
		balanceMap.put(playerId, balance);
	}

	public Set<Entry<Integer, Long>> getBalances() {
		return balanceMap.entrySet();
	}
	
}
