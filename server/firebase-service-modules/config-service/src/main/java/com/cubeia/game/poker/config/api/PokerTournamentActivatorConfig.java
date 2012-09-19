package com.cubeia.game.poker.config.api;

import static com.cubeia.firebase.api.server.conf.Inheritance.ALLOW;

import com.cubeia.firebase.api.server.conf.Configurable;
import com.cubeia.firebase.api.server.conf.Configurated;
import com.cubeia.firebase.api.server.conf.Property;

@Configurated(inheritance=ALLOW,namespace="com.cubeia.game.poker.mtt")
public interface PokerTournamentActivatorConfig extends Configurable {

	@Property(defaultValue="false")
	public boolean useMockIntegrations();
	
}
