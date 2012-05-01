package com.cubeia.poker.variant.telesina;

import java.util.Random;

public class TelesinaDeckFactory {

    public TelesinaDeck createNewDeck(Random rng, int tableSize) {
        return new TelesinaDeck(new TelesinaDeckUtil(), rng, tableSize);
    }

    //TODO: remove this code once GLI has used the rig deck feature
    public TelesinaDeck createNewRiggedDeck(int tableSize, String riggedDeck) {
        return new TelesinaRiggedDeck(new TelesinaDeckUtil(), tableSize, riggedDeck);
    }

}
