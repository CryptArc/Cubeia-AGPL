package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.variant.telesina.hand.TelesinaHandStrengthEvaluator;

public class TelesinaForTesting extends Telesina {

    private static final long serialVersionUID = 1L;

    private int numberOfSentBestHands = 0;
    public int currentRoundId = 0;

    public TelesinaForTesting(RNGProvider rng, TelesinaDeckFactory deckFactory, TelesinaRoundFactory roundFactory, TelesinaDealerButtonCalculator dealerButtonCalculator) {
        super(rng, deckFactory, roundFactory, dealerButtonCalculator);
    }

    @Override
    protected void calculateAndSendBestHandToPlayer(TelesinaHandStrengthEvaluator handStrengthEvaluator, PokerPlayer player) {
        ++numberOfSentBestHands;
    }

    public int getNumberOfSentBestHands() {
        return numberOfSentBestHands;
    }

    @Override
    public Rank getDeckLowestRank() {
        return Rank.TWO;
    }

    @Override
    protected int getBettingRoundId() {
        return currentRoundId;
    }
}
