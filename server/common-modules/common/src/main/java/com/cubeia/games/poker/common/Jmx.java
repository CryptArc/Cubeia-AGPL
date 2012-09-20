package com.cubeia.games.poker.common;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class Jmx {

    private final MBeanServer mbs;
	
	public Jmx() { 
		this(ManagementFactory.getPlatformMBeanServer());
	}
	
	public Jmx(MBeanServer server) {
		this.mbs = server;
	}

	public void mountBean(String name, Object bean) {
		try {
            ObjectName monitorName = new ObjectName(name);
            if(!mbs.isRegistered(monitorName)) {
                mbs.registerMBean(bean, monitorName);           	
            }
        } catch (Exception e) {
            Logger.getLogger(Jmx.class).error("failed to bind poker activator to JMX", e);
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
        	Logger.getLogger(Jmx.class).error("failed to unbind poker activator to JMX", e);
        }
	}
}
