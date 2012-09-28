package com.cubeia.games.poker.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * This hack is used to circumvent a flawed design in the poker adapter
 * layer, namely: Since adapters adapt each other they cannot depend on each
 * others calculations. In particular, if, say, the hand history needs information
 * about a transaction at the end of the hand, it cannot get it. There is no way for this
 * for this information to be passed in a meaningful manner. 
 * 
 * @author Lars J. Nilsson
 */
public class UberAdapterHack {

	private static final ThreadLocal<Map<String, String>> CALL_ATTRIBUTES = new ThreadLocal<Map<String,String>>();
	
	public static void set(String key, String value) {
		CALL_ATTRIBUTES.get().put(key, value);
	}
	
	public static String get(String key) {
		return CALL_ATTRIBUTES.get().get(key);
	}
	
	public static void prepare() { 
		CALL_ATTRIBUTES.set(new HashMap<String, String>(10));
	}
	
	public static void clear() {
		CALL_ATTRIBUTES.remove();
	}
}
