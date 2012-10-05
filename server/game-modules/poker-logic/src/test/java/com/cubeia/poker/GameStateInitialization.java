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

package com.cubeia.poker;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.factory.GameTypeFactory;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.texasholdem.TexasHoldem;

public class GameStateInitialization {
	
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createGameTypeByVariant() {
        // PokerState state = new PokerState();
        GameType gameType = GameTypeFactory.createGameType(TELESINA);
        assertThat(gameType, instanceOf(Telesina.class));
        gameType = GameTypeFactory.createGameType(TEXAS_HOLDEM);
        assertThat(gameType, instanceOf(TexasHoldem.class));
    }

    @Test
    public void init() {
        TimingProfile timing = Mockito.mock(TimingProfile.class);
        int anteLevel = 1234;
        PokerSettings settings = new PokerSettings(anteLevel, anteLevel, anteLevel * 2, 100, 1000, timing, 6, BetStrategyName.NO_LIMIT,
        TestUtils.createOnePercentRakeSettings(), null);
        PokerState state = new PokerState();
        GameType gt = GameTypeFactory.createGameType(TELESINA);
        state.init(gt, settings);
        assertThat(state.getAnteLevel(), is(anteLevel));
        assertThat(state.getTimingProfile(), is(timing));
        assertThat(state.getTableSize(), is(6));
    }
}
