package com.cubeia.games.poker.debugger;

import java.util.List;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceService;
import com.cubeia.games.poker.debugger.guice.GuiceModule;
import com.google.inject.Module;

public class HandDebuggerFacade extends GuiceService implements HandDebuggerContract {
	
	@Override
	public void init(ServiceContext context) throws SystemException {
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		try {
			super.init(context);
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
			
			System.out.println(" ******************** START *******************");
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
	public void hej() {
		guice(HandDebuggerContract.class).hej();
	}

	
}
