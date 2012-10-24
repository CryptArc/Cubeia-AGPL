/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.adapter;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.util.SitoutCalculator;
import com.cubeia.poker.tournament.RoundReport;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public interface ServerAdapter {

    void scheduleTimeout(long millis);

    void requestAction(ActionRequest request);

    /**
     * Requests multiple actions sharing the same sequence number and timeout.
     *
     * @param requests
     */
    void requestMultipleActions(Collection<ActionRequest> requests);


    void notifyCommunityCards(List<Card> cards);

    /**
     * Notify all players who is dealer.
     *
     * @param seatId
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
     * Notify the user of his best possible hand using both pocket (hidden and exposed) and community cards.
     *
     * @param playerId    player id
     * @param handType    hand type classification
     * @param cardsInHand cards used in best hand
     * @param publicHand  if the bestHand should be broadcasted to all players or just the owner
     */
    void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand);

    /**
     * Sends the private cards to the given player and notify
     * all other players with exposed cards.
     *
     * @param playerId
     * @param cards
     */
    void notifyPrivateExposedCards(int playerId, List<Card> cards);


    /**
     * A new hand is about to start.
     *
     * @throws SystemShutdownException If the system is shutting down and the table should close
     */
    void notifyNewHand() throws SystemShutdownException;

    /**
     * Notify about market references.
     * If any reference is null then it is replaced by a minus sign.
     *
     * @param playerId
     * @param externalTableReference        the tables reference
     * @param externalTableSessionReference the players table reference
     */
    void notifyExternalSessionReferenceInfo(int playerId, String externalTableReference, String externalTableSessionReference);

    void exposePrivateCards(ExposeCardsHolder holder);

    /**
     * Notifies that the hand has ended.
     *
     * @param handResult    Summary of the results or null if hand was cancelled
     * @param handEndStatus the way the hand ended, for example normal or canceled
     * @param tournamentTable indicates if the hand was part of a tournament
     */
    void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, boolean tournamentTable);

    /**
     * Notify players about updated player balance.
     *
     * @param player
     */
    void notifyPlayerBalance(PokerPlayer player);


    /**
     * Called after an action from the player has been successfully
     * dealt with.
     *
     * @param pokerPlayer the player who performed an action
     * @param action,     not null.
     */
    void notifyActionPerformed(PokerAction action, PokerPlayer pokerPlayer);

    /**
     * Reports the end of a round to a tournament coordinator.
     *
     * @param report, a report value object. Not null.
     */
    void reportTournamentRound(RoundReport report);

    /**
     * Remove all players in state LEAVING or DISCONNECTED
     */
    void cleanupPlayers(SitoutCalculator sitoutCalculator);

    /**
     * Notifies the client about pot updates by sending the post and pot transitions.
     *
     * @param pots           updated post
     * @param potTransitions pot transitions
     */
    void notifyPotUpdates(Collection<Pot> pots, Collection<PotTransition> potTransitions);

    void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status, boolean inCurrentHand);

    /**
     * Send information if the deck in use.
     *
     * @param size    total number of cards in deck
     * @param rankLow lowest used rank in deck, this is normally TWO, but if the deck is stripped
     *                it might be different.
     */
    void notifyDeckInfo(int size, Rank rankLow);

    void notifyNewRound();

    /**
     * Send information to client about buyins
     *
     * @param playerId
     * @param mandatoryBuyin TODO
     */

    void notifyBuyInInfo(int playerId, boolean mandatoryBuyin);

    /**
     * Notify the client of the current total rake ant pot sizes.
     *
     * @param rakeInfoContainer rake info
     */
    void notifyRakeInfo(RakeInfoContainer rakeInfoContainer);

    public void unseatPlayer(int playerId, boolean setAsWatcher);

    /**
     * Notify that a bet was taken back from betstack to balance since it was uncalled
     *
     * @param playerId
     * @param amount
     */
    void notifyTakeBackUncalledBet(int playerId, int amount);

    /**
     * Notify that the player will be able to do this actions later when it is the players turn
     *
     * @param player
     * @param optionList
     */
    void notifyFutureAllowedActions(PokerPlayer player, List<PokerActionType> optionList);

    /**
     * Request buy in:s for the given players that has {@link PokerPlayer#getRequestedBuyInAmount()} > 0.
     *
     * @param players
     */
    void performPendingBuyIns(Collection<PokerPlayer> players);

    /**
     * Send out a player status for a new hand starting
     *
     * @param playerId
     * @param status
     */
    void notifyHandStartPlayerStatus(int playerId, PokerPlayerStatus status);

    void notifyDisconnected(int playerId);

    /**
     * Returns the identifier of the hand that was provided by the backend.
     *
     * @return backend integration hand id
     */
    String getIntegrationHandId();
    
    Random getSystemRNG();

}
