package com.cubeia.games.poker.activator;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.games.poker.entity.TableConfigTemplate;

public class LobbyDomainSelectorImplTest {

	private LobbyDomainSelectorImpl selector = new LobbyDomainSelectorImpl();

	@Test
	public void testTexas10Seats() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getVariant()).thenReturn(TEXAS_HOLDEM);
		Mockito.when(templ.getSeats()).thenReturn(10);
		Assert.assertEquals("texas/cashgame/REAL_MONEY/10", selector.selectLobbyDomainFor(templ));
	}
	
	@Test
	public void testTexas6Seats() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getVariant()).thenReturn(TEXAS_HOLDEM);
		Mockito.when(templ.getSeats()).thenReturn(6);
		Assert.assertEquals("texas/cashgame/REAL_MONEY/6", selector.selectLobbyDomainFor(templ));
	}
	
	@Test
	public void testTelesina() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getVariant()).thenReturn(TELESINA);
		Mockito.when(templ.getSeats()).thenReturn(6);
		Assert.assertEquals("telesina/cashgame/REAL_MONEY/6", selector.selectLobbyDomainFor(templ));
	}
}
