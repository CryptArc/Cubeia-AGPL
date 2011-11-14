package com.cubeia.poker.rake;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.RakeSettings;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;

/**
 * Rake calculator where rake is linear (defined by a fraction) up to a limit after which
 * no more rake is taken.
 * @author w
 *
 */
@SuppressWarnings("serial")
public class LinearRakeWithLimitCalculator implements RakeCalculator {

    private final BigDecimal rakeFraction;
    private final BigDecimal rakeLimit;
    private final BigDecimal rakeLimitHeadsUp;
    
    /**
     * Rake calculator with limit.
     * @param rakeFraction fraction (0.01 gives 1%)
     * @param rakeLimit rake limit
     */
    public LinearRakeWithLimitCalculator(RakeSettings rakeSettings) {
        this.rakeFraction = rakeSettings.getRakeFraction();
        this.rakeLimit = new BigDecimal(rakeSettings.getRakeLimit());
        this.rakeLimitHeadsUp = new BigDecimal(rakeSettings.getRakeLimitHeadsUp());
    }
    
    @Override
    public RakeInfoContainer calculateRakes(Collection<Pot> pots, boolean tableHasSeenAction) {
        Map<Pot, BigDecimal> potRake = new HashMap<Pot, BigDecimal>();

        BigDecimal limit = countPlayers(pots) == 2 ? rakeLimitHeadsUp : rakeLimit;
        
        List<Pot> potsSortedById = sortPotsInIdOrder(pots);
        
        BigDecimal totalRake = BigDecimal.ZERO;
        int totalPot = 0;
        
        for (Pot pot : potsSortedById) {
            long potSize = pot.getPotSize();
            
            BigDecimal rake = BigDecimal.ZERO;
            if (tableHasSeenAction) {
                rake = rakeFraction.multiply(new BigDecimal(potSize));
                if (willRakeAdditionBreakLimit(totalRake, rake)) {
                    rake = limit.subtract(totalRake);
                }
                totalRake = totalRake.add(rake);
            }
            
            totalPot += potSize;
            potRake.put(pot, rake);
        }
        
        return new RakeInfoContainer(totalPot, totalRake.intValue(), potRake);
    }

    private int countPlayers(Collection<Pot> pots) {
        HashSet<PokerPlayer> players = new HashSet<PokerPlayer>();
        for (Pot pot : pots) {
            players.addAll(pot.getPotContributors().keySet());
        }
        return players.size();
    }

    /**
     * Returns a new list where the pots are ordered by ascending pot id.
     * @param pots pots to sort
     * @return new sorted list
     */
    private List<Pot> sortPotsInIdOrder(Collection<Pot> pots) {
        List<Pot> potsSortedById = new ArrayList<Pot>(pots);
        Collections.sort(potsSortedById, new Comparator<Pot>() {
            @Override public int compare(Pot p1, Pot p2) { return p1.getId() - p2.getId(); }
        });
        return potsSortedById;
    }

    private boolean willRakeAdditionBreakLimit(BigDecimal totalRake, BigDecimal rakeAddition) {
        return totalRake.add(rakeAddition).compareTo(rakeLimit) > 0;
    }
    
    
    @Override
    public String toString() {
        return "rake fraction = " + rakeFraction + ", rake limit = " + rakeLimit;
    }
}
