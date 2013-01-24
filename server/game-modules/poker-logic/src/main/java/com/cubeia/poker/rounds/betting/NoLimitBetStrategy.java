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

import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.player.PokerPlayer;
import org.apache.log4j.Logger;

import static java.lang.Math.min;

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
 * 3. The currently highest (valid) bet (that is, an incomplete bet or raise should not count).
 * 4. The size of the last raise or bet
 * 5. Are all other players all-in?
 */
public class NoLimitBetStrategy implements BetStrategy {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(NoLimitBetStrategy.class);

    private final long minBet;

    public NoLimitBetStrategy(long minBet) {
        this.minBet = minBet;
    }

    @Override
    public long getMinBetAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        return min(player.getBalance(), minBet);
    }

    @Override
    public long getMaxBetAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        return player.getBalance();
    }

    @Override
    public BetStrategyType getType() {
        return BetStrategyType.NO_LIMIT;
    }

    @Override
    public long getMinRaiseToAmount(BettingRoundContext context, PokerPlayer player) {
        if (context.allOtherNonFoldedPlayersAreAllIn(player) || !canAffordRaise(context, player)) {
            return 0;
        }

        long raiseTo = getNextValidRaiseToLevel(context);
        log.debug("Next valid raise level: " + raiseTo + ". Highest complete bet: " + context.getHighestCompleteBet());

        long cost = raiseTo - player.getBetStack();
        if (cost < 0) {
            // Sanity check that current high bet is not lower than this player's current bet.
            throw new IllegalStateException(String.format("Current high bet (%d) is lower than player's bet stack (%d). MaxRaise(%d) Balance(%d)",
                                                          context.getHighestCompleteBet(), player.getBetStack(), raiseTo, player.getBalance()));
        }
        long affordableCost = min(player.getBalance(), cost);
        long minRaiseToAmount = player.getBetStack() + affordableCost;
        log.debug("Min raise to amount is: " + minRaiseToAmount);
        return minRaiseToAmount;
    }

    @Override
    public long getMaxRaiseToAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        if (bettingRoundContext.allOtherNonFoldedPlayersAreAllIn(player) || !canAffordRaise(bettingRoundContext, player)) {
            return 0;
        }
        return player.getBetStack() + player.getBalance();
    }

    @Override
    public long getCallAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        long diff = bettingRoundContext.getHighestBet() - player.getBetStack();
        if (diff <= 0) {
            return 0;
        }

        return min(player.getBalance(), diff);
    }

    @Override
    public long getNextValidRaiseToLevel(BettingRoundContext context) {
        if (context.getHighestCompleteBet() == 0) return context.getHighestBet() + minBet;
        return context.getHighestBet() + context.getSizeOfLastCompleteBetOrRaise();
    }

    @Override
    public boolean isCompleteBetOrRaise(BettingRoundContext context, long amountRaisedOrBetTo) {
        return amountRaisedOrBetTo >= getNextValidRaiseToLevel(context);
    }

    @Override
    public boolean shouldBettingBeCapped(int betsAndRaises, boolean headsUp) {
        return false;
    }

    private boolean canAffordRaise(BettingRoundContext context, PokerPlayer player) {
        return player.getBalance() > getCallAmount(context, player);
    }

}
