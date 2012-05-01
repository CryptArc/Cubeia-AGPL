package com.cubeia.poker;

import com.cubeia.poker.rng.RNGProvider;

import java.util.Random;

@SuppressWarnings("serial")
public class DummyRNGProvider implements RNGProvider {

    private final Random rng;

    public DummyRNGProvider() {
        this.rng = new Random();
    }

    public DummyRNGProvider(Random rng) {
        this.rng = rng;
    }

    @Override
    public Random getRNG() {
        return rng;
    }
}
