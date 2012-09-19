package com.cubeia.game.poker.config.api;

import com.cubeia.firebase.api.service.Contract;

public interface PokerConfigurationService extends Contract { 
	
	public PokerTournamentActivatorConfig getTournamentActivatorConfig();
	
}