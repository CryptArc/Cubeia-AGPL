package poker.gs.server.blinds;

import java.util.List;

/**
 * Interface for providing a random seatId.
 * 
 * Used when moving the dealer button to a random seat.
 * 
 */
public interface RandomSeatProvider {

	/**
	 * Returns a random seat id of the given seat ids.
	 * 
	 * @param availableSeatIds the list of available seat ids
	 * 
	 * @return A random seat id of the given seat ids. The result must be one of the values in the given list
	 */
	public int getRandomSeatId(List<Integer> availableSeatIds);
}
