package com.cubeia.poker.hand;

import java.util.Comparator;

/**
 * Comparator for cards that compares the rank and suit.
 * The order of the cards are compatible with the order of the
 * {@link Suit} and {@link Rank} enums.
 * An Ace is greater than a King etc.
 * The card id is not compared.
 *
 * @author w
 */
public class CardComparator implements Comparator<Card> {
    @Override
    public int compare(Card c1, Card c2) {
        int comp = c1.getRank().ordinal() * 1000 - c2.getRank().ordinal() * 1000;
        comp += c1.getSuit().ordinal() - c2.getSuit().ordinal();
        return comp;
    }

}
