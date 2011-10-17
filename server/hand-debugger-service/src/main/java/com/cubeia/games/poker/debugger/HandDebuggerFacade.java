package com.cubeia.games.poker.debugger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceService;
import com.cubeia.games.poker.debugger.guice.GuiceModule;
import com.cubeia.games.poker.services.HandDebuggerContract;
import com.google.inject.Module;

public class HandDebuggerFacade extends GuiceService implements HandDebuggerContract {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	/** Delegate that has been created with Guice */
	HandDebuggerContract delegate;

	@Override
	public void init(ServiceContext context) throws SystemException {
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		try {
			super.init(context);
			delegate = guice(HandDebuggerContract.class);
		} finally {
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}
	
	@Override
	public void start() {
		super.start();
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		try {
			guice(HandDebuggerContract.class).start();
		} finally {
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}
	
	
	@Override
	public Configuration getConfigurationHelp() {
		return new Configuration() {
			public ContractsConfig getServiceContract() {
				return new ContractsConfig(HandDebuggerImpl.class, HandDebuggerContract.class);
			}
		};
	}

	@Override
	protected void preInjectorCreation(List<Module> modules) {
		modules.add(new GuiceModule());
	}

	@Override
	public void addPublicAction(int tableId, GameAction action) {
		delegate.addPublicAction(tableId, action);
	}

	@Override
	public void addPrivateAction(int tableId, int playerId, GameAction action) {
		delegate.addPrivateAction(tableId, playerId, action);
	}

	@Override
	public void clearTable(int tableId) {
		delegate.clearTable(tableId);
	}

	@Override
	public void sendHttpLink(int tableId, int playerId) {}

	@Override
	public void setRouter(ServiceRouter router) {
		delegate.setRouter(router);
	}

	@Override
	public void onAction(ServiceAction e) {}
	
}
