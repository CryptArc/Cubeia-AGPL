package com.cubeia.poker.rng;

import java.io.Serializable;
import java.util.Random;

/**
 * Provides a random number generator.
 *
 * @author w
 */
public interface RNGProvider extends Serializable {
    public Random getRNG();
}
