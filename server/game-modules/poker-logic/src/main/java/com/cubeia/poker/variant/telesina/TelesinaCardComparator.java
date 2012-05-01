package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.hand.Card;

import java.util.Collections;
import java.util.Comparator;

/**
 * Compares two cards telesina style. Note an ACE will always be better than any other
 * Rank card. So an ACE used as a low card in a straight will still compare as a high card
 * if compared to another card.
 * This comparator excludes the id of the card from the comparison.
 */
public class TelesinaCardComparator implements Comparator<Card> {
    public static final Comparator<Card> ASC = new TelesinaCardComparator();
    public static final Comparator<Card> DESC = Collections.reverseOrder(ASC);

    public TelesinaCardComparator() {
    }

    @Override
    public int compare(Card c1, Card c2) {
        if (c1.getRank() != c2.getRank()) {
            return c1.getRank().ordinal() - c2.getRank().ordinal();
        }

        return c1.getSuit().telesinaSuitValue - c2.getSuit().telesinaSuitValue;
    }
}