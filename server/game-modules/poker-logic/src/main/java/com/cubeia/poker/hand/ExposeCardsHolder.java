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

package com.cubeia.poker.hand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ExposeCardsHolder {

    private Map<Integer, Collection<Card>> allCards = new HashMap<Integer, Collection<Card>>();

    public void setExposedCards(int playerId, Collection<Card> cards) {
        allCards.put(playerId, cards);
    }

    public boolean hasCards() {
        return allCards.size() > 0;
    }

    public Set<Integer> getPlayerIdSet() {
        return allCards.keySet();
    }

    public Collection<Card> getCardsForPlayer(Integer playerId) {
        return allCards.get(playerId);
    }

}
