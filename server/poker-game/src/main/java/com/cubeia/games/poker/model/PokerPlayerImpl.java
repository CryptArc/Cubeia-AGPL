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
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
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
    private static final Logger log = LoggerFactory.getLogger(PokerPlayerImpl.class);
    
	private static final long serialVersionUID = 1L;

	private GenericPlayer placeholder;

	private PlayerSessionId playerSessionId;
	
	private String externalPlayerSessionId;

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
	public void setPlayerSessionId(PlayerSessionId playerSessionId) {
	    log.debug("updating player {} session id: {} -> {}", new Object[] {getId(), this.playerSessionId, playerSessionId});
		this.playerSessionId = playerSessionId;
	}	

	/**
	 * Returns the session id for this player.
	 * @return the session id, null if not in a session
	 */
	public PlayerSessionId getPlayerSessionId() {
		return playerSessionId;
	}

	/**
	 * Returns the external (AAMS for example) session id for the current session. 
	 * @return session id, can be null if no session is open
	 */
	public String getExternalPlayerSessionId() {
        return externalPlayerSessionId;
    }

    public void setExternalPlayerSessionReference(String externalPlayerSessionId) {
        this.externalPlayerSessionId = externalPlayerSessionId;
    }
    
}
