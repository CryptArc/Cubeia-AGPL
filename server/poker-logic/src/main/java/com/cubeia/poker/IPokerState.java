package com.cubeia.poker;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.states.PlayingSTM;
import com.cubeia.poker.variant.PokerVariant;

public interface IPokerState {

	void init(RNGProvider rngProvider, PokerSettings settings);
	
	void notifyPlayerSittingOut(int playerId);
	
	void playerIsSittingOut(int playerId, SitOutStatus misssedAnte);

	int getAnteLevel();

	Map<Integer, PokerPlayer> getCurrentHandPlayerMap();

	SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap();

	PokerPlayer getPlayerInCurrentHand(Integer playerId);

	int countNonFoldedPlayers();

	boolean isPlayerInHand(int playerId);

	void notifyDealerButton(int dealerButtonSeatId);

	PokerVariant getPokerVariant();
	
	List<Card> getCommunityCards();

	int getEntryBetLevel();
	
	/**
	 * Must be invoked when the first call in the hand has been made.
	 */
	void call();
	
	/**
	 * Notify that the betstack of a player has updated
	 */
	void notifyBetStacksUpdated();

	void exposeShowdownCards();
	
	/**
	 * Shutdown this table. After calling this method the table cannot be started again.
	 * The game will move to the {@link ShutdownSTM} state.
	 */
	void shutdown();
	
}