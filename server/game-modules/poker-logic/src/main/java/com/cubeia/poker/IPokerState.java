package com.cubeia.poker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.states.ShutdownSTM;
import com.cubeia.poker.variant.PokerVariant;

public interface IPokerState {

	void init(RNGProvider rngProvider, PokerSettings settings);
	
	void notifyPlayerSittingOut(int playerId);
	
	void playerIsSittingOut(int playerId, SitOutStatus misssedAnte);

	int getAnteLevel();

	Map<Integer, PokerPlayer> getCurrentHandPlayerMap();

	SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap();

	/**
	 * Returns a player participating in the current hand (or the last played hand if waiting to start) by it's id.
	 * NOTE: that this method might return a player even after the hand is finished.
	 * @param playerId player id
	 * @return player or null if not in hand
	 */
	PokerPlayer getPlayerInCurrentHand(Integer playerId);

	/**
	 * Returns the number of non folded players.
	 * @return number of non folded players
	 */
	int countNonFoldedPlayers();
	
//	/**
//	 * Returns the number of players sitting in.
//	 * @return number of players sitting in
//	 */
//    int countSittingInPlayers();
    
	/**
	 * Returns the players that are ready to start a new hand.
	 * @return number of players ready for a new hand
	 */
    Collection<PokerPlayer> getPlayersReadyToStartHand();
    
	boolean isPlayerInHand(int playerId);

	void notifyDealerButton(int dealerButtonSeatId);

	PokerVariant getPokerVariant();
	
	List<Card> getCommunityCards();

	int getEntryBetLevel();
	
	/**
	 * Must be invoked when the first call in the hand has been made.
	 */
	void callOrRaise();
	
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

	/**
	 * Returns true if the state/table is shut down.
	 * @return true if shut down, false otherwise
	 */
    boolean isShutdown();

    /**
     * Sit out the players that has been marked for sitout next round.
     * This method should be called before or after a hand, not in the middle.
     */
    void sitOutPlayersMarkedForSitOutNextRound();

    /**
     * Handles a buy in request for a player.
     * @param pokerPlayer player
     * @param amount amount requested
     */
    void handleBuyInRequest(PokerPlayer pokerPlayer, int amount);

    /**
     * True if the current state is PLAYING.
     * @return true if playing, false otherwise
     */
    boolean isPlaying();

	boolean isEveryoneSittingOut();

}