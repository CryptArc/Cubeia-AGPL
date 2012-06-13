package com.cubeia.games.poker.debugger.cache;

import org.junit.Test;

import static org.testng.Assert.assertEquals;

public class TableEventCacheTest {

    @Test
    public void testClearTableShouldNotThrowNullPointer() {
        TableEventCache cache = new TableEventCache();
        cache.clearTable(4);
        assertEquals(null, cache.getPreviousEvents(4));
    }
}
