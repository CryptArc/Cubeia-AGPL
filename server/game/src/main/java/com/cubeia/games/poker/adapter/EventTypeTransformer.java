package com.cubeia.games.poker.adapter;

import com.cubeia.games.poker.persistence.history.model.EventType;
import com.cubeia.poker.action.PokerActionType;

public class EventTypeTransformer {
    
    public static EventType transform(PokerActionType action) {
        switch (action) {
            case SMALL_BLIND:
                return EventType.SMALL_BLIND;
            case BIG_BLIND:
                return EventType.BIG_BLIND;
            case BET:
                return EventType.BET;
            case CALL:
                return EventType.CALL;
            case CHECK:
                return EventType.CHECK;
            case DECLINE_ENTRY_BET:
                return EventType.DENY_ENTRY_BET;
            case FOLD:
                return EventType.FOLD;
            case RAISE:
                return EventType.RAISE;
            
            default:
                return null;
        }
    }
    
}
