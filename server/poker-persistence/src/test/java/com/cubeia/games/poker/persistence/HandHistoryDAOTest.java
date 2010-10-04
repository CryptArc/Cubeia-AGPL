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
