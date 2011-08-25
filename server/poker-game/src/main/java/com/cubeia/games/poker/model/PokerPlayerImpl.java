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
