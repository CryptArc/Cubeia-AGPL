package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.List;

/**
 * Id generator that assigns the list index as id:s to the cards.
 *
 * @author w
 */
public class IndexCardIdGenerator implements CardIdGenerator {

    public List<Card> copyAndAssignIds(List<Card> cards) {
        ArrayList<Card> newCards = new ArrayList<Card>();
        int id = 0;
        for (Card card : cards) {
            newCards.add(card.makeCopyWithId(id++));
        }
        return newCards;
    }

}
