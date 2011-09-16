package com.cubeia.poker;

import java.util.Random;

import com.cubeia.poker.rng.RNGProvider;

@SuppressWarnings("serial")
public class TestRNGProvider implements RNGProvider {

    private final Random rng;
    
    public TestRNGProvider() {
        this.rng = new Random();
    }
    
    public TestRNGProvider(Random rng) {
        this.rng = rng;
    }
    
    @Override
    public Random getRNG() {
        return rng;
    }
}
