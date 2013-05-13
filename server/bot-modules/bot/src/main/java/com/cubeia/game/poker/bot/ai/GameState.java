package com.cubeia.game.poker.bot.ai;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.poker.hand.Card;

public class GameState {
	
	private List<Card> privateCards = new ArrayList<Card>();
	
	private List<Card> communityCards = new ArrayList<Card>();
	
	public void clear() {
		privateCards.clear();
		communityCards.clear();
	}

	public void addPrivateCard(Card card) {
		privateCards.add(card);
	}
	
	public void addCommunityCard(Card card) {
		communityCards.add(card);
	}
	
	public List<Card> getPrivateCards() {
		return privateCards;
	}
	
	public List<Card> getCommunityCards() {
		return communityCards;
	}
}
