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

import static com.cubeia.poker.timing.Timings.MINIMUM_DELAY;
import static com.cubeia.poker.variant.PokerVariant.TELESINA;

import java.util.Random;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingFactory;

public class MockTableFactory {

    public static MockTable create() {
		MockTable table = new MockTable();
		
		PokerSettings settings = new PokerSettings(-1, -1, -1, TimingFactory.getRegistry().getTimingProfile(MINIMUM_DELAY), TELESINA, 6, 
		    BetStrategyName.NO_LIMIT, TestUtils.createOnePercentRakeSettings(), null);

		final Random rng = new Random();
		PokerState pokerState = new PokerState();
		pokerState.init(new RNGProvider() {
            
			private static final long serialVersionUID = -1911497186832055195L;

			@Override
            public Random getRNG() {
                return rng;
            }
        }, settings);
		pokerState.setGameType(new MockGame());
		table.getGameState().setState(pokerState);
		return table;
	}
	
}
