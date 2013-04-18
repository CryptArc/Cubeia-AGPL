package com.cubeia.poker.domainevents.api;

import com.cubeia.events.event.GameEvent;
import com.cubeia.firebase.api.service.Contract;

public interface DomainEventsService extends Contract { 
	
	public void sendEvent(GameEvent event);
	
}