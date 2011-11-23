package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.variant.telesina.TelesinaDeckUtil.calculateLowestRank;
import static com.cubeia.poker.variant.telesina.TelesinaDeckUtil.createDeckCards;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.CardIdGenerator;
import com.cubeia.poker.hand.Deck;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Shuffler;

/**
 * Telesina deck. The size of the deck will vary depending on the
 * number of participants.
 * 
 * @author w
 */
public class TelesinaDeck implements Deck {

    private static final long serialVersionUID = -5030565526818602010L;
    private final List<Card> cards;
    private int currentCardIndex;
    private final int deckSize;
    
    private final Rank deckLowestRank;
    
    
    /**
     * Constructs a deck with a size calculated by the number of participants given.
     * The lowest card in the deck will be 11 - <number of participants>.
     * @param numberOfParticipants
     */
    public TelesinaDeck(Shuffler<Card> shuffler, CardIdGenerator idGenerator, int numberOfParticipants) {
        checkArgument(numberOfParticipants >= 2, "participants must be >= 2");
        checkArgument(numberOfParticipants <= 10, "participants must be <= 10");
    	deckLowestRank = calculateLowestRank(numberOfParticipants);
        List<Card> vanillaCards = createDeckCards(deckLowestRank);
        deckSize = vanillaCards.size();
        List<Card> shuffledCards = shuffler.shuffle(vanillaCards);
        cards = idGenerator.copyAndAssignIds(shuffledCards);
    }
    
    public int getTotalNumberOfCardsInDeck() {
        return deckSize;
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
