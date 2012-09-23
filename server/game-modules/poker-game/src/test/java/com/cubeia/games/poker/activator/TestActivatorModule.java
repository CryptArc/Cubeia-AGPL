package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.guice.inject.FirebaseModule;
import com.cubeia.poker.rng.RNGProvider;

class TestActivatorModule extends FirebaseModule {
	
	private ServiceRegistry registry;

	public TestActivatorModule() {
		this(new TestServiceRegistry());
	}
	
	public TestActivatorModule(ServiceRegistry reg) {
		super(reg);
		this.registry = reg; 
	}
	
	@Override
	protected void configure() {
		bind(ServiceRegistry.class).toInstance(registry);
		bind(RNGProvider.class).to(DummyRNGProvider.class); 
        bind(ParticipantFactory.class).to(ParticipantFactoryImpl.class);
        bind(LobbyDomainSelector.class).to(LobbyDomainSelectorImpl.class);
        bind(PokerStateCreator.class).to(InjectorPokerStateCreator.class);
        super.configure(); 
	} 
}