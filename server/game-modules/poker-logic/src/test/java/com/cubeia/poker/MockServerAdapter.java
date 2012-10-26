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

package com.cubeia.poker;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.util.SitoutCalculator;
import com.cubeia.poker.tournament.RoundReport;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

public class MockServerAdapter implements ServerAdapter {

    Logger log = Logger.getLogger(this.getClass());

    private int timeoutCounter = 0;
    private Deque<ActionRequest> requests = new LinkedList<ActionRequest>();
    public Collection<RatedPlayerHand> hands;
    protected List<PokerAction> allActions = new ArrayList<PokerAction>();
    public HandEndStatus handEndStatus;
    public Map<Integer, List<Card>> exposedCards = new HashMap<Integer, List<Card>>();
    public HandResult result;
    public Map<Integer, PokerPlayerStatus> playerStatus = new HashMap<Integer, PokerPlayerStatus>();
    public Random random;
    
    @Override
    public Random getSystemRNG() {
    	return random;
    }
    
    public void clear() {
        handEndStatus = null;
        allActions.clear();
        exposedCards.clear();
        hands = null;
        playerStatus.clear();
        timeoutCounter = 0;
    }

    @Override
    public void notifyNewRound() {
    }

    public void scheduleTimeout(long millis) {
        timeoutCounter++;
    }

    public int getTimeoutRequests() {
        return timeoutCounter;
    }

    public int decrementScheduledTimeouts() {
        return timeoutCounter--;
    }

    public PokerAction getLatestActionPerformed() {
        if (allActions.isEmpty()) {
            return null;
        } else {
            return allActions.get(allActions.size() - 1);
        }
    }

    public PokerAction getNthAction(int n) {
        return allActions.get(n);
    }

    public int getPerformedActionCount() {
        return allActions.size();
    }

    public ActionRequest getLastActionRequest() {
        return requests.peekLast();
    }

    /**
     * Sets the action request to null.
     */
    public void clearActionRequest() {
        requests.clear();
    }


    public void requestAction(ActionRequest request) {
        requests.add(request);
    }

    @Override
    public void requestMultipleActions(Collection<ActionRequest> requests) {
        this.requests.addAll(requests);
    }

    public void notifyHandEnd(HandResult result, HandEndStatus status, boolean tournamentTable) {
        this.result = result;
        this.hands = result != null ? result.getPlayerHands() : null;
        this.handEndStatus = status;
    }

    public void notifyActionPerformed(PokerAction action, PokerPlayer pokerPlayer) {
        allActions.add(action);
    }

    public void notifyCommunityCards(List<Card> cards) {
    }

    public void notifyPrivateCards(int playerId, List<Card> cards) {
    }

    public void notifyHandCanceled() {
    }

    public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
    }

    public void exposePrivateCards(ExposeCardsHolder holder) {
    }

    public void notifyDealerButton(int playerId) {
    }

    public void reportTournamentRound(RoundReport report) {
    }

    public void cleanupPlayers(SitoutCalculator sitoutCalculator) {
    }

    public void updatePot(Integer sum) {
    }

    public void notifyPlayerBalance(PokerPlayer p) {
    }

    public void notifyNewHand() {
    }

    public void notifyDeckInfo(int size, Rank rankLow) {
    }

    public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand) {
    }

    public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
    }

    public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
    }

    public void notifyTakeBackUncalledBet(int playerId, int amount) {
    }

    public void notifyExternalSessionReferenceInfo(int playerId, String externalTableReference, String externalTableSessionReference) {
    }

    public void notifyFutureAllowedActions(PokerPlayer player, List<PokerActionType> optionList) {
    }

    public String getIntegrationHandId() {
        return null;
    }

    @Override
    public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status, boolean isInCurrentHand) {
        playerStatus.put(playerId, status);
    }

    @Override
    public void notifyHandStartPlayerStatus(int playerId, PokerPlayerStatus status) {
        // TODO Auto-generated method stub

    }

    public List<PokerPlayer> getWinners() {
        List<PokerPlayer> winners = new ArrayList<PokerPlayer>();
        for (Entry<PokerPlayer, Result> entry : result.getResults().entrySet()) {
            if (entry.getValue().getNetResult() > 0) {
                winners.add(entry.getKey());
            }
        }
        return winners;
    }

    public PokerPlayerStatus getPokerPlayerStatus(int playerId) {
        return playerStatus.get(playerId);
    }

    @Override
    public void notifyPotUpdates(Collection<Pot> iterable, Collection<PotTransition> potTransitions) {
    }

    @Override
    public void unseatPlayer(int playerId, boolean setAsWatcher) {
    }

    @Override
    public void performPendingBuyIns(Collection<PokerPlayer> players) {
    }

    @Override
    public void notifyDisconnected(int playerId) {
        // TODO Auto-generated method stub

    }

}
