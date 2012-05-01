package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * <p>Compare to another hand strength with hand ranking in mind,
 * i.e. the strongest hand should come first (is greater than a lesser hand).</p>
 * <p/>
 * This ordering is contrary to the contract specified by Comparator so take care!
 */
public class HandStrengthComparator implements Comparator<HandStrength> {

    @Override
    public int compare(HandStrength hs1, HandStrength hs2) {
        if (!hs2.getHandType().equals(hs1.getHandType())) {
            // Different hand types so only compare type
            return hs2.getHandType().ordinal() - hs1.getHandType().ordinal();

        } else {
            // Check highest card etc.
            if (hs2.getHighestRank() != hs1.getHighestRank()) {
                return hs2.getHighestRank().ordinal() - hs1.getHighestRank().ordinal();

            } else if (hs2.getSecondRank() != hs1.getSecondRank()) {
                return hs2.getSecondRank().ordinal() - hs1.getSecondRank().ordinal();

            } else {
                // Check kickers in descending order
                for (int i = 0; i < hs1.getKickerCards().size(); i++) {
                    if (hs2.getKickerCards().get(i).getRank() != hs1.getKickerCards().get(i).getRank()) {
                        return hs2.getKickerCards().get(i).getRank().ordinal() - hs1.getKickerCards().get(i).getRank().ordinal();
                    }
                }
            }
        }

        // Same strength
        return 0;
    }

}
