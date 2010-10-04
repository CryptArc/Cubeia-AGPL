package com.cubeia.poker.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.poker.model.PlayerHands;
import com.cubeia.poker.player.PokerPlayer;
import com.google.inject.internal.ToStringBuilder;

/**
 * The result of a hand. This class maps the player to the resulting win/lose amount of the hand.
 */
public class HandResult implements Serializable {

	private static final long serialVersionUID = -7802386310901901021L;

	private Map<PokerPlayer, Result> results = new HashMap<PokerPlayer, Result>();
	
	private PlayerHands playerHands;
	
	public PlayerHands getPlayerHands() {
		return playerHands;
	}

	public void setPlayerHands(PlayerHands playerHands) {
		this.playerHands = playerHands;
	}
	
	public Map<PokerPlayer, Result> getResults() {
		return results;
	}

	public void setResults(Map<PokerPlayer, Result> results) {
		this.results = results;
	}
	
	public String toString() {
		return "HandResult results["+results+"] playerHands["+playerHands+"]";
	}
}
