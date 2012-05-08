package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsRound;
import com.cubeia.poker.rounds.dealing.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.dealing.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.ante.AnteRoundHelper;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.NoLimitBetStrategy;
import com.cubeia.poker.variant.telesina.hand.TelesinaPlayerToActCalculator;
import com.cubeia.poker.rounds.dealing.ExposePrivateCardsRound;

/**
 * Factory of Telesina game rounds.
 * The main purpose of this class is to separate round creation from the game type logic
 * to enable unit testing.
 *
 * @author w
 */
public class TelesinaRoundFactory {

    AnteRound createAnteRound(Telesina telesina) {
        return new AnteRound(telesina, new AnteRoundHelper());
    }

    BettingRound createBettingRound(Telesina telesina, int dealerButtonSeatId) {
        return new BettingRound(telesina, dealerButtonSeatId, new TelesinaPlayerToActCalculator(
                telesina.getDeckLowestRank()), new ActionRequestFactory(new NoLimitBetStrategy()), new TelesinaFutureActionsCalculator());
    }

    DealExposedPocketCardsRound createDealExposedPocketCardsRound(Telesina telesina) {
        return new DealExposedPocketCardsRound(telesina);
    }


    ExposePrivateCardsRound createExposePrivateCardsRound(Telesina telesina) {
        return new ExposePrivateCardsRound(telesina);
    }

    DealCommunityCardsRound createDealCommunityCardsRound(Telesina telesina) {
        return new DealCommunityCardsRound(telesina);
    }

    DealInitialPocketCardsRound createDealInitialCardsRound(Telesina telesina) {
        return new DealInitialPocketCardsRound(telesina);
    }

}
