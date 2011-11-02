package com.cubeia.poker.hand;

import static com.cubeia.poker.hand.Rank.KING;
import static com.cubeia.poker.hand.Rank.QUEEN;
import static com.cubeia.poker.hand.Suit.CLUBS;
import static com.cubeia.poker.hand.Suit.DIAMONDS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cubeia.poker.variant.telesina.TelesinaDeck;

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
    	Answer<?> answer = new Answer<List<Card>>() {
        	
        	@Override
        	public List<Card> answer(InvocationOnMock invocation) throws Throwable {
        		return (List<Card>) invocation.getArguments()[0];
        	}
		};
        Shuffler<Card> shuffler = mock(Shuffler.class);
        when(shuffler.shuffle(anyList())).thenAnswer(answer);
        CardIdGenerator idGenerator = mock(CardIdGenerator.class);
        when(idGenerator.copyAndAssignIds(anyList())).thenAnswer(answer);
        
        TelesinaDeck deck = new TelesinaDeck(shuffler, idGenerator, 2);
        assertThat(deck.getDeckLowestRank(), is(Rank.NINE));
        assertThat(deck.getAllCards().size(), is(6 * 4));

        deck = new TelesinaDeck(shuffler, idGenerator, 3);
        assertThat(deck.getDeckLowestRank(), is(Rank.EIGHT));
        assertThat(deck.getAllCards().size(), is(7 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 4);
        assertThat(deck.getDeckLowestRank(), is(Rank.SEVEN));
        assertThat(deck.getAllCards().size(), is(8 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 5);
        assertThat(deck.getDeckLowestRank(), is(Rank.SIX));
        assertThat(deck.getAllCards().size(), is(9 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 6);
        assertThat(deck.getDeckLowestRank(), is(Rank.FIVE));
        assertThat(deck.getAllCards().size(), is(10 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 7);
        assertThat(deck.getDeckLowestRank(), is(Rank.FOUR));
        assertThat(deck.getAllCards().size(), is(11 * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 9);
        assertThat(deck.getDeckLowestRank(), is(Rank.TWO));
        assertThat(deck.getAllCards().size(), is(Rank.values().length * 4));
        
        deck = new TelesinaDeck(shuffler, idGenerator, 10);
        assertThat(deck.getDeckLowestRank(), is(Rank.TWO));
        assertThat(deck.getAllCards().size(), is(Rank.values().length * 4));
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
