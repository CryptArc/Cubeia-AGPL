package com.cubeia.poker.variant.telesina;

import java.util.Random;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.IndexCardIdGenerator;
import com.cubeia.poker.hand.Shuffler;

public class TelesinaDeckFactory {

    TelesinaDeck createNewDeck(Random rng, int tableSize) {
        return new TelesinaDeck(new Shuffler<Card>(rng), new IndexCardIdGenerator(), tableSize);
    }

}
