package com.cubeia.games.poker.handhistory;

import java.util.List;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;

public interface HandHistoryCollectorService extends Contract {

	public void startHand(HandIdentification id, List<Player> seats);
	
	public void reportEvent(int tableId, HandHistoryEvent event);
	
	public void reportDeckInfo(int tableId, DeckInfo deckInfo);

	public void reportResults(int tableId, long totalRake, Results res);
	
	public void stopHand(int tableId);

	public void cancelHand(int tableId);
	
}
