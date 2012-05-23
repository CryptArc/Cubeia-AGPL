package com.cubeia.poker.pot.rake;

import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;

import java.math.BigDecimal;
import java.util.*;

/**
 * Rake calculator where rake is linear (defined by a fraction) up to a limit after which
 * no more rake is taken.
 *
 * @author w
 */
@SuppressWarnings("serial")
public class LinearRakeWithLimitCalculator implements RakeCalculator {

    private final BigDecimal rakeFraction;
    private final long rakeLimit;
    private final long rakeLimitHeadsUp;

    /**
     * Rake calculator with limit.
     *
     * @param rakeFraction fraction (0.01 gives 1%)
     * @param rakeLimit    rake limit
     */
    public LinearRakeWithLimitCalculator(RakeSettings rakeSettings) {
        this.rakeFraction = rakeSettings.getRakeFraction();
        this.rakeLimit = rakeSettings.getRakeLimit();
        this.rakeLimitHeadsUp = rakeSettings.getRakeLimitHeadsUp();
    }

    @Override
    public RakeInfoContainer calculateRakes(Collection<Pot> pots, boolean tableHasSeenAction) {
        Map<Pot, Long> potRake = new HashMap<Pot, Long>();

        long limit = countPlayers(pots) == 2 ? rakeLimitHeadsUp : rakeLimit;

        List<Pot> potsSortedById = sortPotsInIdOrder(pots);

        long totalRake = 0L;
        long totalPot = 0L;

        for (Pot pot : potsSortedById) {
            long potSize = pot.getPotSize();

            long rake = 0L;
            if (tableHasSeenAction) {
                rake = rakeFraction.multiply(new BigDecimal(potSize)).longValue();
                if (willRakeAdditionBreakLimit(totalRake, rake, limit)) {
                    rake = limit - totalRake;
                }
                totalRake += rake;
            }

            totalPot += potSize;
            potRake.put(pot, rake);
        }

        return new RakeInfoContainer(totalPot, totalRake, potRake);
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
     *
     * @param pots pots to sort
     * @return new sorted list
     */
    private List<Pot> sortPotsInIdOrder(Collection<Pot> pots) {
        List<Pot> potsSortedById = new ArrayList<Pot>(pots);
        Collections.sort(potsSortedById, new Comparator<Pot>() {
            @Override
            public int compare(Pot p1, Pot p2) {
                return p1.getId() - p2.getId();
            }
        });
        return potsSortedById;
    }

    private boolean willRakeAdditionBreakLimit(long totalRake, long rakeAddition, long limit) {
        return totalRake + rakeAddition > limit;
    }


    @Override
    public String toString() {
        return "rake fraction = " + rakeFraction + ", rake limit = " + rakeLimit;
    }
}
