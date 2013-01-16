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

package com.cubeia.games.poker.debugger;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.service.Contract;
import com.cubeia.firebase.api.service.RoutableService;

public interface HandDebuggerContract extends Contract, RoutableService {

    void start();

    void addPublicAction(int tableId, GameAction action);

    void addPrivateAction(int tableId, int playerId, GameAction action);

    void clearTable(int tableId);

    void sendHttpLink(int tableId, int playerId);

    void updatePlayerInfo(int tableId, int playerId, String name, boolean isSittingIn, long tableBalance, long betStack);

}
