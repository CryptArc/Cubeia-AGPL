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

public class ActionContainer {

    private final Integer playerId;

    private final Integer excludedPlayerId;

    private final GameAction gameAction;

    private final Long timestamp = System.currentTimeMillis();

    private ActionContainer(Integer playerId, GameAction gameAction, Integer excludedPlayerId) {
        this.playerId = playerId;
        this.gameAction = gameAction;
        this.excludedPlayerId = excludedPlayerId;
    }

    private ActionContainer(GameAction gameAction, Integer excludedPlayerId) {
        this(null, gameAction, excludedPlayerId);
    }

    private ActionContainer(Integer playerId, GameAction gameAction) {
        this(playerId, gameAction, null);
    }

    private ActionContainer(GameAction gameAction) {
        this(null, gameAction, null);
    }

    public static ActionContainer createPublic(GameAction gameAction) {
        return new ActionContainer(null, gameAction);
    }

    public static ActionContainer createPublic(GameAction gameAction, Integer excludedPlayerId) {
        return new ActionContainer(null, gameAction, excludedPlayerId);
    }

    public static ActionContainer createPrivate(int playerId, GameAction gameAction) {
        return new ActionContainer(playerId, gameAction);
    }

    public int getPlayerId() {
        return playerId;
    }

    public Integer getExcludedPlayerId() {
        return excludedPlayerId;
    }

    public GameAction getGameAction() {
        return gameAction;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isPublic() {
        return playerId == null;
    }
}
