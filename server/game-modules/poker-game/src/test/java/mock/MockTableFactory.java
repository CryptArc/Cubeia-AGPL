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

package mock;

import com.cubeia.poker.*;
import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.variant.factory.GameTypeFactory;

import static com.cubeia.poker.timing.Timings.MINIMUM_DELAY;
import static com.cubeia.poker.variant.PokerVariant.TELESINA;

public class MockTableFactory {

    public static MockTable create() {
        MockTable table = new MockTable();

        PokerSettings settings = new PokerSettings(-1, -1, -1, -1, TimingFactory.getRegistry().getTimingProfile(MINIMUM_DELAY)
                , 6,
                BetStrategyName.NO_LIMIT, TestUtils.createOnePercentRakeSettings(), null);

        PokerState pokerState = new PokerState();
        GameType gameType = GameTypeFactory.createGameType(TELESINA, pokerState, null);
        pokerState.init(gameType, settings);
        pokerState.setGameType(new MockGame());
        table.getGameState().setState(pokerState);
        return table;
    }

}
