package com.cubeia.games.poker.adapter.achievement;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.log.Log;

import com.cubeia.bonus.event.GameEvent;
import com.cubeia.bonus.firebase.api.AchievementsService;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;

public class AchievementAdapter {

	@Service AchievementsService service;
	
	/**
	 * Report hand end result to the achievment service
	 * @param handResult
	 * @param handEndStatus
	 * @param tournamentTable
	 */
	public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, boolean tournamentTable) {
		Log.debug("Notify hand end");
		Map<PokerPlayer, Result> map = handResult.getResults();
		for (PokerPlayer player : map.keySet()) {
			sendPlayerHandEnd(player, map.get(player), handResult);	
		}
	}
	
	private void sendPlayerHandEnd(PokerPlayer player, Result result, HandResult handResult) {
		Log.debug("Notify player hand end to achievment system");
		GameEvent event = new GameEvent();
		event.game = "poker";
		event.type = "roundEnd";
		event.player = player.getPlayerId()+"";
		event.attributes.put("stake", calculateStake(result)+"");
		event.attributes.put("winAmount", result.getWinningsIncludingOwnBets()+"");
		
		if (calculateIsWin(result)) {
			event.attributes.put("win", "true");
		} else {
			event.attributes.put("lost", "true");
		}

		Log.debug("Send game event: "+event);
		service.sendEvent(event);
	}

	private boolean calculateIsWin(Result result) {
		return result.getNetResult() > 0;
	}

	private long calculateStake(Result result) {
		return result.getWinningsIncludingOwnBets() - result.getNetResult();
	}
}
