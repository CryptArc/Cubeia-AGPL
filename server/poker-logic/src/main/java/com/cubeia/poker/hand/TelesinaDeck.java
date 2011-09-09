package com.cubeia.poker.hand;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.cartesianProduct;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

/**
 * Telesina deck. The size of the deck will vary depending on the
 * number of participants.
 * 
 * @author w
 */
public class TelesinaDeck implements Deck {
    private static final long serialVersionUID = -5030565526818602010L;
    private List<Card> cards;
    private int currentCardIndex;
    
    private final Rank deckLowestRank;
    
    
    /**
     * Constructs a deck with a size calculated by the number of participants given.
     * The lowest card in the deck will be 11 - <number of participants>.
     * @param numberOfParticipants
     */
    public TelesinaDeck(Shuffler<Card> shuffler, CardIdGenerator idGenerator, int numberOfParticipants) {
        checkArgument(numberOfParticipants >= 2, "participants must be >= 2");
        checkArgument(numberOfParticipants <= 10, "participants must be <= 10");
        
    	int firstRankIndex = Math.max(0, 11 - numberOfParticipants - 2);
    	deckLowestRank = Rank.values()[firstRankIndex];
    	
        List<Card> vanillaCards = createDeck();
        List<Card> shuffledCards = shuffler.shuffle(vanillaCards);
        cards = idGenerator.copyAndAssignIds(shuffledCards);
    }
    
    @SuppressWarnings("unchecked")
    protected List<Card> createDeck() {

        ArrayList<Card> cards = new ArrayList<Card>();
        
        List<Rank> ranks = asList(Rank.values()).subList(deckLowestRank.ordinal(), Rank.values().length);
        for (List<Enum<?>> cardContainer : cartesianProduct(copyOf(Suit.values()), copyOf(ranks))) {
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

	public Rank getDeckLowestRank() {
		return deckLowestRank;
	}
}
