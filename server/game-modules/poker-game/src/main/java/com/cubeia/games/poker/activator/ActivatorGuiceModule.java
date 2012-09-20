/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.activator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.activator.TableFactory;
import com.cubeia.firebase.api.routing.ActivatorRouter;
import com.cubeia.firebase.guice.inject.FirebaseModule;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.poker.rng.RNGProvider;
import com.google.inject.Provider;
import com.google.inject.name.Names;

public class ActivatorGuiceModule extends FirebaseModule {

    private final ActivatorContext context;

	public ActivatorGuiceModule(ActivatorContext context) {
        super(context.getServices());
		this.context = context;
    } 

    @Override
    protected void configure() {
        super.configure();
        bind(RNGProvider.class).to(DummyRNGProvider.class); // FIX FOR REAL IMPLEMENTATION
        bind(ScheduledExecutorService.class).annotatedWith(Names.named("activatorThreads")).toInstance(Executors.newSingleThreadScheduledExecutor());
        bind(TableFactory.class).toInstance(context.getTableFactory());
        bind(ActivatorContext.class).toInstance(context);
        bind(ParticipantFactory.class).to(ParticipantFactoryImpl.class);
        bind(LobbyDomainSelector.class).to(LobbyDomainSelectorImpl.class);
        bind(PokerStateCreator.class).to(InjectorPokerStateCreator.class);
        bind(TableActionHandler.class).to(TableActionHandlerImpl.class);
        bind(ActivatorRouter.class).toInstance(context.getActivatorRouter());
        bind(ActivatorTableManager.class).to(ActivatorTableManagerImpl.class);
        bind(LobbyTableInspector.class).to(LobbyTableInspectorImpl.class); 
        bind(MttTableCreationHandler.class).to(MttTableCreationHandlerImpl.class);
        bind(Long.class).annotatedWith(Names.named("activatorInterval")).toProvider(new Provider<Long>() {
        	
        	@Service
        	private PokerConfigurationService serv;
        	
        	@Override
        	public Long get() {
        		return serv.getActivatorConfig().getActivatorInterval();
        	}
		});
    }
}
