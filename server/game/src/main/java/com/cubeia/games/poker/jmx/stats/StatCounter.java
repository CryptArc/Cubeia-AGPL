package com.cubeia.games.poker.jmx.stats;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Count hits over a given time period.
 * Getting the count has performance impact so it is important not to call this
 * method often and/or concurrently. (It is typically designed for using with
 * a JMX interface which polls about once per second).
 * 
 * This class is not targeted towards highly concurrent data (e.g. > 100 hits per second).
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class StatCounter {
    
    private ConcurrentLinkedQueue<Hit> cache = new ConcurrentLinkedQueue<Hit>(); 
    
    private final long window;
    
    
    /**
     * Add a hit and remove tail object if too old.
     * The removal of the head object is only a safety precaution if no-one is polling
     * the stats object. (Polling cleans up the cache of all old objects).
     */
    public void register() {
        cache.add(new Hit());
        synchronized (cache) {
            if (System.currentTimeMillis() > cache.peek().time + window) {
                cache.remove();
            }
        }
    }
    
    public int getCurrent() {
        cleanAllOldObjects();
        return cache.size();
    }
    
    
	private void cleanAllOldObjects() {
        synchronized (cache) {
            while (cache.size() > 0) {
                if (System.currentTimeMillis() > cache.peek().time + window) {
                    cache.remove();
                } else {
                    break;
                }
            }
        }
        
    }

    public StatCounter(long millis) {
        this.window = millis;
	}
	
    
    private class Hit {
        public long time = System.currentTimeMillis();    
    }
	
}
