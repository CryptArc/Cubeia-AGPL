package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.variant.turkish.hand.TurkishFirstRoundToActCalulator;

public class TurkishFirstRoundActingOrderFactory implements PlayerToActCalculatorFactory {

    private static final long serialVersionUID = -5134506667994746747L;

	@Override
    public PlayerToActCalculator createPlayerToActCalculator(PokerContext context) {
        return new TurkishFirstRoundToActCalulator(context.getBlindsInfo().getDealerButtonSeatId(), context.getDeck().getDeckLowestRank());
    }
}
