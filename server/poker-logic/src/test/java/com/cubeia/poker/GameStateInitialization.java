package com.cubeia.poker;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.texasholdem.TexasHoldem;

public class GameStateInitialization {

	@Test
	public void createGameTypeByVariant() {
		PokerState state = new PokerState();
		RNGProvider rngProvider = Mockito.mock(RNGProvider.class);
        GameType gt = state.createGameTypeByVariant(rngProvider, TELESINA);
		assertThat(gt, instanceOf(Telesina.class));
		gt = state.createGameTypeByVariant(rngProvider, TEXAS_HOLDEM);
		assertThat(gt, instanceOf(TexasHoldem.class));
	}
	
	@Test
	public void init() {
		TimingProfile timing = Mockito.mock(TimingProfile.class);
		int anteLevel = 1234;
		PokerSettings settings = new PokerSettings(anteLevel, timing , TELESINA, 6);
		
        RNGProvider rngProvider = Mockito.mock(RNGProvider.class);
		PokerState state = new PokerState();
		state.init(rngProvider, settings);
		
		assertThat(state.getAnteLevel(), is(anteLevel));
		assertThat(state.getTimingProfile(), is(timing));
		assertThat(state.getPokerVariant(), is(TELESINA));
		assertThat(state.getTableSize(), is(6));
	}
	
}
