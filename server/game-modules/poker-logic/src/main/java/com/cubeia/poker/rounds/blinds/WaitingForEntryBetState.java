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

package com.cubeia.poker.rounds.blinds;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class WaitingForEntryBetState extends AbstractBlindsState {

    private static final long serialVersionUID = 1L;

    @Override
    public void bigBlind(int playerId, PokerContext context, BlindsRound blindsRound) {
        PokerPlayer player = context.getPlayerInCurrentHand(playerId);
        if (player.getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND)) {
            player.setHasPostedEntryBet(true);
            player.addBet(100);
            blindsRound.bigBlindPosted();
        } else {
            throw new IllegalArgumentException("Player " + player + " is not allowed to post big blind. Options were " + player.getActionRequest());
        }
    }

    @Override
    public void declineEntryBet(Integer playerId, PokerContext context, BlindsRound blindsRound) {
        PokerPlayer player = context.getPlayerInCurrentHand(playerId);
        if (player.getActionRequest().isOptionEnabled(PokerActionType.DECLINE_ENTRY_BET)) {
            blindsRound.entryBetDeclined(player);
        } else {
            throw new IllegalArgumentException("Player " + player + " is not allowed to decline entry bet.");
        }
    }

    @Override
    public void timeout(PokerContext context, BlindsRound round) {
        PokerPlayer nextEntryBetter = round.getNextEntryBetter();
        declineEntryBet(nextEntryBetter.getId(), context, round);
    }

}
