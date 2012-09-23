package com.cubeia.game.poker.config.api;

import static com.cubeia.firebase.api.server.conf.Inheritance.ALLOW;

import com.cubeia.firebase.api.server.conf.Configurable;
import com.cubeia.firebase.api.server.conf.Configurated;
import com.cubeia.firebase.api.server.conf.Property;

@Configurated(inheritance=ALLOW,namespace="com.cubeia.game.poker")
public interface PokerSystemConfig extends Configurable {

	@Property(defaultValue="EUR")
	public String getSystemCurrencyCode();
	
	@Property(defaultValue="2")
	public int getSystemCurrencyFractions();
	
}
