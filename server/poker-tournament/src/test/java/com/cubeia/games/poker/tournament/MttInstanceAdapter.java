package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.mtt.MTTState;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.support.LobbyAttributeAccessorAdapter;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.tables.MttTableCreator;
import com.cubeia.firebase.api.scheduler.Scheduler;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.mttplayerreg.TournamentPlayerRegistry;

public class MttInstanceAdapter implements MttInstance {

	TournamentPlayerRegistry tournamentPlayerRegistry = new TournamentPlayerRegistryAdapter();
	
	LobbyAttributeAccessor lobbyAttributeAccessor = new LobbyAttributeAccessorAdapter();

	private MTTState state;

	private Scheduler<MttAction> scheduler; 
	
	public LobbyAttributeAccessor getLobbyAccessor() {
		return lobbyAttributeAccessor;
	}

	public Scheduler<MttAction> getScheduler() {
		return scheduler;
	}
	
	public MttNotifier getMttNotifier() {
		return null;
	}
	
	public MttTableCreator getTableCreator() {
		return null;
	}
	
	public void setScheduler(Scheduler<MttAction> scheduler) {
		this.scheduler = scheduler;
	}

	public ServiceRegistry getServiceRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	public MTTState getState() {
		return state;
	}

	public TournamentPlayerRegistry getSystemPlayerRegistry() {
		return tournamentPlayerRegistry;
	}

	public LobbyAttributeAccessor getTableLobbyAccessor(int tableId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setState(MTTStateSupport state) {
		this.state = state;
	}

}
