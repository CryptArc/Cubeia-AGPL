package com.cubeia.game.poker.util;

import java.util.Random;

/**
 * FIXME: This is a duplicate of the Arithmetic class in the
 * bots project. But if I depend on it I get cyclic dependency in
 * maven.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class Arithmetic {

    private static Random rng = new Random();

    // .. removed methods

    /**
     * Returns a gaussian average for the given mean and deviation.
     *
     * @param mean
     * @param deviation
     * @return result, may be negative
     */
    public static int gaussianAverage(int mean, int deviation) {
        float g = (float) rng.nextGaussian();
        g = g * (float) deviation;
        return mean + (int) g;
    }

}
