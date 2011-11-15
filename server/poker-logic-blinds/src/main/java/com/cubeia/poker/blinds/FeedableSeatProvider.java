package com.cubeia.poker.blinds;

import java.util.List;
import java.util.Random;

/**
 * A {@link RandomSeatProvider} that can be fed with values in two ways. Either, the feedNextSeatId can be
 * used, or a mocked randomizer can be injected.
 * 
 */
public class FeedableSeatProvider implements RandomSeatProvider {

	/** Used for fetching random values. */
	private Random randomizer;

	/** The next "random" seat id to return. */ 
	int nextSeatId = -1;	
	
	// TODO: break out RNG, or remove this method
	public FeedableSeatProvider() {
		this(new Random());
	}
	
	public FeedableSeatProvider(Random randomizer) {
		this.randomizer = randomizer;
	}
	
	
	/**
	 * Feeds the next seat id to return.
	 * 
	 * After the next call to getRandomSeatId, this will be reset.
	 * 
	 * @param nextSeatId
	 */
	public void feedNextSeatId(int nextSeatId) {
		this.nextSeatId = nextSeatId;
	}
	
	/**
	 * Gets a "random" seat id. If we have a nextSeatId, that seat id is returned, otherwise randomizes 
	 * a seat id in the given collection.
	 * 
	 * If the fed nextSeatId is not contained in the list of available seat ids, a random seat id is returned instead.
	 * 
	 */
	public int getRandomSeatId(List<Integer> availableSeatIds) {
		final int randomSeatId;
		if (nextSeatId != -1 && availableSeatIds.contains(nextSeatId)) {
			randomSeatId = nextSeatId;
			nextSeatId = -1;
		} else {
			final Integer seatIds[] = new  Integer[availableSeatIds.size()]; 
			availableSeatIds.toArray(seatIds);
			final int index = randomizer.nextInt(availableSeatIds.size());
			randomSeatId = seatIds[index];			
		}
		return randomSeatId;
	}

}
