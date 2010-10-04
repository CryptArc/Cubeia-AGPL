package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.mtt.activator.ActivatorContext;
import com.cubeia.firebase.api.routing.ActivatorRouter;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.util.ConfigSource;
import com.cubeia.firebase.api.util.ConfigSourceListener;

public class MockMttActivatorContext implements ActivatorContext {

    public ConfigSource getConfigSource() {
        return null;
    }
    
    public ActivatorRouter getActivatorRouter() {
    	// TODO Auto-generated method stub
    	return null;
    }

    public int getMttId() {
        return 0;
    }

    public ServiceRegistry getServices() {
        return null;
    }

    public void setConfigSourceListener(ConfigSourceListener arg0) {

    }

}
