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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import se.jadestone.dicearena.game.poker.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.poker.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.ActionType;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.PlayerHand;

public class ActionTransformerTest extends TestCase {

	public void testCreateHandEndPacket() {
		Hand hand1 = new Hand("As Ks");
		Hand hand2 = new Hand("Td Tc");
		
		Hand community = new Hand("Qs Js Ts 4d 2c");
		hand1.addCards(community.getCards());
		hand2.addCards(community.getCards());
		
		List<PlayerHand> hands = new ArrayList<PlayerHand>();
		hands.add(new PlayerHand(11, hand1));
		hands.add(new PlayerHand(22, hand2));
		
		HandEnd end = ActionTransformer.createHandEndPacket(hands);
		
		assertEquals(2, end.hands.size());
		assertNotSame("Two High", end.hands.get(0).name);
		assertNotSame("Two High", end.hands.get(1).name);
	}

	public void testCreatePrivateVisibleCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.add(new Card(0, "AS"));
		cards.add(new Card(1, "AS"));
		DealPrivateCards privateCards = ActionTransformer.createPrivateCardsPacket(1, cards, false);
		assertEquals(2, privateCards.cards.size());
		CardToDeal dealtCard = privateCards.cards.get(0);
		assertEquals(1, dealtCard.player);
		assertEquals(Enums.Rank.ACE, dealtCard.card.rank);
		assertEquals(Enums.Suit.SPADES, dealtCard.card.suit);
	}
	
	public void testCreatePrivateHiddenCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.add(new Card(0, "AS"));
		cards.add(new Card(1, "AH"));
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
	
	public void testTransformActionTypeToPokerActionType() {
	    assertThat("wrong number of action types, something broken?", ActionType.values().length, is(9));
        assertThat(ActionTransformer.transform(ActionType.FOLD), is(PokerActionType.FOLD));
        assertThat(ActionTransformer.transform(ActionType.CHECK), is(PokerActionType.CHECK));
        assertThat(ActionTransformer.transform(ActionType.CALL), is(PokerActionType.CALL));
        assertThat(ActionTransformer.transform(ActionType.BET), is(PokerActionType.BET));
        assertThat(ActionTransformer.transform(ActionType.BIG_BLIND), is(PokerActionType.BIG_BLIND));
        assertThat(ActionTransformer.transform(ActionType.SMALL_BLIND), is(PokerActionType.SMALL_BLIND));
        assertThat(ActionTransformer.transform(ActionType.RAISE), is(PokerActionType.RAISE));
        assertThat(ActionTransformer.transform(ActionType.DECLINE_ENTRY_BET), is(PokerActionType.DECLINE_ENTRY_BET));
        assertThat(ActionTransformer.transform(ActionType.ANTE), is(PokerActionType.ANTE));
	}
	
}
