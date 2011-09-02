package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.Rank.KING;
import static com.cubeia.poker.hand.Rank.QUEEN;
import static com.cubeia.poker.hand.Suit.CLUBS;
import static com.cubeia.poker.hand.Suit.DIAMONDS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 10);
        
        List<Rank> ranks = deck.calculateRanksToUse(2);
        assertThat(ranks.get(0), is(Rank.NINE));
        assertThat(ranks.size(), is(6));

        ranks = deck.calculateRanksToUse(3);
        assertThat(ranks.get(0), is(Rank.EIGHT));
        assertThat(ranks.size(), is(7));
        
        ranks = deck.calculateRanksToUse(4);
        assertThat(ranks.get(0), is(Rank.SEVEN));
        assertThat(ranks.size(), is(8));
        
        ranks = deck.calculateRanksToUse(5);
        assertThat(ranks.get(0), is(Rank.SIX));
        assertThat(ranks.size(), is(9));
        
        ranks = deck.calculateRanksToUse(6);
        assertThat(ranks.get(0), is(Rank.FIVE));
        assertThat(ranks.size(), is(10));
        
        ranks = deck.calculateRanksToUse(7);
        assertThat(ranks.get(0), is(Rank.FOUR));
        assertThat(ranks.size(), is(11));
        
        ranks = deck.calculateRanksToUse(10);
        assertThat(ranks.get(0), is(Rank.TWO));
        assertThat(ranks.size(), is(Rank.values().length));
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDeck() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 4);
        
        List<Card> cards = deck.createDeck(4);
        assertThat(cards, notNullValue());
        assertThat(cards.size(), is(32));
        assertThat(new HashSet<Card>(cards).size(), is(32));
        
        cards = deck.createDeck(10);
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
