package com.cubeia.poker.hand;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.cartesianProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard 52-card deck.
 *
 * This implementation is not thread safe. 
 * 
 * @author w
 */
public class StandardDeck implements Deck {
    private static final long serialVersionUID = -3518540450503808264L;
    
    private List<Card> cards;
    private int currentCardIndex = 0;

    public StandardDeck(Shuffler<Card> shuffler, CardIdGenerator idGenerator) {
        List<Card> vanillaDeck = createDeck();
        List<Card> shuffledDeck = shuffler.shuffle(vanillaDeck);
        this.cards = idGenerator.copyAndAssignIds(shuffledDeck);
    }
    
    @SuppressWarnings("unchecked")
    protected List<Card> createDeck() {
        ArrayList<Card> cards = new ArrayList<Card>();
        
        for (List<Enum<?>> cardContainer : cartesianProduct(copyOf(Suit.values()), copyOf(Rank.values()))) {
            Suit suit = (Suit) cardContainer.get(0);
            Rank rank = (Rank) cardContainer.get(1);
            cards.add(new Card(rank, suit));
        }
        
        return cards;
    }

    @Override
    public Card deal() {
        if (isEmpty()) {
            throw new IllegalStateException("no more cards in deck");
        }
        
        return cards.get(currentCardIndex++);
    }

    @Override
    public boolean isEmpty() {
        return currentCardIndex >= cards.size();
    }

    @Override
    public List<Card> getAllCards() {
        return new ArrayList<Card>(cards);
    }
}
