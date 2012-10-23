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

import com.cubeia.poker.player.PokerPlayer;

/**
 * Implementation of no limit betting strategy.
 * <p/>
 * Rules:
 * Players may bet or raise their entire stack at any given time.
 * Exception: when all players are all-in except for the player to act, in this case he may only call or fold.
 * When betting, the size of the bet must be >= the min bet according to the configuration.
 * When raising, the size of the raise must be >= the size of the last bet or raise.
 * <p/>
 * What we need to know:
 * 1. Min bet (as configured)
 * 2. Player to act's current bet stack and total stack
 * 3. The currently highest bet
 * 4. The size of the last raise or bet
 * 5. Are all other players all-in?
 */
public class NoLimitBetStrategy implements BetStrategy {

    private static final long serialVersionUID = 1L;

//	private final BettingRoundContext bettingRoundContext;
//
//	public NoLimitBetStrategy(BettingRoundContext bettingRoundContext) {
//		this.bettingRoundContext = bettingRoundContext;
//	}

    @Override
    public long getMaxBetAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        return player.getBalance();
    }

    @Override
    public long getMaxRaiseToAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        if (bettingRoundContext.allOtherNonFoldedPlayersAreAllIn(player)) {
            return 0;
        }

        return player.getBalance() + player.getBetStack();
    }

    @Override
    public long getMinBetAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        return Math.min(player.getBalance(), bettingRoundContext.getMinBet());
    }

    @Override
    public long getMinRaiseToAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        if (bettingRoundContext.allOtherNonFoldedPlayersAreAllIn(player)) {
            return 0;
        }

        // Check if we have stored a next bet level, if not create one from previous bet.
        long raiseTo = bettingRoundContext.getNextValidRaiseLevel();
        if (raiseTo == 0) {
            raiseTo = bettingRoundContext.getHighestBet() + bettingRoundContext.getSizeOfLastBetOrRaise();
        }

        long cost = raiseTo - player.getBetStack();
        long affordableCost = Math.min(player.getBalance(), cost);

        if (cost < 0) {
            throw new IllegalStateException(String.format("Current high bet (%d) is lower than player's bet stack (%d). MaxRaise(%d) Balance(%d)", bettingRoundContext.getHighestBet(), player.getBetStack(), raiseTo, player.getBalance()));
        }

        return player.getBetStack() + affordableCost;
    }

    @Override
    public long getCallAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        long diff = bettingRoundContext.getHighestBet() - player.getBetStack();
        if (diff <= 0) {
            throw new IllegalStateException("Nothing to call");
        }

        return Math.min(player.getBalance(), diff);
    }

}
