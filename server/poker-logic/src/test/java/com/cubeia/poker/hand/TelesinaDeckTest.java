package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.Rank.*;
import static com.cubeia.poker.hand.Suit.*;
import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class TelesinaDeckTest {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testConstruction() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        new TelesinaDeck(shuffler, idGenerator, 6);
        
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(shuffler).shuffle(listCaptor.capture());
        verify(idGenerator).copyAndAssignIds(Mockito.anyList());
        assertThat(listCaptor.getValue().size(), is(40));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void calculateRanksToUse() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 2);
        assertThat(deck.getDeckLowestRank(), is(Rank.NINE));
        assertThat(deck.createDeck().size(), is(6 * 4));

        deck = new TelesinaDeck(shuffler, idGenerator, 3);
        assertThat(deck.getDeckLowestRank(), is(Rank.EIGHT));
        assertThat(deck.createDeck().size(), is(7 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 4);
        assertThat(deck.getDeckLowestRank(), is(Rank.SEVEN));
        assertThat(deck.createDeck().size(), is(8 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 5);
        assertThat(deck.getDeckLowestRank(), is(Rank.SIX));
        assertThat(deck.createDeck().size(), is(9 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 6);
        assertThat(deck.getDeckLowestRank(), is(Rank.FIVE));
        assertThat(deck.createDeck().size(), is(10 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 7);
        assertThat(deck.getDeckLowestRank(), is(Rank.FOUR));
        assertThat(deck.createDeck().size(), is(11 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 9);
        assertThat(deck.getDeckLowestRank(), is(Rank.TWO));
        assertThat(deck.createDeck().size(), is(Rank.values().length * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 10);
        assertThat(deck.getDeckLowestRank(), is(Rank.TWO));
        assertThat(deck.createDeck().size(), is(Rank.values().length * 4));
    }

    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetLowestRank() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 2);
        assertThat(deck.getDeckLowestRank(), is(Rank.NINE));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 4);
        assertThat(deck.getDeckLowestRank(), is(Rank.SEVEN));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 8);
        assertThat(deck.getDeckLowestRank(), is(Rank.THREE));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetTotalNumberOfCardsInDeck() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 2);
        assertThat(deck.getTotalNumberOfCardsInDeck(), is(6 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 4);
        assertThat(deck.getTotalNumberOfCardsInDeck(), is(8 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 9);
        assertThat(deck.getTotalNumberOfCardsInDeck(), is(13 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 10);
        assertThat(deck.getTotalNumberOfCardsInDeck(), is(13 * 4));
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDeck() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 4);
        
        List<Card> cards = deck.createDeck();
        assertThat(cards, notNullValue());
        assertThat(cards.size(), is(32));
        assertThat(new HashSet<Card>(cards).size(), is(32));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 10);
        cards = deck.createDeck();
        assertThat(cards, notNullValue());
        assertThat(cards.size(), is(52));
        assertThat(new HashSet<Card>(cards).size(), is(52));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void dealCard() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        Card card1 = new Card(0, KING, CLUBS);
        Card card2 = new Card(1, QUEEN, DIAMONDS);
        when(idGenerator.copyAndAssignIds(Mockito.anyList())).thenReturn(asList(card1, card2));
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 4);
        
        verify(shuffler).shuffle(Mockito.anyList());
        assertThat(deck.isEmpty(), is(false));
        assertThat(deck.deal(), is(card1));
        assertThat(deck.isEmpty(), is(false));
        assertThat(deck.deal(), is(card2));
        assertThat(deck.isEmpty(), is(true));
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalStateException.class)
    public void dealCardExceptionIfEmpty() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        when(shuffler.shuffle(Mockito.anyList())).thenReturn(Collections.<Card>emptyList());
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 4);
        assertThat(deck.isEmpty(), is(true));
        deck.deal();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void getAllCards() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        Card card1 = new Card(1, KING, CLUBS);
        Card card2 = new Card(2, QUEEN, DIAMONDS);
        when(idGenerator.copyAndAssignIds(Mockito.anyList())).thenReturn(asList(card1, card2));
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 4);
        
        assertThat(deck.getAllCards().size(), is(2));
        deck.deal();
        assertThat(deck.getAllCards().size(), is(2));
    }

}
