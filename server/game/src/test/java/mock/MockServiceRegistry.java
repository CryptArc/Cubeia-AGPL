package mock;

import java.util.HashMap;
import java.util.Map;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.firebase.api.service.ServiceInfo;
import com.cubeia.firebase.api.service.ServiceRegistry;

public class MockServiceRegistry implements ServiceRegistry {

	@SuppressWarnings("unchecked")
	private Map<Class, Object> serviceMap = new HashMap<Class, Object>();
	
	public <T extends Contract> void addService(Class<T> contract, T service) {
		serviceMap.put(contract, service);
	}
	
    @SuppressWarnings("unchecked")
    public <T extends Contract> T getServiceInstance(Class<T> serviceClass) {
    	return (T) serviceMap.get(serviceClass);
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
