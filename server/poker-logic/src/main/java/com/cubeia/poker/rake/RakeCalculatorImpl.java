package com.cubeia.poker.rake;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cubeia.poker.RakeSettings;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

/**
 * @author w
 *
 */
public class RakeCalculatorImpl implements RakeCalculator {

    private final BigDecimal rakeFraction;
    private final BigDecimal rakeLimit;

    
    /**
     * Rake calculator with limit.
     * @param rakeFraction fraction (0.01 gives 1%)
     * @param rakeLimit rake limit
     */
    public RakeCalculatorImpl(RakeSettings rakeSettings) {
        this.rakeFraction = rakeSettings.getRakeFraction();
        this.rakeLimit = new BigDecimal(rakeSettings.getRakeLimit());
    }
    
    @Override
    public Map<Pot, BigDecimal> calculateRakes(BigDecimal totalCurrentRake, Collection<PotTransition> potTransitions) {
        Map<Pot, BigDecimal> potRakes = new HashMap<Pot, BigDecimal>();

        SortedMap<Pot, Integer> sortedPotBetSums = sortInPotIdOrder(aggregateBetsPerPot(potTransitions));
        
        for (Map.Entry<Pot, Integer> entry : sortedPotBetSums.entrySet()) {
            Pot pot = entry.getKey();
            int bets = entry.getValue();

            BigDecimal rake = rakeFraction.multiply(BigDecimal.valueOf(bets));
            
            if (rake.add(totalCurrentRake).compareTo(rakeLimit) > 0) {
                rake = BigDecimal.ZERO.max(rakeLimit.subtract(totalCurrentRake));
            }
            
            potRakes.put(pot, rake);
            totalCurrentRake = totalCurrentRake.add(rake);
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
