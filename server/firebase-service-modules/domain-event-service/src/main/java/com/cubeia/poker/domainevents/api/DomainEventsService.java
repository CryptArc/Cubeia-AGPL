package com.cubeia.poker.domainevents.api;

import com.cubeia.events.event.GameEvent;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.service.Contract;
import com.cubeia.games.poker.common.money.Money;

import java.math.BigDecimal;

public interface DomainEventsService extends Contract { 
	
	public void sendEvent(GameEvent event);

	public void sendTournamentPayoutEvent(int playerId, BigDecimal payout, String currencyCode, int position, MttInstance instance);
	
	public void sendEndPlayerSessionEvent(int playerId, Money accountBalance);
	
}