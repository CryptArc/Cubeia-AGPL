package com.cubeia.poker.domainevents.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.events.client.EventClient;
import com.cubeia.events.client.EventListener;
import com.cubeia.events.event.GameEvent;
import com.cubeia.events.event.GameEventType;
import com.cubeia.events.event.achievement.BonusEvent;
import com.cubeia.events.event.poker.PokerAttributes;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.firebase.api.service.router.RouterService;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.poker.domainevents.api.BonusEventWrapper;
import com.cubeia.poker.domainevents.api.DomainEventsService;
import com.google.inject.Singleton;

@Singleton
public class DomainEventsServiceImpl implements Service, DomainEventsService, EventListener {
	
	Logger log = Logger.getLogger(getClass());
	
	EventClient client;
	
	@com.cubeia.firebase.guice.inject.Service
	RouterService router;
	
	@com.cubeia.firebase.guice.inject.Service
	PublicClientRegistryService clientRegistry;
	
	@com.cubeia.firebase.guice.inject.Service
    CashGamesBackendService cashGameBackend;
	
	ObjectMapper mapper = new ObjectMapper();
	
	public void init(ServiceContext con) throws SystemException {}

	public void start() {
		client = new EventClient(this);
	}

	public void stop() {}
	
	public void destroy() {}

	@Override
	public void sendEvent(GameEvent event) {
		client.send(event);
	}

	/**
	 * A bonus event has been triggered by the achievement service
	 */
	@Override
	public void onBonusEvent(BonusEvent event) {
		try {
			int playerId = Integer.parseInt(event.player);
			
			Map<Integer, Integer> seatedTables = clientRegistry.getSeatedTables(playerId);
			for (int tableId : seatedTables.keySet()) {
				String json = mapper.writeValueAsString(event);
				BonusEventWrapper wrapper = new BonusEventWrapper(playerId, json);
				wrapper.broadcast = event.broadcast;
				
				log.debug("Bonus Event send JSON: "+json);
				
				GameObjectAction action = new GameObjectAction(tableId);
				action.setAttachment(wrapper);
				router.getRouter().dispatchToGame(tableId, action );
			}
			
		} catch (Exception e) {
			log.error("Failed to handle bonus event["+event+"]", e);
		}
	}

	@Override
	public void onEvent(GameEvent event) {}

	@Override
	public void sendTournamentPayoutEvent(int playerId, int payoutInCents, String currencyCode, int position, MttInstance instance) {
		int tournamentId = instance.getState().getId();
		String tournamentName = instance.getState().getName();
		
		int operatorId = clientRegistry.getOperatorId(playerId);
		String screenname = clientRegistry.getScreenname(playerId);
		
		Money accountBalance = new Money(-1, currencyCode, 2); 
		try {
			accountBalance = cashGameBackend.getAccountBalance(playerId, currencyCode);
		} catch (GetBalanceFailedException e) {
			log.error("Failed to get balance for player["+playerId+"] and currency["+currencyCode+"]", e);
		}
		
		GameEvent event = new GameEvent();
		event.game = PokerAttributes.poker.name();
		event.player = playerId+"";
		event.type = GameEventType.tournamentPayout.name();
		event.operator = operatorId+"";
		
		event.attributes.put(PokerAttributes.win.name(), payoutInCents+"");
		event.attributes.put(PokerAttributes.tournamentId.name(), tournamentId+"");
		event.attributes.put(PokerAttributes.tournamentName.name(), tournamentName);
		event.attributes.put(PokerAttributes.tournamentPosition.name(), position+"");
		
		event.attributes.put(PokerAttributes.accountBalance.name(), accountBalance.getAmount()+"");
		event.attributes.put(PokerAttributes.accountCurrency.name(), currencyCode);  
		
		event.attributes.put(PokerAttributes.screenname.name(), screenname);
		
		log.debug("Send Player Session ended event: "+event);
		sendEvent(event);
	}
	
	public void sendEndPlayerSessionEvent(int playerId, Money accountBalance) {
		log.debug("Event Player Session ended. Player["+playerId+"], Balance["+accountBalance+"]");
		
		String screenname = clientRegistry.getScreenname(playerId);
		int operatorId = clientRegistry.getOperatorId(playerId);
		
		GameEvent event = new GameEvent();
		event.game = PokerAttributes.poker.name();
		event.player = playerId+"";
		event.type = GameEventType.leaveTable.name();
		event.operator = operatorId+"";
		
		event.attributes.put(PokerAttributes.accountBalance.name(), accountBalance.getAmount()+"");
		event.attributes.put(PokerAttributes.accountCurrency.name(), accountBalance.getCurrencyCode());
		event.attributes.put(PokerAttributes.screenname.name(), screenname);
		
		log.debug("Send Player Session ended event: "+event);
		sendEvent(event);
	}
	
}