package com.cubeia.poker;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.variant.PokerVariant;

public interface IPokerState {

	void init(RNGProvider rngProvider, PokerSettings settings);
	
	void notifyPlayerSittingOut(int playerId);

	int getAnteLevel();

	Map<Integer, PokerPlayer> getCurrentHandPlayerMap();

	SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap();

	PokerPlayer getPlayerInCurrentHand(Integer playerId);

	int countNonFoldedPlayers();

	boolean isPlayerInHand(int playerId);

	void notifyDealerButton(int dealerButtonSeatId);

	PokerVariant getPokerVariant();
	
	List<Card> getCommunityCards();
}