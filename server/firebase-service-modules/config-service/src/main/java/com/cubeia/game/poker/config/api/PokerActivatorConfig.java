package com.cubeia.game.poker.config.api;

import static com.cubeia.firebase.api.server.conf.Inheritance.ALLOW;

import com.cubeia.firebase.api.server.conf.Configurable;
import com.cubeia.firebase.api.server.conf.Configurated;
import com.cubeia.firebase.api.server.conf.Property;

@Configurated(inheritance=ALLOW,namespace="com.cubeia.game.poker")
public interface PokerActivatorConfig extends Configurable {

	@Property(defaultValue="false")
	public boolean useMockIntegrations();
	
	@Property(defaultValue="60000", property="default-table-ttl")
	public long getDefaultTableTTL();

	@Property(defaultValue="30000")
	public long getActivatorInterval();

}
