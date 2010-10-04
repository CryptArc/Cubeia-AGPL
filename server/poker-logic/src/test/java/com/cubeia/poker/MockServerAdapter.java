package com.cubeia.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.ualberta.cs.poker.Card;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.PlayerHands;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.tournament.RoundReport;

public class MockServerAdapter implements ServerAdapter {

	private int timeoutCounter = 0;
	private ActionRequest request;
	public PlayerHands hands;
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
	
	public void scheduleTimeout(long millis) {
		timeoutCounter++;
	}

	public int getTimeoutRequests() {
		return timeoutCounter;
	}
	
	public int decrementScheduledTimeouts() {
		return timeoutCounter--;
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
    
    public void notifyActionPerformed(PokerAction action) {
        actionPerformed = action;
    }

	public void notifyCommunityCards(List<Card> cards) {}
	public void notifyPrivateCards(int playerId, List<Card> cards) {}
	public void exposePrivateCards(int playerId, List<Card> cards) {}
	public void notifyDealerButton(int playerId) {}
    public void reportTournamentRound(RoundReport report) {}
    public void cleanupPlayers() {}
    public void updatePot(Integer sum) {}
    public void notifyPlayerBalance(PokerPlayer p) {}
    public void notifyNewHand() {}

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
	public void updatePots(Iterable<Pot> iterable) {
		// TODO Auto-generated method stub
		
	}
}
