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


import com.cubeia.poker.context.PokerContext;

public abstract class AbstractBlindsState implements BlindsState {

    private static final long serialVersionUID = 1L;

    @Override
    public void bigBlind(int playerId, PokerContext context, BlindsRound round) {
        throw new IllegalStateException();
    }

    @Override
    public void smallBlind(int playerId, PokerContext context, BlindsRound round) {
        throw new IllegalStateException();
    }

    @Override
    public void declineEntryBet(int playerId, PokerContext context, BlindsRound blindsRound) {
        throw new IllegalStateException();
    }

    @Override
    public void deadSmallBlind(int playerId, PokerContext context, BlindsRound blindsRound) {
        throw new IllegalStateException();
    }

    @Override
    public void bigBlindPlusDeadSmallBlind(int playerId, PokerContext context, BlindsRound round) {
        throw new IllegalStateException();
    }

    @Override
    public void timeout(PokerContext context, BlindsRound round) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

}
