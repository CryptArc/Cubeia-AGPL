package com.cubeia.poker.handhistory.impl;

import java.util.List;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceService;
import com.cubeia.games.poker.handhistory.HandHistoryCollectorService;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;

public class CollectorServiceFacade extends GuiceService implements HandHistoryCollectorService {

	@Override
	public Configuration getConfigurationHelp() {
		return new Configuration() {
			
			@Override
			public ContractsConfig getServiceContract() {
				return new ContractsConfig(CollectorServiceImpl.class, HandHistoryCollectorService.class);
			}
		};
	}
	
	
	@Override
	public void reportEvent(int tableId, HandHistoryEvent event) {
		g().reportEvent(tableId, event);
	}
	
	@Override
	public void startHand(HandIdentification id, List<Player> seats) {
		g().startHand(id, seats);
	}
	
	@Override
	public void stopHand(int tableId) {
		g().stopHand(tableId);
	}
	
	@Override
	public void cancelHand(int tableId) {
		g().cancelHand(tableId);
	}
	
	@Override
	public void reportDeckInfo(int tableId, DeckInfo deckInfo) {
		g().reportDeckInfo(tableId, deckInfo);
	}
	
	@Override
	public void reportResults(int tableId, long totalRake, Results res) {
		g().reportResults(tableId, totalRake, res);
	}
	
	
	// --- PRIVATE METHODS --- //
	
	private HandHistoryCollectorService g() {
		return guice(HandHistoryCollectorService.class);
	}
}
