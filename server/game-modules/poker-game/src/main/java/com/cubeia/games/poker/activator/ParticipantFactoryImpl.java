package com.cubeia.games.poker.activator;

import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.poker.rng.RNGProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ParticipantFactoryImpl implements ParticipantFactory {

    @Inject
    private RNGProvider rngProvider;
    
    @Inject
    private PokerStateCreator stateCreator;
    
    @Service
    private CashGamesBackendContract backend;
    
    @Inject
    private LobbyDomainSelector domainSelector;
	
	@Override 
	public PokerParticipant createParticipantFor(TableConfigTemplate template) {
		return new PokerParticipant(
					template, 
					template == null ? null : domainSelector.selectLobbyDomainFor(template), 
					stateCreator, 
					rngProvider, 
					backend);
	}
}
