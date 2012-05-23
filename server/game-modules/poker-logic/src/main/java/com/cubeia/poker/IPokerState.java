package com.cubeia.poker;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.states.ShutdownSTM;
import com.cubeia.poker.variant.PokerVariant;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface IPokerState {

    void init(GameType gameType, PokerSettings settings);

    void playerSitsOut(int playerId, SitOutStatus sitOutStatus);

    int getAnteLevel();

    Map<Integer, PokerPlayer> getCurrentHandPlayerMap();

    SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap();

    /**
     * Returns a player participating in the current hand (or the last played hand if waiting to start) by its id.
     * NOTE: that this method might return a player even after the hand is finished.
     *
     * @param playerId player id
     * @return player or null if not in hand
     */
    PokerPlayer getPlayerInCurrentHand(Integer playerId);

    /**
     * Returns the number of non folded players.
     *
     * @return number of non folded players
     */
    int countNonFoldedPlayers();

    /**
     * Returns the players that are ready to start a new hand.
     *
     * @return number of players ready for a new hand
     */
//    Collection<PokerPlayer> getPlayersReadyToStartHand();

    boolean isPlayerInHand(int playerId);

    void notifyDealerButton(int dealerButtonSeatId);

    List<Card> getCommunityCards();

    int getEntryBetLevel();

    /**
     * Notify that the betstack of a player has updated
     */
    void notifyBetStacksUpdated();

    /**
     * Shutdown this table. After calling this method the table cannot be started again.
     * The game will move to the {@link ShutdownSTM} state.
     */
    void shutdown();

    /**
     * Sit out the players that has been marked for sitout next round.
     * This method should be called before or after a hand, not in the middle.
     */
    void sitOutPlayersMarkedForSitOutNextRound();

    /**
     * Handles a buy in request for a player.
     *
     * @param pokerPlayer player
     * @param amount      amount requested
     */
    void handleBuyInRequest(PokerPlayer pokerPlayer, int amount);

    boolean isEveryoneSittingOut();

}