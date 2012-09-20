package com.cubeia.games.poker.debugger;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceServiceHandler;

public class HandDebuggerServiceHandler extends GuiceServiceHandler {

	@Override
    public Configuration getConfiguration() {
        return new Configuration() {
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(HandDebuggerImpl.class, HandDebuggerContract.class);
            }
        };
    }
}
