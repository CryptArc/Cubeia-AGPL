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

package com.cubeia.games.poker.persistence;

import java.util.HashSet;

import junit.framework.TestCase;

import com.cubeia.games.poker.persistence.history.HandHistoryDAO;
import com.cubeia.games.poker.persistence.history.model.EventType;
import com.cubeia.games.poker.persistence.history.model.PlayedHand;
import com.cubeia.games.poker.persistence.history.model.PlayedHandEvent;
import com.cubeia.games.poker.persistence.mock.MockServiceRegistry;

public class HandHistoryDAOTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void XtestInsert() {
        HandHistoryDAO dao = new HandHistoryDAO(new MockServiceRegistry());
        PlayedHand hand = new PlayedHand();
        hand.setTableId(666);
        
        hand.setEvents(new HashSet<PlayedHandEvent>());
        
        PlayedHandEvent event1 = new PlayedHandEvent();
        event1.setPlayerId(1);
        event1.setType(EventType.SMALL_BLIND);
        event1.setHand(hand);
        
        PlayedHandEvent event2 = new PlayedHandEvent();
        event2.setPlayerId(2);
        event2.setType(EventType.BIG_BLIND);
        event2.setHand(hand);
        
        hand.getEvents().add(event1);
        hand.getEvents().add(event2);
        
        dao.persist(hand);
    }
    
    public void testDummy() {
    }
}
