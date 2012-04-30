package com.cubeia.poker.handhistory.api;

import java.util.List;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;

/**
 * Service contract for collecting hand history events. The service
 * is assumed to keep hand state until the hand is finished and is 
 * responsible for persisting the hand when it is stopped. 
 * 
 * @author Lars J. Nilsson
 */
public interface HandHistoryCollectorService extends Contract {

	/**
	 * @param id Hand id, must not be null
	 * @param seats Players in hand, must not be null
	 */
	public void startHand(HandIdentification id, List<Player> seats);
	
	/**
	 * @param tableId Firebase table id
	 * @param event Event to report, must not be null
	 */
	public void reportEvent(int tableId, HandHistoryEvent event);
	
	/**
	 * @param tableId Firebase table id
	 * @param deckInfo Deck information, must not be null
	 */
	public void reportDeckInfo(int tableId, DeckInfo deckInfo);

	/**
	 * @param tableId Firebase table id
	 * @param res Hand results, must not be null
	 */
	public void reportResults(int tableId, Results res);
	
	/**
	 * @param tableId Firebase table id
	 */
	public void stopHand(int tableId);

	
	/**
	 * @param tableId Firebase table id
	 */
	public void cancelHand(int tableId);
	
}
