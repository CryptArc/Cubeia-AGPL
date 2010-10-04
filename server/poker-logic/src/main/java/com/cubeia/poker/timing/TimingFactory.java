package com.cubeia.poker.timing;

import com.cubeia.poker.timing.impl.TimingRegistryImpl;

/**
 * Use this factory class to access the server default registry.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class TimingFactory {
	
	private static TimingRegistry registry = new TimingRegistryImpl();

	public static TimingRegistry getRegistry() {
		return registry;
	}
	
}
