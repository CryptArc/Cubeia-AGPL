package com.cubeia.poker.hand.calculator;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;

/**
 * <p>Inspect and calculate what poker hands are implemented in a Hand.</p>
 * <p/>
 * <p>Calculates the best hand strength given a poker hand.
 * The calculator does not require 5 cards specifically, but
 * if you provide more or less it might produce unpredictive results.</p>
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public interface HandCalculator {

    /**
     * <p>Get the hand strength representation for the given hand.</p>
     *
     * @param hand, cannot be null
     * @return HandStrength, never null
     */
    public HandStrength getHandStrength(Hand hand);

}