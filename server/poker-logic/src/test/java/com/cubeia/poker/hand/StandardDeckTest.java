package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.Rank.KING;
import static com.cubeia.poker.hand.Rank.QUEEN;
import static com.cubeia.poker.hand.Suit.CLUBS;
import static com.cubeia.poker.hand.Suit.DIAMONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class StandardDeckTest {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testConstruction() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        new StandardDeck(shuffler);
        
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(shuffler).shuffle(listCaptor.capture());
        assertThat(listCaptor.getValue().size(), is(52));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDeck() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        StandardDeck deck = new StandardDeck(shuffler);
        
        List<Card> cards = deck.createDeck();
        assertThat(cards, notNullValue());
        assertThat(cards.size(), is(52));
        assertThat(new HashSet<Card>(cards).size(), is(52));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void dealCard() {
        Shuffler<Card> shuffler = mock(Shuffler.class);
        Card card1 = new Card(KING, CLUBS);
        Card card2 = new Card(QUEEN, DIAMONDS);
        when(shuffler.shuffle(Mockito.anyList())).thenReturn(Arrays.asList(card1, card2));
        
        StandardDeck deck = new StandardDeck(shuffler);
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
        when(shuffler.shuffle(Mockito.anyList())).thenReturn(Collections.<Card>emptyList());
        
        StandardDeck deck = new StandardDeck(shuffler);
        assertThat(deck.isEmpty(), is(true));
        deck.deal();
    }

}
