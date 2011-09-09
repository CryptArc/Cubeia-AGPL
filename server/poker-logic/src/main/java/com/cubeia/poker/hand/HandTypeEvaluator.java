package com.cubeia.poker.hand;

public interface HandTypeEvaluator {

	/**
	 * Return the best possible hand type given context constraints possible
	 * to assemble using a players hand. Valid inputs may have any number of
	 * cards on hand, eg none, 5 or 7 cards.
	 * 
	 * @param hand the Hand to evaluate
	 * 
	 * @return The best HandType that can be assembled given the rules of the
	 * game and the given cards.
	 */
	public HandType getHandType(Hand hand);
}
