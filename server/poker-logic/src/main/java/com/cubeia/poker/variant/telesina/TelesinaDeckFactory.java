package com.cubeia.poker.variant.telesina;

import java.util.Random;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.IndexCardIdGenerator;
import com.cubeia.poker.hand.Shuffler;

public class TelesinaDeckFactory {

    public TelesinaDeck createNewDeck(Random rng, int tableSize) {
        return new TelesinaDeck(new Shuffler<Card>(rng), new IndexCardIdGenerator(), tableSize);
    }
    
    //TODO: remove this code once GLI has used the rig deck feature
    public TelesinaDeck createNewRiggedDeck(Random rng, int tableSize, String riggedDeck) {
        return new TelesinaDeck(new Shuffler<Card>(rng), new IndexCardIdGenerator(), tableSize, riggedDeck);
    }

}
