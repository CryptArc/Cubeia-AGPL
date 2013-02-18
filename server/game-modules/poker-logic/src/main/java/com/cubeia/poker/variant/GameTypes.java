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

import com.cubeia.poker.rounds.RoundCreator;
import com.cubeia.poker.rounds.betting.BettingRoundCreator;
import com.cubeia.poker.rounds.betting.BettingRoundName;
import com.cubeia.poker.rounds.blinds.BlindsRoundCreator;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsCreator;
import com.cubeia.poker.rounds.dealing.DealPocketCardsRoundCreator;

import static com.cubeia.poker.rounds.betting.BettingRoundName.FLOP;
import static com.cubeia.poker.rounds.betting.BettingRoundName.PRE_FLOP;
import static com.cubeia.poker.rounds.betting.BettingRoundName.RIVER;
import static com.cubeia.poker.rounds.betting.BettingRoundName.TURN;

public class GameTypes {

    public static GameType createTexasHoldem() {
        return new PokerGameBuilder().withRounds(
                        blinds(),
                        dealPocketCards(2),
                        bettingRound(PRE_FLOP),
                        dealCommunityCards(3),
                        bettingRound(FLOP),
                        dealCommunityCards(1),
                        bettingRound(TURN),
                        dealCommunityCards(1),
                        bettingRound(RIVER)).build();
    }

    public static GameType createTelesina() {
        return new PokerGameBuilder().withRounds(
                        ante(),
                        dealPocketCards(1, 1),
                        bettingRound(),
                        dealPocketCards(0, 1),
                        bettingRound(),
                        dealPocketCards(0, 1),
                        bettingRound(),
                        dealPocketCards(0, 1),
                        bettingRound(),
                        dealCommunityCards(1),
                        bettingRound()).build();
    }

    private static RoundCreator dealPocketCards(int faceDownCards, int faceUpCards) {
        return null;
    }

    private static DealCommunityCardsCreator dealCommunityCards(int numberOfCardsToDeal) {
        return new DealCommunityCardsCreator(numberOfCardsToDeal);
    }

    private static BettingRoundCreator bettingRound(BettingRoundName roundName) {
        return new BettingRoundCreator(roundName);
    }

    private static BettingRoundCreator bettingRound() {
        return new BettingRoundCreator(null);
    }

    private static DealPocketCardsRoundCreator dealPocketCards(int numberOfCards) {
        return new DealPocketCardsRoundCreator(numberOfCards);
    }

    private static BlindsRoundCreator blinds() {
        return new BlindsRoundCreator();
    }

    private static BlindsRoundCreator ante() {
        return new BlindsRoundCreator();
    }
}
