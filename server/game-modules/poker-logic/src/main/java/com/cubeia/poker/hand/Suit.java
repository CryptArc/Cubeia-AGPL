package com.cubeia.poker.hand;

/**
 * Suits.
 * The order of the enums are ascending rank according to: http://www.pagat.com/poker/rules/ranking.html#suit
 *
 * @author w
 */
public enum Suit {

    CLUBS(1),
    DIAMONDS(2),
    HEARTS(3),
    SPADES(0);

    public final int telesinaSuitValue;

    private Suit(int telesinaSuitValue) {
        this.telesinaSuitValue = telesinaSuitValue;
    }

    public String toShortString() {
        return name().substring(0, 1);
    }

    public static Suit fromShortString(char suit) {
        switch (suit) {
            case 'h':
                return HEARTS;
            case 'd':
                return DIAMONDS;
            case 's':
                return SPADES;
            case 'c':
                return CLUBS;
            case 'H':
                return HEARTS;
            case 'D':
                return DIAMONDS;
            case 'S':
                return SPADES;
            case 'C':
                return CLUBS;
            default:
                throw new IllegalArgumentException("Invalid enum value for Suit: " + suit);
        }
    }

}
