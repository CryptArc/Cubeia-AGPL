package com.cubeia.games.poker.persistence.mock;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.firebase.api.service.ServiceInfo;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.persistence.PublicPersistenceService;

public class MockServiceRegistry implements ServiceRegistry {

    @SuppressWarnings("unchecked")
    public <T extends Contract> T getServiceInstance(Class<T> serviceClass) {
        if (serviceClass == PublicPersistenceService.class) {
            return (T) new MockPersistenceService();
        } else {
            return null;
        }
    }

    public ServiceInfo getServiceInfo(String arg0) {
        return null;
    }

    public <T extends Contract> ServiceInfo getServiceInfo(Class<T> arg0, String arg1) {
        return null;
    }

    public Contract getServiceInstance(String arg0) {
        return null;
    }

    public <T extends Contract> T getServiceInstance(Class<T> arg0, String arg1) {
        return null;
    }

}
