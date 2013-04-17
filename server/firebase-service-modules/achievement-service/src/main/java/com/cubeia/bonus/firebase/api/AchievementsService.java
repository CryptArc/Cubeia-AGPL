package com.cubeia.bonus.firebase.api;

import com.cubeia.events.event.GameEvent;
import com.cubeia.firebase.api.service.Contract;

public interface AchievementsService extends Contract { 
	
	public void sendEvent(GameEvent event);
	
}