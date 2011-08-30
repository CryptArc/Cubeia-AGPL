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

package com.cubeia.games.poker.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import se.jadestone.dicearena.game.telesina.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.telesina.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.telesina.network.protocol.Enums;
import se.jadestone.dicearena.game.telesina.network.protocol.HandEnd;
import ca.ualberta.cs.poker.Card;
import ca.ualberta.cs.poker.Hand;

import com.cubeia.poker.model.PlayerHands;

public class ActionTransformerTest extends TestCase {

	public void testCreateHandEndPacket() {
		Hand hand1 = new Hand("As Ks");
		Hand hand2 = new Hand("Td Tc");
		
		Hand community = new Hand("Qs Js Ts 4d 2c");
		hand1.addCards(community.getCards());
		hand2.addCards(community.getCards());
		
		Map<Integer, Hand> hands = new HashMap<Integer, Hand>();
		hands.put(11, hand1);
		hands.put(22, hand2);
		
		
		PlayerHands playerHands = new PlayerHands(hands);
		
		HandEnd end = ActionTransformer.createHandEndPacket(playerHands);
		
		assertEquals(2, end.hands.size());
		assertNotSame("Two High", end.hands.get(0).name);
		assertNotSame("Two High", end.hands.get(1).name);
	}

	public void testCreatePrivateVisibleCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.add(new Card("AS"));
		cards.add(new Card("AS"));
		DealPrivateCards privateCards = ActionTransformer.createPrivateCardsPacket(1, cards, false);
		assertEquals(2, privateCards.cards.size());
		CardToDeal dealtCard = privateCards.cards.get(0);
		assertEquals(1, dealtCard.player);
		assertEquals(Enums.Rank.ACE, dealtCard.card.rank);
		assertEquals(Enums.Suit.SPADES, dealtCard.card.suit);
	}
	
	public void testCreatePrivateHiddenCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.add(new Card("AS"));
		cards.add(new Card("AH"));
		DealPrivateCards privateCards = ActionTransformer.createPrivateCardsPacket(1, cards, true);
		assertEquals(2, privateCards.cards.size());
		CardToDeal dealtCard = privateCards.cards.get(0);
		assertEquals(1, dealtCard.player);
		assertEquals(Enums.Rank.HIDDEN, dealtCard.card.rank);
		assertEquals(Enums.Suit.HIDDEN, dealtCard.card.suit);
		dealtCard = privateCards.cards.get(1);
		assertEquals(1, dealtCard.player);
		assertEquals(Enums.Rank.HIDDEN, dealtCard.card.rank);
		assertEquals(Enums.Suit.HIDDEN, dealtCard.card.suit);
	}
}
