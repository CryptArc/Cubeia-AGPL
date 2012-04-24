package com.cubeia.games.poker.adapter;

import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.action.WatchResponseAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.protocol.Enums.WatchResponseStatus;

public class PlayerUnseater {
    
    public void unseatPlayer(Table table, int playerId, boolean setAsWatcher) {
        table.getPlayerSet().unseatPlayer(playerId);
        table.getListener().playerLeft(table, playerId);
        if (setAsWatcher) {
            LeaveAction leave = new LeaveAction(playerId, table.getId());
            WatchResponseAction watch = new WatchResponseAction(table.getId(), WatchResponseStatus.OK);
            table.getNotifier().sendToClient(playerId, leave);
            table.getNotifier().sendToClient(playerId, watch);
            table.getWatcherSet().addWatcher(playerId);
            table.getListener().watcherJoined(table, playerId);
        }
    }

}
