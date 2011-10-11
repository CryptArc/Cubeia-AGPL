package com.cubeia.games.poker.debugger.guice;

import java.util.HashMap;
import java.util.Map;

import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.cache.TableStringCache;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		bind(new TypeLiteral<TableEventCache<String>>(){}).to(TableStringCache.class);
		
		// REST
		Map<String, String> params = new HashMap<String, String>();
		params.put("com.sun.jersey.config.property.packages", "com.cubeia.games.poker.debugger.web");
		serve("*").with(GuiceContainer.class, params);
	}

}
