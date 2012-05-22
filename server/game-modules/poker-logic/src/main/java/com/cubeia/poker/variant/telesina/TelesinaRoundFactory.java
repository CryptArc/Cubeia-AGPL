package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.rounds.betting.PlayerToActCalculator;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsRound;
import com.cubeia.poker.rounds.dealing.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.dealing.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.ante.AnteRoundHelper;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.NoLimitBetStrategy;
import com.cubeia.poker.rounds.dealing.ExposePrivateCardsRound;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.variant.telesina.hand.TelesinaPlayerToActCalculator;

/**
 * Factory of Telesina game rounds.
 * The main purpose of this class is to separate round creation from the game type logic
 * to enable unit testing.
 *
 * @author w
 */
public class TelesinaRoundFactory {

    AnteRound createAnteRound(PokerContext context, ServerAdapterHolder serverAdapterHolder) {
        return new AnteRound(context, serverAdapterHolder, new AnteRoundHelper(context, serverAdapterHolder));
    }

    BettingRound createBettingRound(PokerContext context, ServerAdapterHolder serverAdapterHolder, Rank lowestRank) {
        ActionRequestFactory actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
        TelesinaPlayerToActCalculator playerToActCalculator = new TelesinaPlayerToActCalculator(lowestRank);
        TelesinaFutureActionsCalculator futureActionsCalculator = new TelesinaFutureActionsCalculator();
        return new BettingRound(context.getBlindsInfo().getDealerButtonSeatId(), context, serverAdapterHolder, playerToActCalculator, actionRequestFactory, futureActionsCalculator);
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
