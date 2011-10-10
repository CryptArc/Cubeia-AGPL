package com.cubeia.games.poker.debugger;

import com.cubeia.games.poker.debugger.guice.GuiceConfig;
import com.cubeia.games.poker.debugger.server.WebServer;
import com.google.inject.Inject;

public class HandDebuggerImpl implements HandDebuggerContract {
	
	@Inject GuiceConfig guice;
	
	@Inject WebServer server;
	
	public void start() {
		server.start();
	}

	@Override
	public void hej() {
		// TODO Auto-generated method stub
		
	}
	
}
