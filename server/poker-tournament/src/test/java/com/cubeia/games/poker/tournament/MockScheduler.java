package com.cubeia.games.poker.tournament;

import java.util.UUID;

import com.cubeia.firebase.api.action.Action;
import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.scheduler.Scheduler;
import com.cubeia.firebase.api.util.UnmodifiableSet;

public class MockScheduler implements Scheduler<MttAction> {

	public void cancelScheduledAction(UUID id) {
		// TODO Auto-generated method stub

	}
	
	public void cancelAllScheduledActions() {
		// TODO Auto-generated method stub
		
	}

	public UnmodifiableSet<UUID> getAllScheduledGameActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Action getScheduledGameAction(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getScheduledGameActionDelay(UUID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasScheduledGameAction(UUID id) {
		// TODO Auto-generated method stub
		return false;
	}

	public UUID scheduleAction(MttAction action, long delay) {
		// TODO Auto-generated method stub
		return null;
	}

}
