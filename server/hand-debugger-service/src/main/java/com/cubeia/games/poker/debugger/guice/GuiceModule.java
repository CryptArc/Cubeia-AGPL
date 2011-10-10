package com.cubeia.games.poker.debugger.guice;

import java.util.HashMap;
import java.util.Map;

import com.cubeia.games.poker.debugger.web.EchoResource;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceModule extends ServletModule {

	@Override
	protected void configureServlets() {
		System.out.println(" >>>>>>>>>>>>>>>>> CONFIGURE SERVLETS!!! <<<<<<<<<<<<<<<<<<<<<");

		/* specify the REST resources package */
		// bind(EchoResource.class);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("com.sun.jersey.config.property.packages", "com.cubeia.games.poker.debugger.web");


		//		bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
		//	    bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

//		bind(MessageBodyReader.class).to(XXXMyAwesomeWriter.class);
//		bind(MessageBodyWriter.class).to(XXXMyAwesomeWriter.class);

		//filter("/*").through(GuiceContainer.class, params);
		serve("*").with(GuiceContainer.class, params);


	}

}
