package com.cubeia.games.poker.adapter.achievements;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.bonus.firebase.api.AchievementsService;
import com.cubeia.events.event.GameEvent;
import com.cubeia.events.event.GameEventType;
import com.cubeia.events.event.poker.PokerAttributes;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class AchievementAdapter {
	
	Logger log = LoggerFactory.getLogger(getClass());

	/** Service for sending and listening to bonus/achievement events to players */
	@Service AchievementsService service;
	
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
	
	
	public void notifyEndPlayerSession(int playerId, int operatorId, String screenname, Money accountBalance) {
		log.debug("Event Player Session ended. Player["+playerId+":"+screenname+"], Balance["+accountBalance+"]");
		
		GameEvent event = new GameEvent();
		event.game = PokerAttributes.poker.name();
		event.player = playerId+"";
		event.type = GameEventType.leaveTable.name();
		event.operator = operatorId+"";
		event.screenName = screenname;
		
		event.attributes.put(PokerAttributes.accountBalance.name(), accountBalance.getAmount()+"");
		event.attributes.put(PokerAttributes.accountCurrency.name(), accountBalance.getCurrencyCode());
		event.attributes.put(PokerAttributes.screenname.name(), screenname);
		
		log.debug("Send Player Session ended event: "+event);
		service.sendEvent(event);
	}
	
	
	private void sendPlayerHandEnd(PokerPlayer player, Result result, HandResult handResult) {
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
