package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.rounds.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.DealVelaCardRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.ante.AnteRoundHelper;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.TelesinaPlayerToActCalculator;

/**
 * Factory of Telesina game rounds.
 * The main purpose of this class is to separate round creation from the game type logic
 * to enable unit testing.
 * @author w
 */
public class TelesinaRoundFactory {

    AnteRound createAnteRound(Telesina telesina) {
        return new AnteRound(telesina, new AnteRoundHelper());
    }

    BettingRound createBettingRound(Telesina telesina, int dealerButtonSeatId) {
        return new BettingRound(telesina, dealerButtonSeatId, new TelesinaPlayerToActCalculator(telesina.getDeckLowestRank()));
   }

    DealExposedPocketCardsRound createDealPocketCardsRound() {
        return new DealExposedPocketCardsRound();
    }

    DealVelaCardRound createDealVelaCardRound() {
        return new DealVelaCardRound();
    }
    
}
