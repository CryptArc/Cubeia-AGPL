package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PlayerCardsDealt extends HandHistoryEvent {

	private final int playerId;
	private final List<GameCard> cards = new LinkedList<GameCard>();
	private final boolean isExposed;
	
	public PlayerCardsDealt(int playerId, boolean isExposed) {
		this.playerId = playerId;
		this.isExposed = isExposed;
	}

	public boolean isExposed() {
		return isExposed;
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public List<GameCard> getCards() {
		return cards;
	}
}
