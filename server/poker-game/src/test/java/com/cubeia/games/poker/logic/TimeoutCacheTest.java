/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.logic;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

public class TimeoutCacheTest {

    
    @Test
    public void testBasicUsage() throws Exception {
        TimeoutCache cache = TimeoutCache.getInstance();
        UUID u1 = UUID.randomUUID();
        cache.addTimeout(1, 2, u1);
        assertEquals(1, cache.actions.size());
        
        cache.removeTimeout(1, 2, null);
        assertEquals(0, cache.actions.size());
    }
    
}
