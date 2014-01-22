package com.cubeia.poker.domainevents.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.events.client.EventClient;
import com.cubeia.events.event.GameEvent;
import com.cubeia.firebase.api.mtt.MTTState;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.games.poker.common.money.Money;

public class DomainEventsServiceImplTest {

	DomainEventsServiceImpl service;
	
	@Mock CashGamesBackendService cashGameBackend;
	
	@Mock PublicClientRegistryService clientRegistry;

	MttPlayer player;

	@Mock MttInstance instance;
	
	@Mock EventClient client;

	@Mock MTTState state;
	
	@Before
	public void setup() throws GetBalanceFailedException {
		MockitoAnnotations.initMocks(this);
		
		service = new DomainEventsServiceImpl();
		service.cashGameBackend = cashGameBackend;
		service.clientRegistry = clientRegistry;
		service.client = client;
		
		player = new MttPlayer(11, "TestPlayer");
		
		when(clientRegistry.getOperatorId(11)).thenReturn(2);
		when(instance.getState()).thenReturn(state);
		when(state.getId()).thenReturn(123);
		when(state.getName()).thenReturn("TestTourny");
		
		when(cashGameBackend.getAccountBalance(Mockito.anyInt(), Mockito.anyString())).thenReturn(new Money(new BigDecimal(300), new Currency("EUR", 2)));
		
	}
	
	@Test
	public void testSimpleCall() {
		BigDecimal payout = new BigDecimal(100.5);
		service.sendTournamentPayoutEvent(player, BigDecimal.ONE, payout, "EUR", 1, instance);
		verify(client).send(Mockito.any(GameEvent.class));
	}
	
	@Test
	public void testOperatorIdIsNull() {
		when(clientRegistry.getOperatorId(11)).thenReturn(null);
		
		BigDecimal payout = new BigDecimal(100.5);
		service.sendTournamentPayoutEvent(player, BigDecimal.ONE, payout, "EUR", 1, instance);
	}

}
