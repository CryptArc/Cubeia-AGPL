package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * <p>Compare to another hand strength with hand ranking in mind,
 * i.e. the strongest hand should come first (is greater than a lesser hand).</p>
 * 
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
    
/*
    @Override
    public int compareTo(HandStrength other) {
        if (!other.getHandType().equals(type)) {
            // Different hand types so only compare type
            return other.getHandType().ordinal() - type.ordinal();
            
        } else {
            // Check highest card etc.
            if (other.getHighestRank() != highestRank) {
                return other.getHighestRank().ordinal() - highestRank.ordinal();
                
            } else if (other.getSecondRank() != secondRank) {
                return other.getSecondRank().ordinal() - secondRank.ordinal();
                
            } else {
                // Check kickers in descending order
                for (int i = 0; i < kickerCards.size(); i++) {
                    if (other.getKickerCards().get(i).getRank() != kickerCards.get(i).getRank()) {
                        return other.getKickerCards().get(i).getRank().ordinal() - kickerCards.get(i).getRank().ordinal();
                    }
                }
            }
        }
        
        // Same strength
        return 0;
    }
     */

}
