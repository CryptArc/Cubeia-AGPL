package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.hand.PokerEvaluator;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.ante.AnteRoundHelper;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.TelesinaPlayerToActCalculator;

public class TelesinaRoundFactory {

    AnteRound createAnteRound(Telesina telesina) {
        return new AnteRound(telesina, new AnteRoundHelper());
    }

    BettingRound createBettingRound(Telesina telesina, int dealerButtonSeatId) {
        return new BettingRound(telesina, dealerButtonSeatId, new TelesinaPlayerToActCalculator(new PokerEvaluator()));
   }

}
