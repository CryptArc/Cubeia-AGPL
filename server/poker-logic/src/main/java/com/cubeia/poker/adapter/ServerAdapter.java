/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General License for more details.
 *
 * You should have received a copy of the GNU Affero General License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.adapter;

import java.util.Collection;
import java.util.List;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.tournament.RoundReport;

public interface ServerAdapter {
	
	void scheduleTimeout(long millis);
	
	void requestAction(ActionRequest request);

	void notifyCommunityCards(List<Card> cards);
	
	/**
	 * Notify all players who is dealer.
	 * 
	 * @param playerId
	 */
	void notifyDealerButton(int seatId);
	
	/**
	 * Sends the private cards to the given player and notify
	 * all other players with hidden cards.
	 * 
	 * @param playerId
	 * @param cards
	 */
	void notifyPrivateCards(int playerId, List<Card> cards);
	
	/**
	 * Notify the user of his best possible hand using both pocket (hidden and exposted) and community cards.
	 * @param playerId player id
	 * @param handType hand type classification
	 * @param cardsInHand cards used in best hand
	 */
	void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand);
	
	/**
	 * Sends the private cards to the given player and notify
	 * all other players with exposed cards.
	 * @param playerId
	 * @param cards
	 */
    void notifyPrivateExposedCards(int playerId, List<Card> cards);
	
	void exposePrivateCards(int playerId, List<Card> cards);
	
    /**
     * A new hand is about to start.
     */
    void notifyNewHand();
    
    
	/**
	 * Notifies that the hand has ended.
	 * 
	 * @param handResult Summary of the results or null if hand was cancelled
	 * @param handEndStatus the way the hand ended, for example normal or canceled
	 * @param potTransitions collection of pot transitions (money from pots to players)
	 */
	void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus);

	/**
	 * Notify players about updated player balance.
	 * 
	 * @param player
	 */
	void notifyPlayerBalance(PokerPlayer player);
	
	
	/**
	 * Called after an action from the player has been successfully
	 * dealt with.
	 * @param resultingBalance the table balance after the action was performed
	 * @param action, not null.
	 */
	void notifyActionPerformed(PokerAction action, long resultingBalance);
	
	/**
	 * Reports the end of a round to a tournament coordinator.
	 * 
	 * @param report, a report value object. Not null.
	 */
	void reportTournamentRound(RoundReport report);
	
	/**
     * Remove all players in state LEAVING or DISCONNECTED
     */
    void cleanupPlayers();
    
    void updatePots(Collection<Pot> iterable, Collection<PotTransition> potTransitions);

    void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status);

    /**
	 * Notify players about updated player balance.
	 * 
	 * @param player
	 */
    void notifyPlayerBalanceReset(PokerPlayer player);

    /**
     * Send information if the deck in use.
     * @param size total number of cards in deck
     * @param rankLow lowest used rank in deck, this is normally TWO, but if the deck is stripped 
     * it might be different.
     */
    void notifyDeckInfo(int size, Rank rankLow);

	void notifyNewRound();
	
    /**
     * Send information to client about buyins
     * @param playerId
     * @param mandatoryBuyin TODO
     */

	void notifyBuyInInfo(int playerId, boolean mandatoryBuyin);

}
