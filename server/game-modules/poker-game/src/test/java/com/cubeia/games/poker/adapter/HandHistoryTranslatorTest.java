package com.cubeia.games.poker.adapter;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.handhistory.api.GameCard;
import com.cubeia.poker.handhistory.api.PlayerAction;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HandHistoryTranslatorTest {

    @Test
    public void checkRankMatch() {
        for (Rank r : Rank.values()) {
            GameCard.Rank gcr = GameCard.Rank.values()[r.ordinal()];
            assertEquals(r.name(), gcr.name());
        }
    }

    @Test
    public void checkSuitMatch() {
        for (Suit s : Suit.values()) {
            GameCard.Suit gcs = GameCard.Suit.values()[s.ordinal()];
            assertEquals(s.name(), gcs.name());
        }
    }

    @Test
    public void checkActionTypeMatch() {
        for (PokerActionType t : PokerActionType.values()) {
            PlayerAction.Type pat = PlayerAction.Type.values()[t.ordinal()];
            assertEquals(t.name(), pat.name());
        }
    }
}
