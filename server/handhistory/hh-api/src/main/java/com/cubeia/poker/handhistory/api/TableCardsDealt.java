package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class TableCardsDealt extends HandHistoryEvent {

	private final List<GameCard> cards = new LinkedList<GameCard>();
	
	public TableCardsDealt() { }
	
	public List<GameCard> getCards() {
		return cards;
	}
}
