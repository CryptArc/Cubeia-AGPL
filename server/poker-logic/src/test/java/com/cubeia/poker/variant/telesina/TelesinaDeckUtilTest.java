package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Shuffler;
import java.util.List;
import java.util.Random;
import junit.framework.Assert;

import org.junit.Test;

public class TelesinaDeckUtilTest {
    
    private static Shuffler<Card> SHUFFLER = new Shuffler<Card>(new Random());

	@Test
	public void checkDeckSize() {
		Assert.assertEquals(32, TelesinaDeckUtil.createDeckCards(4).size());
		Assert.assertEquals(36, TelesinaDeckUtil.createDeckCards(5).size());
		Assert.assertEquals(40, TelesinaDeckUtil.createDeckCards(6).size());
		Assert.assertEquals(52, TelesinaDeckUtil.createDeckCards(10).size());
	}
    
    @Test
    public void checkFromString(){
        for(int i = 0; i < 100; i++){
            createFromStringAndCompareDecks(4, generateShuffleDeck(4));
        }
        
        for(int i = 0; i < 100; i++){
            createFromStringAndCompareDecks(6, generateShuffleDeck(6));
        }
    }
    
    public String generateShuffleDeck(int participants){
        List<Card> orderderCards = TelesinaDeckUtil.createDeckCards(participants);
        List<Card> shuffledCard = SHUFFLER.shuffle(orderderCards);
        
        StringBuilder shuffledCardBuffer = new StringBuilder();
        for(Card card : shuffledCard){
            shuffledCardBuffer.append(card.toString());
        }
       
        return shuffledCardBuffer.toString();
    }
    
    public void createFromStringAndCompareDecks(int participants, String deck){
        List<Card> cards = TelesinaDeckUtil.createRiggedDeck(participants, deck);
        StringBuilder cardBuffer = new StringBuilder();
        for(Card card : cards){
            cardBuffer.append(card.toString());
        }
        String readDeck = cardBuffer.toString();
        if(!deck.equals(readDeck)){
            System.out.println("gen  deck is: "+deck);
            System.out.println("read deck is: "+readDeck);
        }
        Assert.assertEquals(deck, readDeck);
    }
}
