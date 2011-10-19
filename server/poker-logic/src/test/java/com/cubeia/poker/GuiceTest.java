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

import static com.cubeia.poker.timing.Timings.MINIMUM_DELAY;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.variant.PokerVariant;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class GuiceTest extends TestCase {

	protected Injector injector;

	protected MockServerAdapter mockServerAdapter;
	
	protected PokerState game;

	protected PokerVariant variant = PokerVariant.TEXAS_HOLDEM;
	
	protected RNGProvider rng = new DummyRNGProvider();
	
	/** Defaults to 10 seconds */
	protected long sitoutTimeLimitMilliseconds = 10000; 
	
	@Override
	protected void setUp() throws Exception {
		List<Module> list = new LinkedList<Module>();
		list.add(new PokerGuiceModule());
		injector = Guice.createInjector(list);
		setupDefaultGame();
	}
	
	protected void setupDefaultGame() {
		mockServerAdapter = new MockServerAdapter();
		game = injector.getInstance(PokerState.class);
		game.setServerAdapter(mockServerAdapter);
		game.init(rng, createPokerSettings(100));
	}
	
	protected PokerSettings createPokerSettings(int anteLevel) {
		PokerSettings settings = new PokerSettings(anteLevel, 1000, 10000, 
				TimingFactory.getRegistry().getTimingProfile(MINIMUM_DELAY), variant, 6, 
				BetStrategyName.NO_LIMIT, TestUtils.createZeroRakeSettings(), null);
		
		settings.setSitoutTimeLimitMilliseconds(sitoutTimeLimitMilliseconds);
		return settings;
	}
}
