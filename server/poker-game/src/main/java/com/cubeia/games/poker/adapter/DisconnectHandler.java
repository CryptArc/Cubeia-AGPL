package com.cubeia.games.poker.adapter;

import static com.cubeia.firebase.api.game.player.PlayerStatus.CONNECTED;
import static com.cubeia.firebase.api.game.player.PlayerStatus.WAITING_REJOIN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.inject.Inject;

public class DisconnectHandler {

	private static Logger log = LoggerFactory.getLogger(DisconnectHandler.class);
	
	@Inject PokerState state;
	
	@Inject FirebaseServerAdapter adapter;
	
	
	/**
	 * Check the new status and adjust time to act accordingly and send
	 * out notifiations as needed.
	 * 
	 * @param table
	 * @param playerId
	 * @param status
	 */
	public void checkDisconnectTime(Table table, int playerId, PlayerStatus status) {
		log.debug("Check disconnect for player {} with new status {}", playerId, status);
		if (status.equals(WAITING_REJOIN)) {
			handleDisconnected(playerId);
			
		} else if (status.equals(CONNECTED)) {
			
		}
	}

	/**
	 * Player has lost connection but Firebase still has the session alive
	 * Check if we are currently waiting for this player to act
	 * 
	 * @param playerId
	 */
	private void handleDisconnected(int playerId) {
		boolean playerToAct = state.isWaitingForPlayerToAct(playerId);
		log.debug("Disconnected player {}, current player: {}", playerId, playerToAct);
		if (playerToAct) {
			PokerPlayer pokerPlayer = state.getPokerPlayer(playerId);
			log.debug("Disconnected player {} has used disconnect timer: {}", playerId, pokerPlayer.isDisconnectTimeoutUsed());
			if (!pokerPlayer.isDisconnectTimeoutUsed()) {
				adapter.notifyDisconnected(playerId);
				pokerPlayer.setDisconnectTimeoutUsed(true);
			} else {
				log.debug("Player[{}] disconnected but will not be granted extra time since the disconnect time used flag is set", playerId);
			}
		}
	}
}
