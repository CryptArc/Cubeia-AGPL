package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.variant.turkish.hand.TurkishSecondRoundToActCalulator;

public class TurkishSecondRoundActingOrderFactory implements PlayerToActCalculatorFactory {

    private static final long serialVersionUID = -5134506667994746747L;

	@Override
    public PlayerToActCalculator createPlayerToActCalculator(PokerContext context) {
        return new TurkishSecondRoundToActCalulator(context.getDeck().getDeckLowestRank());
    }
}
