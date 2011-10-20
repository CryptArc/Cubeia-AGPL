package com.cubeia.poker.rake;

import java.util.Collection;

import com.cubeia.poker.pot.Pot;

public interface RakeCalculator {
    
    /**
     * Calculates the rakes for the given pots.
     * @param pots pots to calculate rake for
     * @return the calculated rakes per pot, total rake and total bets (pot sizes)
     */
    RakeInfoContainer calculateRakes(Collection<Pot> pots);
    
    
}