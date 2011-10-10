package com.cubeia.games.poker.debugger.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfig extends GuiceServletContextListener {
	
    @Inject Injector injector;

	@Override
    protected Injector getInjector() {
		// injector = Guice.createInjector(new GuiceModule());
        return injector;
    }
    
    public Injector getInjectorPublic() {
    	return injector;
    }
}
