package com.cubeia.bonus.firebase.impl;

import com.cubeia.bonus.firebase.api.AchievementsService;
import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceServiceHandler;

public class GuiceHandler extends GuiceServiceHandler {

	@Override
    protected Configuration getConfiguration() {
        return new Configuration() {

            @Override
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(AchievementServiceImpl.class, AchievementsService.class);
            }
        };
    }
	
}
