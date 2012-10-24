package com.cubeia.game.poker.config.api;

import static com.cubeia.firebase.api.server.conf.Inheritance.ALLOW;

import java.net.URL;

import com.cubeia.firebase.api.server.conf.Configurable;
import com.cubeia.firebase.api.server.conf.Configurated;

@Configurated(inheritance = ALLOW, namespace = "com.cubeia.game.poker.handhistory")
public interface HandHistoryConfig extends Configurable {

	public URL getJsonIndexUrl();
	
}
