/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.variant.turkish;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Shuffler;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TurkishDeckUtilTest {

    private static Shuffler<Card> SHUFFLER = new Shuffler<Card>(new Random());

    private TurkishDeckUtil turkishDeckUtil = new TurkishDeckUtil();

    @Test
    public void checkDeckSize() {
        assertThat(turkishDeckUtil.createDeckCards(2).size(), is(24));
        assertThat(turkishDeckUtil.createDeckCards(3).size(), is(28));
        assertThat(turkishDeckUtil.createDeckCards(4).size(), is(32));
        assertThat(turkishDeckUtil.createDeckCards(5).size(), is(36));
    }

    @Test
    public void calculateLowestRank() {
        assertThat(turkishDeckUtil.calculateLowestRank(2), is(Rank.NINE));
        assertThat(turkishDeckUtil.calculateLowestRank(3), is(Rank.EIGHT));
        assertThat(turkishDeckUtil.calculateLowestRank(4), is(Rank.SEVEN));
        assertThat(turkishDeckUtil.calculateLowestRank(5), is(Rank.SIX));
    }

    @Test
    public void createDeckCards() {
        List<Card> cards = turkishDeckUtil.createDeckCards(2);
        List<Card> cardsFromString = createCardsFromString("9C, 9D, 9H, 9S, " +
                "TC, TD, TH, TS, JC, JD, JH, JS, QC, QD, QH, QS, KC, KD, KH, KS, AC, AD, AH, AS");
        assertThat(cards, is(cardsFromString));
        assertThat(cards.size(), is(24));

        cards = turkishDeckUtil.createDeckCards(3);
        cardsFromString = createCardsFromString("8C, 8D, 8H, 8S, " +
                "9C, 9D, 9H, 9S, TC, TD, TH, TS, JC, JD, JH, JS, QC, QD, QH, QS, KC, KD, KH, KS, AC, AD, AH, AS");
        assertThat(cards, is(cardsFromString));
        assertThat(cards.size(), is(28));

        cards = turkishDeckUtil.createDeckCards(4);
        cardsFromString = createCardsFromString("7C, 7D, 7H, 7S, 8C, 8D, 8H, 8S, 9C, 9D, 9H, 9S, TC, TD, TH, TS, JC, JD, JH, JS, " +
                "QC, QD, QH, QS, KC, KD, KH, KS, AC, AD, AH, AS");
        assertThat(cards, is(cardsFromString));
        assertThat(cards.size(), is(32));

        cards = turkishDeckUtil.createDeckCards(5);
        cardsFromString = createCardsFromString("6C, 6D, 6H, 6S, 7C, 7D, 7H, 7S, 8C, 8D, 8H, 8S, 9C, 9D, 9H, 9S, TC, TD, TH, TS, JC, JD, JH, JS, " +
                "QC, QD, QH, QS, KC, KD, KH, KS, AC, AD, AH, AS");
        assertThat(cards, is(cardsFromString));
        assertThat(cards.size(), is(36));
    }

    private List<Card> createCardsFromString(String str) {
        String[] cardStrings = str.split("[,\\. ]+");
        ArrayList<Card> cards = new ArrayList<Card>();
        for (String cardString : cardStrings) {
            cards.add(new Card(cardString));
        }
        return cards;
    }

    @Test
    public void checkFromString() {
        for (int i = 0; i < 100; i++) {
            createFromStringAndCompareDecks(2, generateShuffleDeck(2));
        }

        for (int i = 0; i < 100; i++) {
            createFromStringAndCompareDecks(3, generateShuffleDeck(3));
        }
        
        for (int i = 0; i < 100; i++) {
            createFromStringAndCompareDecks(4, generateShuffleDeck(4));
        }
        for (int i = 0; i < 100; i++) {
            createFromStringAndCompareDecks(5, generateShuffleDeck(5));
        }
    }

    public String generateShuffleDeck(int participants) {
        List<Card> orderderCards = turkishDeckUtil.createDeckCards(participants);
        List<Card> shuffledCard = SHUFFLER.shuffle(orderderCards);

        StringBuilder shuffledCardBuffer = new StringBuilder();
        for (Card card : shuffledCard) {
            shuffledCardBuffer.append(card.toString());
        }

        return shuffledCardBuffer.toString();
    }

    public void createFromStringAndCompareDecks(int participants, String deck) {
        List<Card> cards = turkishDeckUtil.createRiggedDeck(participants, deck);
        StringBuilder cardBuffer = new StringBuilder();
        for (Card card : cards) {
            cardBuffer.append(card.toString());
        }
        String readDeck = cardBuffer.toString();
        if (!deck.equals(readDeck)) {
            System.out.println("gen  deck is: " + deck);
            System.out.println("read deck is: " + readDeck);
        }
        Assert.assertEquals(deck, readDeck);
    }
}
