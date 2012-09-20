package com.cubeia.poker.handhistory.impl;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceServiceHandler;
import com.cubeia.poker.handhistory.api.HandHistoryCollectorService;

public class CollectorServiceHandler extends GuiceServiceHandler {

	@Override
	protected Configuration getConfiguration() {
		return new Configuration() {

            @Override
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(CollectorServiceImpl.class, HandHistoryCollectorService.class);
            }
        };
	}
}
