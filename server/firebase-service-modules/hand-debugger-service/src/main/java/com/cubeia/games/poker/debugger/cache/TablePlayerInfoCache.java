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

package com.cubeia.games.poker.debugger.cache;


import com.google.common.base.Predicate;
import com.google.inject.Singleton;

import java.util.*;

import static com.google.common.collect.Collections2.filter;

/**
 * This class will leak memory if used in production. Players are never removed
 * from this cache.
 *
 * @author w
 */
@Singleton
public class TablePlayerInfoCache {

    private final Map<Integer, PlayerInfo> playerIdToInfoMap = new HashMap<Integer, PlayerInfo>();

    public synchronized void updatePlayerInfo(int tableId, int playerId, String name, boolean isSittingIn, long balance, long betstack) {
        PlayerInfo playerInfo = new PlayerInfo(tableId, playerId, name, isSittingIn, balance, betstack, new Date());
        playerIdToInfoMap.put(playerId, playerInfo);
    }

    public synchronized PlayerInfo getPlayerInfoById(int playerId) {
        return playerIdToInfoMap.get(playerId);
    }

    public synchronized Collection<PlayerInfo> getPlayerInfosByTableId(final int tableId) {
        Collection<PlayerInfo> playersByTable = filter(new ArrayList<PlayerInfo>(playerIdToInfoMap.values()), new Predicate<PlayerInfo>() {
            @Override
            public boolean apply(PlayerInfo pi) {
                return pi.getTableId() == tableId;
            }
        });
        return playersByTable;
    }

}
