package com.cubeia.poker.rake;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

public class RakeCalculatorImpl implements RakeCalculator {

    private final BigDecimal rakeFraction;

    public RakeCalculatorImpl(BigDecimal rakeFraction) {
        this.rakeFraction = rakeFraction;
    }
    
    @Override
    public Map<Pot, Integer> calculateRakes(Collection<PotTransition> potTransitions) {
        Map<Pot, Integer> potRakes = new HashMap<Pot, Integer>();

        for (Map.Entry<Pot, Integer> entry : aggregateBetsPerPot(potTransitions).entrySet()) {
            int rake = rakeFraction.multiply(BigDecimal.valueOf(entry.getValue())).intValue();
            potRakes.put(entry.getKey(), rake);
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
    
//    public RakeInfoContainer calculateRake(Collection<Pot> pots) {
//
//        int totalPot = 0;
//        
//        for (Pot pot : pots) {
//            totalPot += pot.getPotSize();
//        }
//        
//        
//        
//        int totalRake;
//        return new RakeInfoContainer(totalPot, -1);
//    }

}
