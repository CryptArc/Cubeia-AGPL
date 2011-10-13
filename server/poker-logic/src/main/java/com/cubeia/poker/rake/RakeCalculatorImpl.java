package com.cubeia.poker.rake;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

/**
 * @author w
 *
 */
public class RakeCalculatorImpl implements RakeCalculator {

    private final BigDecimal rakeFraction;
    private final long rakeLimit;

    
    /**
     * Rake calculator with no limit.
     * @param rakeFraction fraction (0.01 gives 1%)
     */
    public RakeCalculatorImpl(BigDecimal rakeFraction) {
        this(rakeFraction, Long.MAX_VALUE);
    }
    
    /**
     * Rake calculator with limit.
     * @param rakeFraction fraction (0.01 gives 1%)
     * @param rakeLimit rake limit
     */
    public RakeCalculatorImpl(BigDecimal rakeFraction, long rakeLimit) {
        this.rakeFraction = rakeFraction;
        this.rakeLimit = rakeLimit;
    }
    
    @Override
    public Map<Pot, Integer> calculateRakes(long totalCurrentRake, Collection<PotTransition> potTransitions) {
        Map<Pot, Integer> potRakes = new HashMap<Pot, Integer>();

        SortedMap<Pot, Integer> sortedPotBetSums = sortInPotIdOrder(aggregateBetsPerPot(potTransitions));
        
        for (Map.Entry<Pot, Integer> entry : sortedPotBetSums.entrySet()) {
            Pot pot = entry.getKey();
            Integer bets = entry.getValue();

            long rake = rakeFraction.multiply(BigDecimal.valueOf(bets)).intValue();
            
            if (rake + totalCurrentRake > rakeLimit) {
                rake = Math.max(0, rakeLimit - totalCurrentRake); 
            }
            
            potRakes.put(pot, (int) rake);
            totalCurrentRake += rake;
        }
        
        return potRakes;
    }

    private Map<Pot, Integer> aggregateBetsPerPot(Collection<PotTransition> potTransitions) {
        Map<Pot, Integer> potBetSums = new HashMap<Pot, Integer>();
        for (PotTransition trans : potTransitions) {
            Pot pot = trans.getPot();
            if (!potBetSums.containsKey(pot)) {
                potBetSums.put(pot, 0);
            }
            potBetSums.put(pot, potBetSums.get(pot) + (int) trans.getAmount());
        }
        
        return potBetSums;
    }
    
    private SortedMap<Pot, Integer> sortInPotIdOrder(Map<Pot, Integer> potsToRake) {
        TreeMap<Pot, Integer> sortedMap = new TreeMap<Pot, Integer>(new Comparator<Pot>() {
            @Override
            public int compare(Pot p1, Pot p2) {
                return p1.getId() - p2.getId();
            }
        });
        sortedMap.putAll(potsToRake);
        return sortedMap;
    }
    
    @Override
    public String toString() {
        return "rake fraction = " + rakeFraction + ", rake limit = " + rakeLimit;
    }
}
