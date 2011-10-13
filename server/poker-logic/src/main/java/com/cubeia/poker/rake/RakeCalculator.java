package com.cubeia.poker.rake;

import java.util.Collection;
import java.util.Map;

import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

public interface RakeCalculator {

    /**
     * Calculates the added rakes by the given pot transitions.
     * @param totalCurrentRake total rake before appending the given pot transitions
     * @param potTransitions pot transitions to calculate rake contribution for
     * @return pot to rake map 
     */
    Map<Pot, Integer> calculateRakes(long totalCurrentRake, Collection<PotTransition> potTransitions);

}