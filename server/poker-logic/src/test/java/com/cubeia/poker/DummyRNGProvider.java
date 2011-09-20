package com.cubeia.poker;

import java.util.Random;

import com.cubeia.poker.rng.RNGProvider;

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
