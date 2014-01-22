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

package com.cubeia.poker.variant;

import static com.cubeia.poker.rounds.betting.BettingRoundName.FLOP;

import com.cubeia.poker.rounds.RoundCreator;
import com.cubeia.poker.rounds.ante.AnteRoundCreator;
import com.cubeia.poker.rounds.betting.BettingRoundCreator;
import com.cubeia.poker.rounds.betting.BettingRoundName;
import com.cubeia.poker.rounds.betting.FromBigBlindFactory;
import com.cubeia.poker.rounds.betting.FromDealerButtonFactory;
import com.cubeia.poker.rounds.betting.TurkishSecondRoundActingOrderFactory;
import com.cubeia.poker.rounds.betting.PlayerToActCalculatorFactory;
import com.cubeia.poker.rounds.betting.TelesinaActingOrderFactory;
import com.cubeia.poker.rounds.blinds.BlindsRoundCreator;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsCreator;
import com.cubeia.poker.rounds.dealing.DealPocketCardsRoundCreator;
import com.cubeia.poker.rounds.discard.DiscardRoundCreator;

public class RoundCreators {
    public static RoundCreator dealFaceDownAndFaceUpCards(int faceDownCards, int faceUpCards) {
        return new DealPocketCardsRoundCreator(faceDownCards, faceUpCards);
    }

    public static RoundCreator dealFaceUpCards(int faceUpCards) {
        return new DealPocketCardsRoundCreator(0, faceUpCards);
    }

    public static DealCommunityCardsCreator dealCommunityCards(int numberOfCardsToDeal) {
        return new DealCommunityCardsCreator(numberOfCardsToDeal);
    }

    public static DiscardRoundCreator discardRound(int cardsToDiscard) {
        return new DiscardRoundCreator(cardsToDiscard);
    }

    public static BettingRoundCreator bettingRound(BettingRoundName roundName) {
        return new BettingRoundCreator(roundName, new FromDealerButtonFactory(), true);
    }

    public static BettingRoundCreator bettingRound(BettingRoundName roundName, PlayerToActCalculatorFactory playerToActCalculatorFactory) {
        return new BettingRoundCreator(roundName, playerToActCalculatorFactory, true);
    }

    public static BettingRoundCreator bettingRound(BettingRoundName roundName, boolean flipCardsOnAllInShowdown) {
        return new BettingRoundCreator(roundName, new FromDealerButtonFactory(), flipCardsOnAllInShowdown);
    }

    public static BettingRoundCreator bettingRound(BettingRoundName roundName, PlayerToActCalculatorFactory playerToActCalculatorFactory, boolean flipCardsOnAllInShowdown) {
        return new BettingRoundCreator(roundName, playerToActCalculatorFactory, flipCardsOnAllInShowdown);
    }

    public static PlayerToActCalculatorFactory fromBigBlind() {
        return new FromBigBlindFactory();
    }

    public static PlayerToActCalculatorFactory fromBestHand() {
        return new TelesinaActingOrderFactory();
    }
    
    public static PlayerToActCalculatorFactory fromOpener() {
    	return new TurkishSecondRoundActingOrderFactory();
    }
    public static PlayerToActCalculatorFactory turkishOpenRound() {
		return new FromDealerButtonFactory();
	}

    public static BettingRoundCreator bettingRound(PlayerToActCalculatorFactory playerToActCalculatorFactory) {
        return new BettingRoundCreator(FLOP, playerToActCalculatorFactory, true); // TODO: Flop isn't really right.
    }

    public static DealPocketCardsRoundCreator dealFaceDownCards(int numberOfCards) {
        return new DealPocketCardsRoundCreator(numberOfCards);
    }

    public static BlindsRoundCreator blinds() {
        return blinds(true);
    }

    public static BlindsRoundCreator blinds(boolean flipCardsOnAllInShowdown) {
        return new BlindsRoundCreator(flipCardsOnAllInShowdown);
    }

    public static AnteRoundCreator ante() {
        return new AnteRoundCreator();
    }
    
    public static DealPocketCardsRoundCreator dealNewCards() {
    	return new DealPocketCardsRoundCreator(2);
    }
}
