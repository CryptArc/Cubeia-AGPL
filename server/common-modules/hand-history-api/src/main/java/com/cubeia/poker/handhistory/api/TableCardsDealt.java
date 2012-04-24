package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class TableCardsDealt extends HandHistoryEvent {

	private final List<GameCard> cards = new LinkedList<GameCard>();
	
	public TableCardsDealt() { }
	
	public List<GameCard> getCards() {
		return cards;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cards == null) ? 0 : cards.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableCardsDealt other = (TableCardsDealt) obj;
		if (cards == null) {
			if (other.cards != null)
				return false;
		} else if (!cards.equals(other.cards))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TableCardsDealt [cards=" + cards + "]";
	}
}
