package com.cubeia.games.poker.debugger.guice;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		// REST
		Map<String, String> params = new HashMap<String, String>();
		params.put("com.sun.jersey.config.property.packages", "com.cubeia.games.poker.debugger.web");
		serve("*").with(GuiceContainer.class, params);
	}

}
