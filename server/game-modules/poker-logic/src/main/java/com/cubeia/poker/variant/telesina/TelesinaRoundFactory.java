/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.rounds.betting.ActionRequestFactory;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsRound;
import com.cubeia.poker.rounds.dealing.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.dealing.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.ante.AnteRoundHelper;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.NoLimitBetStrategy;
import com.cubeia.poker.rounds.dealing.ExposePrivateCardsRound;
import com.cubeia.poker.variant.telesina.hand.TelesinaPlayerToActCalculator;

import java.util.List;

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
        int buttonSeatId = context.getBlindsInfo().getDealerButtonSeatId();
        int betLevel = 2 * context.getSettings().getAnteAmount();
        return new BettingRound(buttonSeatId, context, serverAdapterHolder, playerToActCalculator, actionRequestFactory, futureActionsCalculator, betLevel);
    }

    DealExposedPocketCardsRound createDealExposedPocketCardsRound(Telesina telesina) {
        return new DealExposedPocketCardsRound(telesina);
    }


    ExposePrivateCardsRound createExposePrivateCardsRound(Telesina telesina, List<Integer> revealOrder) {
        return new ExposePrivateCardsRound(telesina, revealOrder);
    }

    DealCommunityCardsRound createDealCommunityCardsRound(Telesina telesina) {
        return new DealCommunityCardsRound(telesina);
    }

    DealInitialPocketCardsRound createDealInitialCardsRound(Telesina telesina) {
        return new DealInitialPocketCardsRound(telesina);
    }

}
