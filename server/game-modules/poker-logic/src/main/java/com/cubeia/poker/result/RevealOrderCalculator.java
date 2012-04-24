package com.cubeia.poker.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;

public class RevealOrderCalculator {

	/**
	 * Calculate the order in which the players should reveal their hidden card(s).
	 * @param currentHandSeatingMap seating map
	 * @param lastPlayerToBeCalled last called player, can be null
	 * @param dealerButtonPlayer player at the dealer button, never null
	 * @return list of player id:s, never null
	 */
	public List<Integer> calculateRevealOrder(SortedMap<Integer, PokerPlayer> currentHandSeatingMap, PokerPlayer lastPlayerToBeCalled, PokerPlayer dealerButtonPlayer) {
		Integer startPlayerSeat;
		
		if (lastPlayerToBeCalled != null) {
			startPlayerSeat = getSeatByPlayer(currentHandSeatingMap, lastPlayerToBeCalled);
		} else {
			ArrayList<PokerPlayer> playerList = new ArrayList<PokerPlayer>(currentHandSeatingMap.values());
			int dealerButtonPlayerIndex = playerList.indexOf(dealerButtonPlayer);
			if (dealerButtonPlayerIndex == playerList.size() - 1) {
				startPlayerSeat = getSeatByPlayer(currentHandSeatingMap, playerList.get(0));
			} else {
				startPlayerSeat = getSeatByPlayer(currentHandSeatingMap, playerList.get(dealerButtonPlayerIndex + 1));
			}
		}
		
		List<PokerPlayer> sortedPlayerList = PokerUtils.unwrapList(currentHandSeatingMap, startPlayerSeat);

		List<Integer> playerIdList = new ArrayList<Integer>();
		for (PokerPlayer player : sortedPlayerList) {
			if(!player.hasFolded()){
				playerIdList.add(player.getId());
			}
		}
		
		return playerIdList;
	}

	private Integer getSeatByPlayer(SortedMap<Integer, PokerPlayer> currentHandSeatingMap, PokerPlayer lastPlayerToBeCalled) {
		for (Map.Entry<Integer, PokerPlayer> entry : currentHandSeatingMap.entrySet()) {
			if (entry.getValue().equals(lastPlayerToBeCalled)) {
				return entry.getKey();
			}
		}
		return null;
	}


}
