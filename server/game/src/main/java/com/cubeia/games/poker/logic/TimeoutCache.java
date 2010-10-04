package com.cubeia.games.poker.logic;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.games.poker.util.TablePlayerKey;


/**
 * TODO: This whole implementation does not support fail over of server nodes.
 * 
 * The actions needs to be stored in the game state.
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class TimeoutCache {
    
    private static TimeoutCache instance = new TimeoutCache();
    
    protected ConcurrentMap<TablePlayerKey,UUID> actions = new ConcurrentHashMap<TablePlayerKey, UUID>();
    
    public static TimeoutCache getInstance() {
        return instance;
    }
    
    /**
     * Adds a reference to a timeout for an action request.
     * 
     * @param tableId
     * @param pid
     * @param actionId
     */
    public void addTimeout(int tableId, int pid, UUID actionId) {
        actions.put(new TablePlayerKey(tableId, pid), actionId);
    }
    
    /**
     * Removes reference from the map and cancel the scheduled action from
     * the given scheduler.
     * 
     * @param tableId
     * @param pid
     * @param tableScheduler, if null then skipped.
     */
    public void removeTimeout(int tableId, int pid, TableScheduler tableScheduler) {
        UUID remove = actions.remove(new TablePlayerKey(tableId, pid));
        // if (remove == null) log.warn("I tried to remove a non-existing timeout for table: "+tableId+" pid: "+pid);
        if (remove != null && tableScheduler != null) {
            tableScheduler.cancelScheduledAction(remove);
        }
    }
 
    
}
