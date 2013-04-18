package com.cubeia.games.poker.adapter.domainevents;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.events.event.GameEvent;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.domainevents.api.DomainEventsService;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class DomainEventAdapter {
	
	Logger log = LoggerFactory.getLogger(getClass());

	/** Service for sending and listening to bonus/achievement events to players */
	@Service DomainEventsService service;
	
	/**
	 * Report hand end result to the achievment service
	 * @param handResult
	 * @param handEndStatus
	 * @param tournamentTable
	 */
	public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, boolean tournamentTable) {
		Map<PokerPlayer, Result> map = handResult.getResults();
		for (PokerPlayer player : map.keySet()) {
			sendPlayerHandEnd(player, map.get(player), handResult);	
		}
	}
	
	
	public void notifyEndPlayerSession(int playerId, Money accountBalance) {
		service.sendEndPlayerSessionEvent(playerId, accountBalance);
	}
	
	
	private void sendPlayerHandEnd(PokerPlayer player, Result result, HandResult handResult) {
		// We don't want to push events for operator id 0 which is reserved for bots and internal users.
		// TODO: Perhaps make excluded operators configurable
		if (player.getOperatorId() == 0) {
			return; 
		}
		
		GameEvent event = new GameEvent();
		event.game = "poker";
		event.type = "roundEnd";
		event.player = player.getId()+"";
		event.attributes.put("stake", calculateStake(result)+"");
		event.attributes.put("winAmount", result.getWinningsIncludingOwnBets()+"");
		
		boolean isWin = calculateIsWin(result);
		if (isWin) {
			event.attributes.put("win", "true");
		} else {
			event.attributes.put("lost", "true");
		}

		if (isWin) {
			RatedPlayerHand hand = getRatedPlayerHand(player, handResult);
			if (hand != null) {
				event.attributes.put("handType", hand.getHandInfo().getHandType().name());
			}
		}
		
		log.debug("Send player hand end event: "+event);
		service.sendEvent(event);
	}

	private RatedPlayerHand getRatedPlayerHand(PokerPlayer player, HandResult handResult) {
		for (RatedPlayerHand rphand : handResult.getPlayerHands()) {
			if (rphand.getPlayerId() == player.getId()) {
				return rphand;
			}
		}
		// This is normal, hand types are only included for non-muck players and show down.
		// log.debug("Could not find hand type for player["+player.getId()+"]");
		return null;
	}

	private boolean calculateIsWin(Result result) {
		return result.getNetResult() > 0;
	}

	private long calculateStake(Result result) {
		return result.getWinningsIncludingOwnBets() - result.getNetResult();
	}


	
}
