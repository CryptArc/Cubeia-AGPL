/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

import static com.cubeia.poker.betting.BetStrategyType.FIXED_LIMIT;
import static java.lang.Math.min;

public class FixedLimitBetStrategy implements BetStrategy {

    private static final Logger log = Logger.getLogger(FixedLimitBetStrategy.class);

    private int maxNumberOfBets = 4;

    private long minBet;

    public FixedLimitBetStrategy(long minBet, boolean doubleBetRound) {
        this.minBet = doubleBetRound ? minBet * 2 : minBet;
    }

    @Override
    public BetStrategyType getType() {
        return FIXED_LIMIT;
    }

    @Override
    public long getMinRaiseToAmount(BettingRoundContext context, PokerPlayer player) {
        if (context.allOtherNonFoldedPlayersAreAllIn(player) || !canAffordRaise(context, player) || context.isBettingCapped()) {
            return 0;
        }
        long affordableCost = min(player.getBalance(), costToRaise(context, player));
        long minRaiseToAmount = player.getBetStack() + affordableCost;
        log.debug("Min raise to = " + minRaiseToAmount + ". costToRaise: " + costToRaise(context, player));
        return minRaiseToAmount;
    }

    @Override
    public long getMaxRaiseToAmount(BettingRoundContext context, PokerPlayer player) {
        return getMinRaiseToAmount(context, player);
    }

    @Override
    public long getMinBetAmount(BettingRoundContext context, PokerPlayer player) {
        return min(player.getBalance(), minBet);
    }

    @Override
    public long getMaxBetAmount(BettingRoundContext context, PokerPlayer player) {
        return min(player.getBalance(), minBet);
    }

    @Override
    public long getCallAmount(BettingRoundContext context, PokerPlayer player) {
        long costToCall = context.getHighestBet() - player.getBetStack();
        if (costToCall <= 0) {
            return 0;
        }

        return min(player.getBalance(), costToCall);
    }

    @Override
    public long getNextValidRaiseToLevel(BettingRoundContext context) {
        if (context.getHighestCompleteBet() == 0) return minBet;
        return context.getHighestCompleteBet() + minBet;
    }

    @Override
    public boolean isCompleteBetOrRaise(BettingRoundContext context, long amountRaisedOrBetTo) {
        long currentLevel = context.getHighestCompleteBet();
        long nextLevel = getNextValidRaiseToLevel(context);
        return amountRaisedOrBetTo >= currentLevel + (nextLevel - currentLevel) / 2;
    }

    @Override
    public boolean shouldBettingBeCapped(int betsAndRaises, boolean headsUp) {
        return betsAndRaises >= maxNumberOfBets && !headsUp;
    }

    private boolean canAffordRaise(BettingRoundContext context, PokerPlayer player) {
        return player.getBalance() > getCallAmount(context, player);
    }

    private long costToRaise(BettingRoundContext context, PokerPlayer player) {
        log.debug("Highest complete bet: " + context.getHighestCompleteBet());
        return context.getHighestCompleteBet() + minBet - player.getBetStack();
    }

}
