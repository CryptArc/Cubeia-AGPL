package com.cubeia.poker;

import java.util.Map;
import java.util.SortedMap;

import com.cubeia.poker.player.PokerPlayer;

public interface IPokerState {

	void init(PokerSettings settings);
	
	void notifyPlayerSittingOut(int playerId);

	int getAnteLevel();

	Map<Integer, PokerPlayer> getCurrentHandPlayerMap();

	SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap();

	PokerPlayer getPlayerInCurrentHand(Integer playerId);

	int countNonFoldedPlayers();

	boolean isPlayerInHand(int playerId);

}