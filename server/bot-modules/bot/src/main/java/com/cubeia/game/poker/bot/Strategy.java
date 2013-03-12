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

package com.cubeia.game.poker.bot;

import com.cubeia.games.poker.io.protocol.PlayerAction;

import java.util.List;
import java.util.Random;

public class Strategy {

    private static Random rng = new Random();

    public static PlayerAction getAction(List<PlayerAction> allowedActions) {

        // Always post blinds
        for (PlayerAction action : allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                    return action;

                case SMALL_BLIND:
                    return action;

                case ANTE:
                    return action;
            }
        }

        int optionCount = allowedActions.size();
        int optionIndex = rng.nextInt(optionCount);
        PlayerAction playerAction = allowedActions.get(optionIndex);

/*
        if (playerAction.type == ActionType.FOLD) {
            // We need to downplay fold
            if (rng.nextBoolean()) return getAction(allowedActions);

        }
*/

        return playerAction;
    }


    /**
     * @param allowedActions
     * @return true if the returned action should use an arbitrary delay.
     */
    public static boolean useDelay(List<PlayerAction> allowedActions) {
        for (PlayerAction action : allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                    return false;

                case SMALL_BLIND:
                    return false;

                case ANTE:
                    return false;

            }
        }
        return true;
    }


}
