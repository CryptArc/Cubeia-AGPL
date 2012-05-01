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
