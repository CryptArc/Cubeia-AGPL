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

import java.io.Serializable;
import java.util.*;

public class FullHand implements Serializable {

    private static final long serialVersionUID = -7056939397836019770L;

    public final static int MAX_CARDS = 7;
    private final Integer playerId;
    private List<GameCard> cards = new ArrayList<GameCard>();

    public FullHand(Integer playerId, List<GameCard> cards) {
        this.playerId = playerId;
        this.cards = cards;
    }

    public List<GameCard> getCards() {
        return cards;
    }

    public void setCards(List<GameCard> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullHand fullHand = (FullHand) o;

        if (cards != null ? !cards.equals(fullHand.cards) : fullHand.cards != null) return false;
        if (playerId != null ? !playerId.equals(fullHand.playerId) : fullHand.playerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playerId != null ? playerId.hashCode() : 0;
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        return result;
    }
}
