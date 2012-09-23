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

package com.cubeia.games.poker.cache;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.debugger.HandDebuggerContract;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple cache for holding actions that composes the game state of the current round.
 * Maps actions to tables. It is important that this cache is properly cleared since there is no inherent
 * house-keeping in this implementation.
 * <p/>
 * NOTE: this cache is not replicated: fail over won't work
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class ActionCache {
    private static Logger log = LoggerFactory.getLogger(ActionCache.class);

    private final Multimap<Integer, ActionContainer> cache;

    private SystemTime dateFetcher;

    @Service
    HandDebuggerContract handDebugger;

    @Inject
    public ActionCache(SystemTime dateFetcher) {
        this.dateFetcher = dateFetcher;
        LinkedListMultimap<Integer, ActionContainer> linkedListMultimap = LinkedListMultimap.<Integer, ActionContainer>create();
        // TODO: this map is fully synchronized but we only need to synchronize on table id (events on the same table are never concurrent)
        cache = Multimaps.<Integer, ActionContainer>synchronizedListMultimap(linkedListMultimap);
    }

    /**
     * Add public action to a table state cache.
     *
     * @param tableId
     * @param action
     */
    public void addPublicAction(int tableId, GameAction action) {
        addPublicActionWithExclusion(tableId, action, -1);
    }

    /**
     * Add public action to a table state cache.
     *
     * @param tableId
     * @param action
     */
    public void addPublicActionWithExclusion(int tableId, GameAction action, int excludedPlayerId) {
        cache.put(tableId, ActionContainer.createPublic(action, excludedPlayerId, dateFetcher.date().getMillis()));
        log.trace("added public action to cache, tableId = {}, action type = {}, new cache size = {}",
                new Object[]{tableId, action.getClass().getSimpleName(), cache.get(tableId).size()});

        if (handDebugger != null) {
            handDebugger.addPublicAction(tableId, action);
        }
    }

    /**
     * Add a private action to the cache.
     *
     * @param tableId
     * @param playerId
     * @param action
     */
    public void addPrivateAction(int tableId, int playerId, GameAction action) {
        cache.put(tableId, ActionContainer.createPrivate(playerId, action, dateFetcher.date().getMillis()));
        log.trace("added private action to cache, tableId = {}, playerId = {}, action type = {}, new cache size = {}",
                new Object[]{tableId, playerId, action.getClass().getSimpleName(), cache.get(tableId).size()});

        if (handDebugger != null) {
            handDebugger.addPrivateAction(tableId, playerId, action);
        }
    }

    /**
     * Retrieve public state from a table. Use this for new players.
     *
     * @param tableId table id
     * @return list of public actions
     */
    public List<GameAction> getPublicActions(int tableId) {
        List<GameAction> publicActions = new LinkedList<GameAction>();
        for (ActionContainer ac : cache.get(tableId)) {
            if (ac.isPublic()) {
                publicActions.add(ac.getGameAction());
            }
        }

        return publicActions;
    }

    /**
     * Retrieve all actions, public and private, for the given table and user.
     * This method should be used when reconnecting a seated player to a table.
     *
     * @param tableId  table id
     * @param playerId player id
     * @return list of both private and public actions
     */
    public Collection<ActionContainer> getPrivateAndPublicActions(int tableId, int playerId) {
        List<ActionContainer> actions = new LinkedList<ActionContainer>();
        for (ActionContainer ac : cache.get(tableId)) {
            if (ac.isPublic() || ac.getPlayerId() == playerId) {
                actions.add(ac);
            }
        }
        return actions;
    }

//    public List<GameAction> getPrivateAndPublicActions(int tableId, int playerId) {
//        List<GameAction> actions = new LinkedList<GameAction>();
//        for (ActionContainer ac : cache.get(tableId)) {
//            if (ac.isPublic()  ||  ac.getPlayerId() == playerId) {
//                actions.add(ac.getGameAction());
//            }
//        }
//        
//        return actions;
//    }

    public void clear(int tableId) {
        log.trace("clearing action cache for tableId = {}", tableId);
        cache.removeAll(tableId);
        if (handDebugger != null) {
            handDebugger.clearTable(tableId);
        }
    }

}
