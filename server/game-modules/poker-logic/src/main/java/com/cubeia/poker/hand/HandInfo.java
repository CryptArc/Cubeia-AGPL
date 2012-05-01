package com.cubeia.poker.hand;

import java.util.List;

public interface HandInfo {

    HandType getHandType();

    List<Card> getCards();
}
