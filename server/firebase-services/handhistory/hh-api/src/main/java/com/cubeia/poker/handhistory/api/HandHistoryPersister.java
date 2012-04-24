package com.cubeia.poker.handhistory.api;

/**
 * Component for reading and writing hand history objects. At the
 * moment only complete hands are written, but this may change in the
 * future.
 * 
 * @author Lars J. Nilsson
 */
public interface HandHistoryPersister {
	
	/**
	 * @param id Id of hand to read, must not be null
	 * @return A hand, or null if not found
	 */
	public HistoricHand retrieve(HandIdentification id);

	
	/**
	 * @param hand Hand to persist, must not be null
	 */
	public void persist(HistoricHand hand);
	
}
