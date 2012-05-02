package com.cubeia.poker;

import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.GameTypeFactory;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.texasholdem.TexasHoldem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GameStateInitialization {

    @Mock
    private RNGProvider rngProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createGameTypeByVariant() {
        PokerState state = new PokerState();
        GameType gameType = GameTypeFactory.createGameType(TELESINA, state, rngProvider);
        assertThat(gameType, instanceOf(Telesina.class));
        gameType = GameTypeFactory.createGameType(TEXAS_HOLDEM, state, rngProvider);
        assertThat(gameType, instanceOf(TexasHoldem.class));
    }

    @Test
    public void init() {
        TimingProfile timing = Mockito.mock(TimingProfile.class);
        int anteLevel = 1234;
        PokerSettings settings = new PokerSettings(anteLevel, 100, 1000, timing, TELESINA, 6, BetStrategyName.NO_LIMIT,
        TestUtils.createOnePercentRakeSettings(), null);

        RNGProvider rngProvider = Mockito.mock(RNGProvider.class);
        PokerState state = new PokerState();
        GameType gt = GameTypeFactory.createGameType(TELESINA, state, rngProvider);
        state.init(gt, settings);

        assertThat(state.getAnteLevel(), is(anteLevel));
        assertThat(state.getTimingProfile(), is(timing));
        assertThat(state.getGameType(), is(Telesina.class));
        assertThat(state.getTableSize(), is(6));
    }

}
