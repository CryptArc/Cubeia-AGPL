package com.cubeia.poker;

import com.cubeia.poker.rng.RNGProvider;

import java.util.Random;

@SuppressWarnings("serial")
public class NonRandomRNGProvider extends Random implements RNGProvider {

    public NonRandomRNGProvider() {
    }

    @Override
    public Random getRNG() {
        return this;
    }

    @Override
    public int nextInt() {
        return 1;
    }

    @Override
    public int nextInt(int n) {
        return 1;
    }

    @Override
    public boolean nextBoolean() {
        return true;
    }
}
