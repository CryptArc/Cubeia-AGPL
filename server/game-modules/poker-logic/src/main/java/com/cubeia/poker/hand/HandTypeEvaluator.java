package com.cubeia.poker.hand;

import java.util.Comparator;

public interface HandTypeEvaluator {

    /**
     * Return the best possible hand given context constraints possible
     * Valid inputs may have any number of cards on hand, eg none, 5 or 7 cards.
     *
     * @param hand the Hand to evaluate
     * @return Info holding details on the best hand that can be assembled using
     *         the given cards.
     */
    public HandInfo getBestHandInfo(Hand hand);

    Comparator<Hand> createHandComparator(int playersInPot);


}
