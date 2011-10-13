package com.cubeia.poker.handhistory.api;

import java.util.LinkedList;
import java.util.List;

public class PlayerCardsExposed extends HandHistoryEvent {

	private final int playerId;
	private final List<GameCard> cards = new LinkedList<GameCard>();
	
	public PlayerCardsExposed(int playerId) {
		this.playerId = playerId;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public List<GameCard> getCards() {
		return cards;
	}
}
