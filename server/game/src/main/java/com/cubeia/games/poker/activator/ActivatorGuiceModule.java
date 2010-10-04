package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.guice.inject.FirebaseModule;

public class ActivatorGuiceModule extends FirebaseModule {
	
    public ActivatorGuiceModule(ActivatorContext context) {
            super(context.getServices());
    }

    @Override
    protected void configure() {
            super.configure();
    }


}
