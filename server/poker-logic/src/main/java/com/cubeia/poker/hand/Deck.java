package com.cubeia.poker.hand;

/**
 * A deck of cards. A deck is stateful and remembers it's shuffled order of cards
 * as well as a which cards has been delt. 
 * @author w
 *
 */
public interface Deck {
    
    /**
     * Deal a card. Picks the next card in the deck and removes it.
     * @return the dealt card, null if deck is empty
     */
    public Card deal();

    /**
     * Returns true if all cards has been dealt.
     * @return true if deck is empty
     */
    boolean isEmpty();
    
}
