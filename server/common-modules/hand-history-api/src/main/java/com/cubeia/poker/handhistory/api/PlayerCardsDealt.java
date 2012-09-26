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

package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PlayerCardsDealt extends HandHistoryEvent {

    private static final long serialVersionUID = 2419782176272291069L;
	
    private int playerId;
    private final List<GameCard> cards = new LinkedList<GameCard>();
    private boolean isExposed;

    public PlayerCardsDealt() {
    }

    public PlayerCardsDealt(int playerId, boolean isExposed) {
        this.playerId = playerId;
        this.isExposed = isExposed;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public int getPlayerId() {
        return playerId;
    }

    public List<GameCard> getCards() {
        return cards;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cards == null) ? 0 : cards.hashCode());
        result = prime * result + (isExposed ? 1231 : 1237);
        result = prime * result + (int) (playerId ^ (playerId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerCardsDealt other = (PlayerCardsDealt) obj;
        if (cards == null) {
            if (other.cards != null)
                return false;
        } else if (!cards.equals(other.cards))
            return false;
        if (isExposed != other.isExposed)
            return false;
        if (playerId != other.playerId)
            return false;
        return true;
    }
}
