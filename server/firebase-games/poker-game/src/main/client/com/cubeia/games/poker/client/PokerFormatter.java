package com.cubeia.games.poker.client;

import java.util.List;

import com.cubeia.games.poker.io.protocol.GameCard;

public class PokerFormatter {

	public String format(List<GameCard> cards) {
		String result = "";
		for (GameCard card : cards) {
			result += format(card)+", "; 
		}
		return result;
	}

	public String format(GameCard card) {
		return card.rank + " of " +card.suit;
	}
	

}
