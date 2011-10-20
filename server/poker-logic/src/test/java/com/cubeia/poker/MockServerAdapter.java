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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.tournament.RoundReport;

public class MockServerAdapter implements ServerAdapter {

	Logger log = Logger.getLogger(this.getClass());
	
	private int timeoutCounter = 0;
	private ActionRequest request;
	public Collection<RatedPlayerHand> hands;
	public PokerAction actionPerformed;
	public HandEndStatus handEndStatus;
	public Map<Integer, List<Card>> exposedCards = new HashMap<Integer, List<Card>>();
	public HandResult result;
	public Map<Integer,PokerPlayerStatus> playerStatus = new HashMap<Integer, PokerPlayerStatus>();
	
	public void clear() {
	    handEndStatus = null;
	    actionPerformed = null;
	    exposedCards.clear();
	    hands = null;
	    playerStatus.clear();
	    timeoutCounter = 0;
	}
	
	@Override
	public void notifyNewRound() { }
	
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
		return actionPerformed;
	}

	public ActionRequest getActionRequest() {
		return request;
	}
	
	/**
	 * Sets the action request to null.
	 */
	public void clearActionRequest() {
		request = null;
	}
	
	
	public void requestAction(ActionRequest request) {
		this.request = request;
	}
	
	public void notifyHandEnd(HandResult result, HandEndStatus status) {
		this.result = result;
        this.hands = result != null ? result.getPlayerHands() : null;
        this.handEndStatus = status;
    }
    
    public void notifyActionPerformed(PokerAction action, long resultingBalance) {
        actionPerformed = action;
    }

	public void notifyCommunityCards(List<Card> cards) {}
	public void notifyPrivateCards(int playerId, List<Card> cards) {}
	public void notifyHandCanceled() {}
	public void notifyPrivateExposedCards(int playerId, List<Card> cards) {}
	public void exposePrivateCards(int playerId, List<Card> cards) {}
	public void notifyDealerButton(int playerId) {}
    public void reportTournamentRound(RoundReport report) {}
    public void cleanupPlayers() {}
    public void updatePot(Integer sum) {}
    public void notifyPlayerBalance(PokerPlayer p) {}
    public void notifyNewHand() {}
    public void notifyDeckInfo(int size, Rank rankLow) {}
    public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand) {}
    public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {}
    public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {}    
    
    @Override
	public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status) {
		playerStatus.put(playerId, status);
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

}
