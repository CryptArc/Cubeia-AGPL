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

package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

public class ActionRequestFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ActionRequestFactory.class);

    private final BetStrategy betStrategy;

    public ActionRequestFactory(BetStrategy betStrategy) {
        this.betStrategy = betStrategy;
    }

    public ActionRequest createFoldCallRaiseActionRequest(BettingRoundContext bettingRoundContext, PokerPlayer p) {
        PossibleAction fold = new PossibleAction(PokerActionType.FOLD, 0);
        PossibleAction call = new PossibleAction(PokerActionType.CALL, betStrategy.getCallAmount(bettingRoundContext, p));
        PossibleAction raise = new PossibleAction(PokerActionType.RAISE, betStrategy.getMinRaiseToAmount(bettingRoundContext, p),
                betStrategy.getMaxRaiseToAmount(bettingRoundContext, p));

        ActionRequest request = new ActionRequest();

        // Check if valid to raise
        boolean raiseAllowed = true;
        if (raise.getMinAmount() == p.getLastRaiseLevel()) {
            log.debug("Raise level [{}] has not changed for player[{}]. Player is not allowed to raise for this round.", p.getLastRaiseLevel(), p.getId());
            raiseAllowed = false;
        }

        /* We will check:
         * 1. If the player have more cash than a call
         * 2. If raise min amount is > call (can be 0 if all other players are all in)
         */
        if (raiseAllowed && p.getBalance() > call.getMinAmount() && raise.getMinAmount() > call.getMinAmount()) {
            request.setOptions(Arrays.asList(fold, call, raise));
        } else {
            log.debug("Player {} is only allowed to call or fold. balance[" + p.getBalance() + "] call.min[" + call.getMinAmount() + "] raise.min[" + raise.getMinAmount() + "]", p.getId());
            request.setOptions(Arrays.asList(fold, call));
        }
        request.setPlayerId(p.getId());
        return request;
    }

    public ActionRequest createFoldCheckBetActionRequest(BettingRoundContext bettingRoundContext, PokerPlayer p) {
        PossibleAction fold = new PossibleAction(PokerActionType.FOLD, 0);
        PossibleAction check = new PossibleAction(PokerActionType.CHECK, 0);
        PossibleAction bet = new PossibleAction(PokerActionType.BET, betStrategy.getMinBetAmount(bettingRoundContext, p),
                betStrategy.getMaxBetAmount(bettingRoundContext, p));
        ActionRequest request = new ActionRequest();
        request.setOptions(Arrays.asList(fold, check, bet));
        request.setPlayerId(p.getId());
        return request;
    }

}
