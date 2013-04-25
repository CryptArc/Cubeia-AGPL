package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.player.PokerPlayer;

public class PotLimitBetStrategy extends NoLimitBetStrategy {


    public PotLimitBetStrategy(long minBet) {
        super(minBet);
    }

    @Override
    public BetStrategyType getType() {
        return BetStrategyType.FIXED_LIMIT;
    }

    @Override
    public long getMaxBetAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        return Math.min(bettingRoundContext.getPotSize(),player.getBalance());
    }

    @Override
    public long getMaxRaiseToAmount(BettingRoundContext bettingRoundContext, PokerPlayer player) {
        long maxRaiseToAmount = super.getMaxRaiseToAmount(bettingRoundContext,player);
        long potSizeAfterCall = getCallAmount(bettingRoundContext, player) + bettingRoundContext.getPotSize();
        long potLimitedRaiseToAmount = bettingRoundContext.getHighestBet() + potSizeAfterCall;
        return Math.min(potLimitedRaiseToAmount,maxRaiseToAmount);
    }

}
