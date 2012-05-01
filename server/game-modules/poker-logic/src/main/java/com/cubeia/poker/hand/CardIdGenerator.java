package com.cubeia.poker.hand;

import java.util.List;

/**
 * Generator of cards with assigned id:s.
 *
 * @author w
 */
public interface CardIdGenerator {

    /**
     * Copy the given list of cards and assigned id:s to the new cards.
     * The order of the list is not altered.
     *
     * @param cards cards to copy
     * @return list of cards with id:s assigned
     */
    List<Card> copyAndAssignIds(List<Card> cards);

}