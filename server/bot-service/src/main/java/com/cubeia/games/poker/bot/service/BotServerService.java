package com.cubeia.games.poker.bot.service;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.poker.pokerbots.server.BotServerDaemon;

public class BotServerService implements BotServerContract, Service {

	@Override
	public void init(ServiceContext con) throws SystemException {}

	@Override
	public void destroy() {}

	@Override
	public void start() {
		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			BotServerDaemon.startLocalHTTP(19090, 1);
		} finally {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}
	}

	@Override
	public void stop() {}

}
