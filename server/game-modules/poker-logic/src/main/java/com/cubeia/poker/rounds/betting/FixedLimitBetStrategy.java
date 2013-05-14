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

import java.math.BigDecimal;

import static com.cubeia.poker.betting.BetStrategyType.FIXED_LIMIT;

public class FixedLimitBetStrategy implements BetStrategy {

    private static final Logger log = Logger.getLogger(FixedLimitBetStrategy.class);

    private int maxNumberOfBets = 4;

    private BigDecimal minBet;

    public FixedLimitBetStrategy(BigDecimal minBet, boolean doubleBetRound) {
        this.minBet = doubleBetRound ? minBet.multiply(new BigDecimal("2")) : minBet;
    }

    @Override
    public BetStrategyType getType() {
        return FIXED_LIMIT;
    }

    @Override
    public BigDecimal getMinRaiseToAmount(BettingRoundContext context, PokerPlayer player) {
        if (context.allOtherNonFoldedPlayersAreAllIn(player) || !canAffordRaise(context, player) || context.isBettingCapped()) {
            return BigDecimal.ZERO;
        }
        BigDecimal affordableCost = player.getBalance().min(costToRaise(context, player));
        BigDecimal minRaiseToAmount = player.getBetStack().add(affordableCost);
        log.debug("Min raise to = " + minRaiseToAmount + ". costToRaise: " + costToRaise(context, player));
        return minRaiseToAmount;
    }

    @Override
    public BigDecimal getMaxRaiseToAmount(BettingRoundContext context, PokerPlayer player) {
        return getMinRaiseToAmount(context, player);
    }

    @Override
    public BigDecimal getMinBetAmount(BettingRoundContext context, PokerPlayer player) {
        return player.getBalance().min(minBet);
    }

    @Override
    public BigDecimal getMaxBetAmount(BettingRoundContext context, PokerPlayer player) {
        return player.getBalance().min(minBet);
    }

    @Override
    public BigDecimal getCallAmount(BettingRoundContext context, PokerPlayer player) {
        BigDecimal costToCall = context.getHighestBet().subtract(player.getBetStack());
        if (costToCall.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return player.getBalance().min(costToCall);
    }

    @Override
    public BigDecimal getNextValidRaiseToLevel(BettingRoundContext context) {
        if (context.getHighestCompleteBet().compareTo(BigDecimal.ZERO) == 0) return minBet;
        return context.getHighestCompleteBet().add(minBet);
    }

    @Override
    public boolean isCompleteBetOrRaise(BettingRoundContext context, BigDecimal amountRaisedOrBetTo) {
        BigDecimal currentLevel = context.getHighestCompleteBet();
        BigDecimal nextLevel = getNextValidRaiseToLevel(context);
        BigDecimal divide = currentLevel.add(nextLevel.subtract(currentLevel).divide(new BigDecimal("2")));
        return amountRaisedOrBetTo.compareTo(divide) >= 0;
    }

    @Override
    public boolean shouldBettingBeCapped(int betsAndRaises, boolean headsUp) {
        return betsAndRaises >= maxNumberOfBets && !headsUp;
    }

    private boolean canAffordRaise(BettingRoundContext context, PokerPlayer player) {
        return player.getBalance().compareTo(getCallAmount(context, player)) > 0;
    }

    private BigDecimal costToRaise(BettingRoundContext context, PokerPlayer player) {
        log.debug("Highest complete bet: " + context.getHighestCompleteBet());
        return context.getHighestCompleteBet().add(minBet).subtract(player.getBetStack());
    }

}
