package com.cubeia.games.poker.logic;

import java.util.UUID;

import junit.framework.TestCase;

public class TimeoutCacheTest extends TestCase {

    
    public void testBasicUsage() throws Exception {
        TimeoutCache cache = TimeoutCache.getInstance();
        UUID u1 = UUID.randomUUID();
        cache.addTimeout(1, 2, u1);
        assertEquals(1, cache.actions.size());
        
        cache.removeTimeout(1, 2, null);
        assertEquals(0, cache.actions.size());
    }
    
}
