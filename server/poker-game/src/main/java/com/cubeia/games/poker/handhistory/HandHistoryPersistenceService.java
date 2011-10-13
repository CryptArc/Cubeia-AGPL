package com.cubeia.games.poker.handhistory;

import java.util.List;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;

public interface HandHistoryPersistenceService extends Contract {

	public void startHand(int tableId, String handId, List<Player> seats);
	
	public void reportEvent(int tableId, HandHistoryEvent event);
	
	public void reportDeckInfo(int tableId, DeckInfo deckInfo);

	public void reportResults(int tableId, Results res);
	
	public void stopHand(int tableId);

	public void cancelHand(int tableId);
	
}
