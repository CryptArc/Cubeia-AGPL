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

package com.cubeia.poker.result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;

/**
 * The result of a hand. This class maps the player to the resulting win/lose amount of the hand.
 */
public class HandResult implements Serializable {

	private static final long serialVersionUID = -7802386310901901021L;

	private final Map<PokerPlayer, Result> results;

	private final List<RatedPlayerHand> playerHands;

	public HandResult() {
	    results = Collections.emptyMap();
	    playerHands = Collections.emptyList();
	}
	
	public HandResult(Map<PokerPlayer, Result> results, List<RatedPlayerHand> playerHands) {
	    this.results = results;
	    this.playerHands = playerHands;
	}
	
	public List<RatedPlayerHand> getPlayerHands() {
	    return playerHands;
	}

    public Map<PokerPlayer, Result> getResults() {
		return results;
	}

	public String toString() {
		return "HandResult results["+results+"] playerHands["+playerHands+"]";
	}
}
