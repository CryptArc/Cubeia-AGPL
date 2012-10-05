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

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.telesina.TelesinaDealerButtonCalculator;
import com.cubeia.poker.variant.telesina.TelesinaDeckFactory;
import com.cubeia.poker.variant.telesina.TelesinaRoundFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class AbstractTelesinaHandTester extends TestCase {

    protected Injector injector;
    protected MockServerAdapter mockServerAdapter;
    protected PokerState game;
    protected Random rng;

    /**
     * Defaults to 10 seconds
     */
    protected long sitoutTimeLimitMilliseconds = 10000;


    protected void setUpTelesina(TelesinaDeckFactory telesinaDeckFactory, int anteLevel) throws Exception {
        setUpTelesina(telesinaDeckFactory, anteLevel, TestUtils.createZeroRakeSettings());
    }

    /**
     * @param rngProvider
     * @param telesinaDeckFactory
     * @param anteLevel
     * @throws Exception
     */
    protected void setUpTelesina(TelesinaDeckFactory telesinaDeckFactory, int anteLevel, RakeSettings rakeSettings) throws Exception {
        rng = new NonRandomRNG();
        List<Module> list = new LinkedList<Module>();
        list.add(new PokerGuiceModule());
        injector = Guice.createInjector(list);
        setupDefaultGame(rng, telesinaDeckFactory, anteLevel, rakeSettings);
    }


    private void setupDefaultGame(Random random, TelesinaDeckFactory deckFactory, int anteLevel, RakeSettings rakeSettings) {
        mockServerAdapter = new MockServerAdapter();
        mockServerAdapter.random = random;
        game = injector.getInstance(PokerState.class);
        game.setServerAdapter(mockServerAdapter);
        PokerSettings settings = createPokerSettings(anteLevel, rakeSettings);
        Telesina gameType = new Telesina(deckFactory, new TelesinaRoundFactory(), new TelesinaDealerButtonCalculator());
        game.init(gameType, settings);
    }

    protected PokerSettings createPokerSettings(int anteLevel, RakeSettings rakeSettings) {
        PokerSettings settings = new PokerSettings(anteLevel, anteLevel, anteLevel * 2, 1000, 10000,
                TimingFactory.getRegistry().getTimingProfile(MINIMUM_DELAY), 6,
                BetStrategyName.NO_LIMIT, rakeSettings,
                Collections.<Serializable, Serializable>singletonMap("EXTERNAL_TABLE_ID", "xyz"));

        settings.setSitoutTimeLimitMilliseconds(sitoutTimeLimitMilliseconds);
        return settings;
    }

//	protected void setAnteLevel(int anteLevel) {
//		game.init(rng, createPokerSettings(anteLevel));
//	}

    protected void act(int playerId, PokerActionType actionType) {
        act(playerId, actionType, mockServerAdapter.getLastActionRequest().getOption(actionType).getMinAmount());
    }

    protected void act(int playerId, PokerActionType actionType, long amount) {
        PokerAction action = new PokerAction(playerId, actionType);
        action.setBetAmount(amount);
        game.act(action);
    }

    protected void addPlayers(PokerState game, PokerPlayer[] p) {
        for (PokerPlayer pl : p) {
            game.addPlayer(pl);
        }
    }

}
