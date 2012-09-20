package com.cubeia.games.poker.activator;

import java.util.Random;

import com.cubeia.poker.rng.RNGProvider;
import com.google.inject.Singleton;

@Singleton
public class DummyRNGProvider implements RNGProvider {
	
	private static final long serialVersionUID = -7885789555680247568L;
	
	public Random rng = new Random();
	
	@Override
	public Random getRNG() {
		return rng;
	}
}