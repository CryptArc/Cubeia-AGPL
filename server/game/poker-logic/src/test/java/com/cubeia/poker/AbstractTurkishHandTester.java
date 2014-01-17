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

import static com.cubeia.poker.variant.RoundCreators.ante;
import static com.cubeia.poker.variant.RoundCreators.bettingRound;
import static com.cubeia.poker.variant.RoundCreators.dealFaceDownCards;
import static com.cubeia.poker.variant.RoundCreators.dealNewCards;
import static com.cubeia.poker.variant.RoundCreators.discardRound;
import static com.cubeia.poker.variant.RoundCreators.fromOpener;
import static com.cubeia.poker.variant.RoundCreators.turkishOpenRound;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.model.BlindsLevel;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.variant.GameTypes;
import com.cubeia.poker.variant.GenericPokerGame;
import com.cubeia.poker.variant.PokerGameBuilder;
import com.cubeia.poker.variant.PokerVariant;
import com.cubeia.poker.variant.turkish.TurkishDeckFactory;
import com.cubeia.poker.variant.turkish.hand.TurkishHandStrengthEvaluator;
import com.google.common.primitives.Ints;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import junit.framework.TestCase;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public abstract class AbstractTurkishHandTester extends TestCase {

    protected Injector injector;
    protected MockServerAdapter mockServerAdapter;
    protected PokerState game;
    protected Random rng;

    /**
     * Defaults to 10 seconds
     */
    protected long sitoutTimeLimitMilliseconds = 10000;


    protected void setUpTurkish(TurkishDeckFactory turkishDeckFactory, BigDecimal anteLevel) throws Exception {
        setUpTurkish(turkishDeckFactory, anteLevel, TestUtils.createZeroRakeSettings());
    }

    protected void setUpTurkish(TurkishDeckFactory turkishDeckFactory, BigDecimal anteLevel, RakeSettings rakeSettings) throws Exception {
        rng = new NonRandomRNG();
        List<Module> list = new LinkedList<Module>();
        list.add(new PokerGuiceModule());
        injector = Guice.createInjector(list);
        setupDefaultGame(rng, turkishDeckFactory, anteLevel, rakeSettings);
    }


    private void setupDefaultGame(Random random, TurkishDeckFactory deckFactory, BigDecimal anteLevel, RakeSettings rakeSettings) {
        mockServerAdapter = new MockServerAdapter();
        mockServerAdapter.random = random;
        game = injector.getInstance(PokerState.class);
        game.setServerAdapter(mockServerAdapter);
        PokerSettings settings = createPokerSettings(anteLevel, rakeSettings);
        GenericPokerGame gameType = createTurkish(deckFactory);
        game.init(gameType, settings);
    }

    protected PokerSettings createPokerSettings(BigDecimal anteLevel, RakeSettings rakeSettings) {
        BlindsLevel level = new BlindsLevel(anteLevel, anteLevel.multiply(new BigDecimal(2)), anteLevel);
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.TURKISH, level, betStrategy, new BigDecimal(1000), new BigDecimal(10000),
                TimingFactory.getRegistry().getTimingProfile("MINIMUM_DELAY"), 5, rakeSettings, new Currency("EUR",2),
                Collections.<Serializable, Serializable>singletonMap("EXTERNAL_TABLE_ID", "xyz"));

        settings.setSitoutTimeLimitMilliseconds(sitoutTimeLimitMilliseconds);
        return settings;
    }

//	protected void setAnteLevel(int anteLevel) {
//		game.init(rng, createPokerSettings(anteLevel));
//	}
    
    protected void discard(int playerId, int[] cardsToDiscard) {
    	DiscardAction action = new DiscardAction(playerId, Ints.asList(cardsToDiscard));
    	game.act(action);
    }

    protected void act(int playerId, PokerActionType actionType) {
        act(playerId, actionType, mockServerAdapter.getLastActionRequest().getOption(actionType).getMinAmount());
    }

    protected void act(int playerId, PokerActionType actionType, BigDecimal amount) {
        PokerAction action = new PokerAction(playerId, actionType);
        action.setBetAmount(amount);
        game.act(action);
    }

    protected void addPlayers(PokerState game, PokerPlayer[] p) {
        for (PokerPlayer pl : p) {
            game.addPlayer(pl);
        }
    }
    
    public static GenericPokerGame createTurkish(TurkishDeckFactory deckFactory) {
        return new PokerGameBuilder().
                        withRounds(
                                ante(),
                                dealFaceDownCards(5),
                                bettingRound(turkishOpenRound()),
                                discardRound(4),
                                dealNewCards(),	
                                bettingRound(fromOpener()))
                        .withDeckProvider(deckFactory).
                        withHandEvaluator(new TurkishHandStrengthEvaluator(Rank.SEVEN)).build();
    }

}
