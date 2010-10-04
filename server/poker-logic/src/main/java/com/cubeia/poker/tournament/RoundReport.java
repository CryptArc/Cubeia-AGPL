package com.cubeia.poker.tournament;

import java.util.HashMap;
import java.util.Map;

public class RoundReport {

    private Map<Integer, Long> balanceMap = new HashMap<Integer, Long>();
    
    public void setSetBalance(int playerId, long balance) {
        balanceMap.put(playerId, balance);
    }

    public Map<Integer, Long> getBalanceMap() {
        return balanceMap;
    }
    
    @Override
    public String toString() {
    	return "RoundReport: "+balanceMap;
    }
}
