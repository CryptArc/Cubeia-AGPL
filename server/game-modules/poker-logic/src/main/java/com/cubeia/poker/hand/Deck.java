package com.cubeia.poker.hand;

import java.io.Serializable;
import java.util.List;

/**
 * A deck of cards. A deck is stateful and remembers it's shuffled order of cards
 * as well as a which cards has been dealt.
 *
 * @author w
 */
public interface Deck extends Serializable {

    /**
     * Deal a card. Picks the next card in the deck and removes it.
     *
     * @return the dealt card, null if deck is empty
     */
    public Card deal();

    /**
     * Returns true if all cards has been dealt.
     *
     * @return true if deck is empty
     */
    boolean isEmpty();

    /**
     * Returns a list (copy) of all cards in the deck including dealt cards.
     * The returned list won't reflect the shuffling of the deck.
     *
     * @return list of cards
     */
    List<Card> getAllCards();

}
