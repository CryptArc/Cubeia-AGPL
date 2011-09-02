package com.cubeia.poker;

import static com.cubeia.poker.gametypes.PokerVariant.TELESINA;
import static com.cubeia.poker.gametypes.PokerVariant.TEXAS_HOLDEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.gametypes.Telesina;
import com.cubeia.poker.gametypes.TexasHoldem;
import com.cubeia.poker.timing.TimingProfile;

public class GameStateInitialization {

	@Test
	public void createGameTypeByVariant() {
		PokerState state = new PokerState();
		GameType gt = state.createGameTypeByVariant(TELESINA);
		assertThat(gt, instanceOf(Telesina.class));
		gt = state.createGameTypeByVariant(TEXAS_HOLDEM);
		assertThat(gt, instanceOf(TexasHoldem.class));
	}
	
	@Test
	public void init() {
		TimingProfile timing = Mockito.mock(TimingProfile.class);
		int anteLevel = 1234;
		PokerSettings settings = new PokerSettings(anteLevel, timing , TELESINA, 6);
		
		PokerState state = new PokerState();
		state.init(settings);
		
		assertThat(state.getAnteLevel(), is(anteLevel));
		assertThat(state.getTimingProfile(), is(timing));
		assertThat(state.getPokerVariant(), is(TELESINA));
		assertThat(state.getTableSize(), is(6));
	}
	
}
