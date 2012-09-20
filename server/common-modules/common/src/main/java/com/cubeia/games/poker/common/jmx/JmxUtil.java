package com.cubeia.games.poker.common.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class JmxUtil {

    private final MBeanServer mbs;
	
	public JmxUtil() { 
		this(ManagementFactory.getPlatformMBeanServer());
	}
	
	public JmxUtil(MBeanServer server) {
		this.mbs = server;
	}

	public void mountBean(String name, Object bean) {
		try {
            ObjectName monitorName = new ObjectName(name);
            if(!mbs.isRegistered(monitorName)) {
                mbs.registerMBean(bean, monitorName);           	
            }
        } catch (Exception e) {
            Logger.getLogger(JmxUtil.class).error("failed to bind poker activator to JMX", e);
        }
	}

	public void unmountBean(String name) {
		try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName monitorName = new ObjectName(name);
            if (mbs.isRegistered(monitorName)) {
                mbs.unregisterMBean(monitorName);
            }
        } catch (Exception e) {
        	Logger.getLogger(JmxUtil.class).error("failed to unbind poker activator to JMX", e);
        }
	}
}
