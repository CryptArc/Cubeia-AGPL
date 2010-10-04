package com.cubeia.games.poker.tournament.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cubeia.poker.timing.Timings;

public class PokerTournamentState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
    private static transient Logger log = Logger.getLogger(PokerTournamentState.class);
	
	private Timings timing = Timings.DEFAULT;
	
	private int tablesToCreate;
	
	private PokerTournamentStatus status = PokerTournamentStatus.ANNOUNCED;
	
	
	// Timestamps for profiling
	private long firstRegisteredTime = 0;
	private long lastRegisteredTime = 0;
	
	
	/** Maps playerId to balance */
	private Map<Integer, Long> balances = new HashMap<Integer, Long>();
	
	
	public boolean allTablesHaveBeenCreated(int tablesCreated) {
		return tablesCreated >= tablesToCreate;
	}

	public void setTablesToCreate(int tablesToCreate) {
		this.tablesToCreate = tablesToCreate;
	}

	public Long getPlayerBalance(int playerId) {
		return balances.get(playerId);
	}

	public void setBalance(int playerId, long balance) {
		balances.put(playerId, balance);
	}

	public PokerTournamentStatus getStatus() {
		return status;
	}

	public void setStatus(PokerTournamentStatus status) {
		this.status = status;
	}

    public Timings getTiming() {
        return timing;
    }

    public void setTiming(Timings timing) {
        this.timing = timing;
    }

    public long getFirstRegisteredTime() {
        return firstRegisteredTime;
    }

    public void setFirstRegisteredTime(long firstRegisteredTime) {
        this.firstRegisteredTime = firstRegisteredTime;
    }

    public long getLastRegisteredTime() {
        return lastRegisteredTime;
    }

    public void setLastRegisteredTime(long lastRegisteredTime) {
        this.lastRegisteredTime = lastRegisteredTime;
    }
	
}
