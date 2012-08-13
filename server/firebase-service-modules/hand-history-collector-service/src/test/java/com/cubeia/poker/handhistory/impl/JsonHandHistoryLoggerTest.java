package com.cubeia.poker.handhistory.impl;

import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.PlayerAction;
import org.junit.Test;

public class JsonHandHistoryLoggerTest {

    @Test
    public void test() {
        HistoricHand hand = new HistoricHand();
        HandHistoryEvent event = new PlayerAction(4, PlayerAction.Type.BET);
        hand.getEvents().add(event);
        System.out.println(new JsonHandHistoryLogger().convertToJson(hand));
    }
}
