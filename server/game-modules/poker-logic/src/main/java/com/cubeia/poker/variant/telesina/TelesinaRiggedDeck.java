package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.hand.Card;

import java.util.List;
import java.util.Random;

/**
 * Rigged Telesina deck. The size of the deck will vary depending on the
 * number of participants.
 *
 * @author w
 */
public class TelesinaRiggedDeck extends TelesinaDeck {
    private static final long serialVersionUID = -5030565526818602010L;

    @SuppressWarnings("serial")
    private static final Random ALWAYS_ZERO_RNG = new Random() {
        protected int next(int bits) {
            return 0;
        }

        ;
    };

    public TelesinaRiggedDeck(TelesinaDeckUtil telesinaDeckUtil, int numberOfParticipants, String riggedDeckString) {
        super(telesinaDeckUtil, ALWAYS_ZERO_RNG, numberOfParticipants);
        List<Card> riggedCards = telesinaDeckUtil.createRiggedDeck(numberOfParticipants, riggedDeckString);
        resetCards(riggedCards);
    }

}
