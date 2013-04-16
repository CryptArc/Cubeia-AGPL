package com.cubeia.bonus.firebase.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cubeia.bonus.firebase.api.AchievementsService;
import com.cubeia.bonus.firebase.api.BonusEventWrapper;
import com.cubeia.events.client.EventClient;
import com.cubeia.events.client.EventListener;
import com.cubeia.events.event.GameEvent;
import com.cubeia.events.event.achievement.BonusEvent;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.firebase.api.service.router.RouterService;
import com.google.inject.Singleton;

@Singleton
public class AchievementServiceImpl implements Service, AchievementsService, EventListener {
	
	Logger log = Logger.getLogger(getClass());
	
	EventClient client;
	
	@com.cubeia.firebase.guice.inject.Service
	RouterService router;
	
	@com.cubeia.firebase.guice.inject.Service
	PublicClientRegistryService clientRegistry;
	
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
	
}