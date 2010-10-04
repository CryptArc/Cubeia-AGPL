package com.cubeia.games.poker.model;

import java.io.Serializable;

import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.poker.player.DefaultPokerPlayer;

/**
 * Models a player that is active in the game.
 * 
 * Part of replicated game state
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerPlayerImpl extends DefaultPokerPlayer implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private GenericPlayer placeholder;

	private Long sessionId;
		
	public PokerPlayerImpl (GenericPlayer placeholder) {
		super(placeholder.getPlayerId());
		this.placeholder = placeholder;
	}

	@Override
	public int getSeatId() {
		return placeholder.getSeatId();
	}
	
	/**
	 * Sets a session id for this player. 
	 * @param sessionId the session id, or null to leave the session
	 */
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}	

	/**
	 * Returns the session id for this player.
	 * @return the session id, null if not in a session
	 */
	public Long getSessionId() {
		return sessionId;
	}
	
	@Override
	public String toString() {
		return String.format("<playerId[%d] seatId[%d] hasFolded[%b] hasActed[%b] isSittingOut[%b]>", playerId, seatId, hasFolded, hasActed, isSittingOut);
	}
}
