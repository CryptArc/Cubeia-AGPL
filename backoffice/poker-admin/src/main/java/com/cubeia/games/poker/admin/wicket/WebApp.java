package com.cubeia.games.poker.admin.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;


public class WebApp extends WebApplication {

	/** Constructor */
    public WebApp() {
    	
    }
    
    @Override
    protected void init()
    {
        // initialize Spring
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    }

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

   
}
