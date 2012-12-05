package com.cubeia.game.poker.challenge.impl;

import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceServiceHandler;
import com.cubeia.game.poker.challenge.api.ChallengeManager;
import com.cubeia.game.poker.challenge.api.ChallengeService;
import com.google.inject.Binder;
import com.google.inject.Module;

import java.util.List;

public class ChallengeServiceHandler extends GuiceServiceHandler {
    @Override
    protected Configuration getConfiguration() {
        return new Configuration() {
            @Override
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(ChallengeServiceImpl.class,ChallengeService.class);
            }
        };
    }
    @Override
    public void preInjectorCreation(ServiceContext context, List<Module> modules) {
        modules.add(new ChallengeModule());
    }
}
