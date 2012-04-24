package com.cubeia.poker.hand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ExposeCardsHolder {
	
	private Map<Integer, Collection<Card>> allCards = new HashMap<Integer, Collection<Card>>();
	
	public void setExposedCards(int playerId, Collection<Card> cards) {
		allCards.put(playerId, cards);
	}

	public boolean hasCards() {
		return allCards.size() > 0;
	}

	public Set<Integer> getPlayerIdSet() {
		return allCards.keySet();
	}

	public Collection<Card> getCardsForPlayer(Integer playerId) {
		return allCards.get(playerId);
	}
	
}
